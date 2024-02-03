package com.farmdeal.domain.order.dto;

import com.farmdeal.domain.order.enums.PaymentEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderInfoUpdateRequestDto {

    @NotNull(message = "결제 방식을 선택해주세요.")
    private PaymentEnum paymentEnum;
    @NotNull(message = "주문자 주소를 입력해주세요.")
    @Size(max = 100, message = "주문자 주소는 100자 이내로 입력해주세요.")
    private String orderAddress;
    @NotNull(message = "주문자 연락처를 입력해주세요.")
    @Size(min = 1, max = 20, message = "주문자 연락처를 20자 이내로 입력해주세요.")
    private String orderPhone;
    @NotNull(message = "주문자 요청사항을 입력해주세요.")
    @Size(max = 100, message = "주문자 요청사항은 100자 이내로 입력해주세요.")
    private String orderMessage;
}
