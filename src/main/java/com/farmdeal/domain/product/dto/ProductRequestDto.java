package com.farmdeal.domain.product.dto;

import com.farmdeal.domain.product.enums.ProductCategoryEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
  @NotNull(message = "제목을 입력해주세요.")
  @Size(min = 5, max = 100, message = "제목은 100자 이내로 입력해주세요.")
  private String title;
  @NotNull(message = "상품명을 입력해주세요.")
  @Size(min = 1, max = 100, message = "상품명은 100자 이내로 입력해주세요.")
  private String name;
  @NotNull(message = "상품 설명을 입력해주세요.")
  @Size(min = 1, max = 1000, message = "상품 설명은 1000자 이내로 입력해주세요.")
  private String description;
  @NotNull(message = "상품 이미지를 최소 1개이상 등록해주세요")
  private List<String> imgList;
  @NotNull(message = "상품 카테고리를 선택해주세요.")
  private ProductCategoryEnum category;
  private String createdAt;
  @NotNull(message = "상품 가격을 입력해주세요.")
  @PositiveOrZero(message = "상품 가격은 0원 이상으로 입력해주세요.")
  private long price;
  @NotNull(message = "상품 재고를 입력해주세요.")
  @PositiveOrZero(message = "상품 재고는 0개 이상으로 입력해주세요.")
  private long stock;
  private String modifiedAt;


}
