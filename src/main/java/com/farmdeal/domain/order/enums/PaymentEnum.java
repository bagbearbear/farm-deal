package com.farmdeal.domain.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PaymentEnum {

        CARD("카드"),
        CASH("현금");

        private String payment;
}
