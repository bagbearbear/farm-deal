package com.farmdeal.domain.wishlist.dto;

import com.farmdeal.domain.wishlist.model.Wishlist;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WishlistResponseDto {

    private Long wishlistId;
    private String productName;


    public static WishlistResponseDto fronEntity(Wishlist wishlist) {
        return WishlistResponseDto.builder()
                .wishlistId(wishlist.getId())
                .productName(wishlist.getProduct().getName())
                .build();
    }
}

