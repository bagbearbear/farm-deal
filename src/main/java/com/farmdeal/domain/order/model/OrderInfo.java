package com.farmdeal.domain.order.model;

import com.farmdeal.domain.order.dto.OrderInfoUpdateRequestDto;
import com.farmdeal.domain.order.enums.PaymentEnum;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class OrderInfo {

    @Enumerated(EnumType.STRING)
    private PaymentEnum paymentEnum;

    private String orderAddress;
    private String orderPhone;
    private String orderMessage;


    public void updateOrderInfo(OrderInfoUpdateRequestDto orderInfoUpdateRequestDto) {
        this.paymentEnum = orderInfoUpdateRequestDto.getPaymentEnum();
        this.orderAddress = orderInfoUpdateRequestDto.getOrderAddress();
        this.orderPhone = orderInfoUpdateRequestDto.getOrderPhone();
        this.orderMessage = orderInfoUpdateRequestDto.getOrderMessage();
    }
}
