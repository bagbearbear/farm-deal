package com.farmdeal.domain.user.dto;

import com.farmdeal.domain.order.model.Order;
import com.farmdeal.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MypageResponseDto{
    private Long orderId;
    private String imageUrl;
    private String productTitle;
    private String productName;
    private long productPrice;
    private String createdAt;



    public static MypageResponseDto allFromEntity(Order order) {
        return MypageResponseDto.builder()
                .orderId(order.getId())
                .imageUrl(order.getProduct().getImageUrl())
                .productTitle(order.getProduct().getTitle())
                .productName(order.getProduct().getName())
                .productPrice(order.getProduct().getPrice())
                .createdAt(order.getCreatedAt())
                .build();

    }

}