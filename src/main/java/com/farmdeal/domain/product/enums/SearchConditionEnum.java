package com.farmdeal.domain.product.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchConditionEnum {

    PRODUCT_TITLE("title", "상품 게시글 제목"),
    PRODUCT_NAME("name", "상품명"),



    ;


    private final String column;

    private final String description;

}
