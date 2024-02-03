package com.farmdeal.domain.product.dto;

import com.farmdeal.domain.product.enums.ProductCategoryEnum;
import com.farmdeal.domain.product.enums.SearchConditionEnum;
import com.farmdeal.domain.product.enums.SortConditionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductSearchDto {
    // 검색 조건
    private SearchConditionEnum searchCondition;
    // 정렬 조건
    private SortConditionEnum sortCondition;
    // 상품 카테고리
    private ProductCategoryEnum productCategory;

    private String keyword;
}
