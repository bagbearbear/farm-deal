package com.farmdeal.domain.admin.service;



import com.farmdeal.domain.order.enums.OrderStatusEnum;
import com.farmdeal.domain.order.model.Order;
import com.farmdeal.domain.product.enums.ProductApprovalEnum;
import com.farmdeal.domain.product.model.Product;
import com.farmdeal.domain.product.repository.ProductRepository;
import com.farmdeal.domain.user.model.User;
import com.farmdeal.global.dto.ApiResponseDto;
import com.farmdeal.global.dto.MessageResponseDto;
import com.farmdeal.global.enums.SuccessCode;
import com.farmdeal.global.exception.CustomException;
import com.farmdeal.global.enums.ErrorCode;
import com.farmdeal.global.service.ImageUploaderService;
import com.farmdeal.global.service.EntityValidatorService;
import com.farmdeal.global.role.Authority;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j(topic = "관리자 기능")
public class AdminService {

    private final ProductRepository productRepository;



    private final ImageUploaderService imageUploaderService;

    private final EntityValidatorService entityValidatorService;


    /**
     * 관리자 게시글 삭제
     * @return ApiResponseDto<MessageResponseDto>
     * @throws CustomException
     * 토큰을 통해 해당 토큰의 정보를 확인. 이때 해당 정보의 Role 설정이 Admin일 경우  게시글 삭제가 가능하도록 설정.
     * 반대로 해당 정보의 Role 설정이 User인 경우 게시글 삭제가 진행되지 않도록 에러메시지 전송.
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> deleteProductByAdmin(User user, Long productId) {


        Product product = entityValidatorService.validateProduct(productId);

        validationAdmin(user);
        productRepository.delete(product);
        List<String> imgList = imageUploaderService.getListImage(product);

        imageUploaderService.deleteImageList(product,imgList);

        return ApiResponseDto.success(
                SuccessCode.SUCCESS_ADMIN_COMMENT_DELETE.getMessage()
        );
    }

    // 상품 판매글 승인
    @Transactional
    public ApiResponseDto<MessageResponseDto> updateApprovalByAdmin(
            User user, Long productId, ProductApprovalEnum productApprovalEnum) {
        validationAdmin(user);
        Product product = entityValidatorService.validateProduct(productId);
        product.updateApprovalStatus(productApprovalEnum);

        return ApiResponseDto.success(
                SuccessCode.SUCCESS.getMessage());
    }

    public ApiResponseDto<MessageResponseDto> updateOrderStatusByAdmin(
            User user, Long productId, OrderStatusEnum orderStatus) {
        validationAdmin(user);
        Order order = entityValidatorService.validateOrder(productId);
        order.updateOrderStatus(orderStatus);

        return ApiResponseDto.success(
                SuccessCode.SUCCESS.getMessage());
    }

    public void validationAdmin(@NotNull User user) {
        if (!user.getRole().equals(Authority.ADMIN)) {
            log.error("{} 회원은 관리자가 아닙니다.", user);
            throw new CustomException(ErrorCode.NOT_ADMIN_ERROR);

        }
    }
}
