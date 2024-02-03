package com.farmdeal.domain.product.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortConditionEnum {
    LOW_PRICE("price DESC", "낮은 가격순"),
    HIGH_PRICE("price ASC", "높은 가격순"),
    RECENTLY_REGISTERED("created_at DESC", "최근 등록순"),
    PRODUCT_ID("product_id DESC", "상품번호순")
    ;

    private final String query;
    private final String description;
}
