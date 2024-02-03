package com.farmdeal.domain.product.service;


import com.farmdeal.domain.product.enums.ProductCategoryEnum;
import com.farmdeal.domain.product.dto.ProductRequestDto;
import com.farmdeal.domain.product.dto.ProductResponseDto;
import com.farmdeal.domain.product.enums.ProductApprovalEnum;
import com.farmdeal.domain.product.model.Product;
import com.farmdeal.domain.product.repository.ProductRepository;
import com.farmdeal.domain.user.model.User;
import com.farmdeal.global.dto.ApiResponseDto;
import com.farmdeal.global.dto.MessageResponseDto;
import com.farmdeal.global.exception.CustomException;
import com.farmdeal.global.enums.ErrorCode;
import com.farmdeal.global.role.Authority;
import com.farmdeal.global.service.ImageUploaderService;
import com.farmdeal.global.service.EntityValidatorService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.farmdeal.global.enums.SuccessCode.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "상품등록 기능")
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageUploaderService imageUploaderService;
    private final EntityValidatorService entityValidatorService;

    /**
     * 상품 등록
     * @param productRequestDto
     * @param user
     * @param imgPaths
     * @return ApiResponseDto<PostResponseDto>
     */
    @Transactional
    public ApiResponseDto<ProductResponseDto> createProduct(ProductRequestDto productRequestDto,
                                                            User user, List<String> imgPaths) {
        // 카테고리 Business Validation
        validateCategory(productRequestDto.getCategory());
        // 판매자만 상품글 작성 가능
        if(!Authority.SELLER.equals(user.getRole())) {
            throw new CustomException(ErrorCode.NOT_SELLER_ERROR);
        }
        Product product = productFromRequest(productRequestDto, user);
        productRepository.save(product);

        List<String> imgList = imageUploaderService.addImage(imgPaths, product);
        product.saveImage(imgList.get(0));

        return ApiResponseDto.success(
                SUCCESS_POST_REGISTER.getMessage(),
                ProductResponseDto.fromEntity(product, imgList));
    }

    private Product productFromRequest(ProductRequestDto productRequestDto, User user) {
        return Product.builder()
                .user(user)
                .title(productRequestDto.getTitle())
                .name(productRequestDto.getName())
                .price(productRequestDto.getPrice())
                .description(productRequestDto.getDescription())
                .stock(productRequestDto.getStock())
                .category(productRequestDto.getCategory())
                .productApproval(ProductApprovalEnum.APPROVED) // 승인 대기 중
                .build();
    }


    /**
     * 게시글 단건 조회
     * @param postId
     * @return ApiResponseDto<PostResponseDto>
     */
    @Transactional// readOnly설정시 데이터가 Mapping되지 않는문제로 해제
    public ApiResponseDto<ProductResponseDto> getProduct(Long productId) {
        Product product = entityValidatorService.validateProduct(productId);

        // 단건 조회 조회수 증가
        viewCount(product);
        List<String> imgList = imageUploaderService.getListImage(product);

        return ApiResponseDto.success(
                SUCCESS_POST_GET_DETAIL.getMessage(),
                ProductResponseDto.detailFromEntity(
                        product, imgList)
        );
    }


    /**
     * 게시글 업데이트
     * @param productId
     * @param productRequestDto
     * @param user
     * @param imgPaths
     * @return ApiResponseDto<PostResponseDto>
     */
    @Transactional
    public ApiResponseDto<ProductResponseDto> updateProduct(Long productId,
                                                         ProductRequestDto productRequestDto,
                                                         User user,
                                                         List<String> imgPaths) {

        Product product = entityValidatorService.validateProduct(productId);
        validateProductUser(product, user);

        //저장된 이미지 리스트 가져오기
        List<String> newImgList = imageUploaderService.updateImage(imgPaths, product);


        product.update(productRequestDto);
        return ApiResponseDto.success(
                SUCCESS_POST_EDIT.getMessage(),
                ProductResponseDto.fromEntity(product, newImgList)
        );
    }

    /**
     * 게시글 삭제
     * @param productId
     * @param user
     * @return ApiResponseDto<MessageResponseDto>
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> deleteProduct(Long productId, User user) {

        Product product = entityValidatorService.validateProduct(productId);
        validateProductUser(product, user);

        productRepository.delete(product);
        List<String> imgList = imageUploaderService.getListImage(product);
        imageUploaderService.deleteImageList(product, imgList);
        return ApiResponseDto.success(SUCCESS_POST_DELETE.getMessage());
    }

    /**
     * 게시글 작성자와 로그인한 사용자가 일치하는지 확인
     * @param product
     * @param user
     */
    public void validateProductUser(@NotNull Product product, User user) {
        if (product.validateUser(user)) {
            throw new CustomException(ErrorCode.INVALID_USER_MATCH);
        }
    }

    /**
     * 카테고리 검증
     * @param category
     */
    public void validateCategory(ProductCategoryEnum category) {

        if (!ProductCategoryEnum.isValidCategory(category)) {
            log.error("[FAIL] {} 카테고리가 존재하지 않습니다.", category);
            throw new CustomException(ErrorCode.POST_CATEGORY_NOT_FOUND);
        }
    }

    /**
     * 조회수 증가
     * @param product
     */
    public void viewCount(Product product) {
        product.increasePostView();
    }



}
