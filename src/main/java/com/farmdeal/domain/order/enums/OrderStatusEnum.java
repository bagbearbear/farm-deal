package com.farmdeal.domain.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum OrderStatusEnum {
    PAYMENT_COMPLETED("결제완료"),
    DELIVERY_PREPARED("배송중"),
    DELIVERY_COMPLETED("배송완료"),
    ORDER_CANCELLED("주문취소"),
    ORDER_REGISTERED("주문등록");

    private String orderStatus;

}
