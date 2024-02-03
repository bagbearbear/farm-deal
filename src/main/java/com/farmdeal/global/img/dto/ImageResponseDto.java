package com.farmdeal.global.img.dto;

import com.farmdeal.domain.product.model.Product;
import com.farmdeal.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDto {

    private Long id;
    private String imageUrl;
    private Product product;
    private User user;
}
