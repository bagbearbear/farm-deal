package com.farmdeal.domain.product.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductCategoryEnum {
ROOT("전체"),
    FRUIT("과일"),
    VEGETABLE("채소"),
    GRAIN("곡물"),
    MEAT("육류"),
    SEAFOOD("수산물"),
    OTHERS("기타");


    private final String description;


    public static boolean isValidCategory(ProductCategoryEnum category) {
        for (ProductCategoryEnum validCategory : values()) {
            if (validCategory.equals(category)) {
                return true;
            }
        }
        return false;
    }
}
