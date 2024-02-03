package com.farmdeal.domain.wishlist.controller;

import com.amazonaws.services.s3.internal.eventstreaming.Message;
import com.farmdeal.domain.wishlist.dto.WishlistResponseDto;
import com.farmdeal.domain.user.model.User;
import com.farmdeal.domain.wishlist.service.WishlistService;
import com.farmdeal.global.dto.ApiResponseDto;
import com.farmdeal.global.dto.MessageResponseDto;
import com.farmdeal.global.security.UserDetailsImpl;
import com.farmdeal.global.service.EntityValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/{productId}")
    public ApiResponseDto<WishlistResponseDto> addWishlist(@PathVariable Long productId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return wishlistService.addWishlist(productId, userDetails.getUser());
    }

    @DeleteMapping("/{wishlistId}")
    public ApiResponseDto<MessageResponseDto> deleteWishlist(@PathVariable Long wishlistId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return wishlistService.deleteWishlist(wishlistId, userDetails.getUser());
    }
}
