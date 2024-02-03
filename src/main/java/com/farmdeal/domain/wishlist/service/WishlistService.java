package com.farmdeal.domain.wishlist.service;

import com.farmdeal.domain.wishlist.dto.WishlistResponseDto;
import com.farmdeal.domain.wishlist.model.Wishlist;
import com.farmdeal.domain.wishlist.repository.WishlistRepository;
import com.farmdeal.domain.product.model.Product;
import com.farmdeal.domain.user.model.User;
import com.farmdeal.global.dto.ApiResponseDto;
import com.farmdeal.global.dto.MessageResponseDto;
import com.farmdeal.global.enums.ErrorCode;
import com.farmdeal.global.enums.SuccessCode;
import com.farmdeal.global.exception.CustomException;
import com.farmdeal.global.service.EntityValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Repository
@RequiredArgsConstructor
public class WishlistService {

    private final EntityValidatorService entityValidatorService;
    private final WishlistRepository wishlistRepository;

    @Transactional
    public ApiResponseDto<WishlistResponseDto> addWishlist(Long productId, User user) {
        Product product = entityValidatorService.validateProduct(productId);
        // 이미 찜한적 있는지 확인
        duplicateWishlist(user, product);
        // 찜 목록 저장
        Wishlist wishlist = createWishItem(product);
        wishlistRepository.save(wishlist);
        return ApiResponseDto.success(
                SuccessCode.SUCCESS.getMessage(), WishlistResponseDto.fronEntity(wishlist));
    }
    private Wishlist createWishItem(Product product) {
        return Wishlist.builder()
                .product(product)
                .build();
    }

    @Transactional
    public ApiResponseDto<MessageResponseDto> deleteWishlist(Long wishlistId, User user) {
        Wishlist wishlist = validateWishlist(wishlistId);
        // 해당 사용자의 찜 상품인지 확인
        validateWishUser(user, wishlist);
        // 찜목록에서 삭제
        wishlistRepository.delete(wishlist);
        return ApiResponseDto.success(
                SuccessCode.SUCCESS.getMessage()
        );
    }
    // 존재하는 찜한 상품인지 확인
    private Wishlist validateWishlist(Long wishlistId) {
        return wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_WISHLIST));
    }
    // 내가 이미 찜한 상품인지 확인
    public void duplicateWishlist(User user, Product product) {
        if(wishlistRepository.findByUserAndProductId(user, product.getId()).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_WISHLIST);
        }
    }
    public void validateWishUser(User user, Wishlist wishlist) {
        if(!wishlist.validateUser(user)) {
            throw new CustomException(ErrorCode.NOT_FOUND_WISHLIST);
        }
    }

}
