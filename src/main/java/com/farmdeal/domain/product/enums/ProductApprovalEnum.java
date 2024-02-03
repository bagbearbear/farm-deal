package com.farmdeal.domain.product.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductApprovalEnum {

    PENDING, // 승인 대기 중
    APPROVED, // 승인됨
    REJECTED  // 거절됨
    ;



    ;
}
