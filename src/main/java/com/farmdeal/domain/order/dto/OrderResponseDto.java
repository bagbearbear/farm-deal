package com.farmdeal.domain.order.dto;

import com.farmdeal.domain.order.model.Order;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponseDto {

    private String nickname;
    private Long productId;
    private String productName;
    private long productPrice;
    private long currentStock;
    private String createdAt;
    private String orderAddress;
    private String orderPhone;
    private String orderMessage;




    public static OrderResponseDto fromEntity(Order order, long stock) {
        return OrderResponseDto.builder()
                .nickname(order.getUser().getNickname())
                .productId(order.getProduct().getId())
                .productName(order.getProduct().getName())
                .productPrice(order.getProduct().getPrice())
                .currentStock(stock)
                .createdAt(order.getCreatedAt())
                .build();
    }

    public static OrderResponseDto getOrderInfo(Order order) {
        return OrderResponseDto.builder()
                .nickname(order.getUser().getNickname())
                .productId(order.getProduct().getId())
                .productName(order.getProduct().getName())
                .productPrice(order.getProduct().getPrice())
                .createdAt(order.getCreatedAt())
                .orderAddress(order.getOrderInfo().getOrderAddress())
                .orderPhone(order.getOrderInfo().getOrderPhone())
                .orderMessage(order.getOrderInfo().getOrderMessage())
                .build();
    }
}
