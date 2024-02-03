package com.farmdeal.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserGreadeEnum {
    PLATINUM("플래티넘", 300_000),
    GOLD("골드", 200_000),
    SILVER("실버", 100_000),
    // DEFAULT 값 WHITE("화이트", 0),
    WHITE("화이트", 0),
    ;

    private final String description;
    private final int totalOrderAmount;
}