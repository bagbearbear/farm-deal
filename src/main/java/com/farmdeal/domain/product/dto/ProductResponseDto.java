package com.farmdeal.domain.product.dto;

import com.farmdeal.domain.product.enums.ProductCategoryEnum;
import com.farmdeal.domain.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {

    private Long postId;
    private String title;
    private String nickname;
    private String content;
    private String imageUrl;
    private Integer likes;
    private Integer view;
    private ProductCategoryEnum category;
    private List<String> imgList;
    private String createdAt;
    private String modifiedAt;

    public ProductResponseDto(Long id, String nickname, String title, String name, String imageUrl, ProductCategoryEnum category, String createdAt, int view) {
    }


    public static ProductResponseDto fromEntity(
            Product product, List<String> imgList) {
        return ProductResponseDto.builder()
                .postId(product.getId())
                .title(product.getTitle())
                .content(product.getDescription())
                .nickname(product.getUser().getNickname())
                .imgList(imgList)
                .category(product.getCategory())
                .view(0)
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .build();
    }


    public static ProductResponseDto detailFromEntity(
            Product product, List<String> imgList) {
        return ProductResponseDto.builder()
                // 부모 클래스의 필드들은 super 키워드로 접근 가능
                .postId(product.getId())
                .title(product.getTitle())
                .content(product.getDescription())
                .nickname(product.getUser().getNickname())
                .imgList(imgList)
                .category(product.getCategory())
                .view(0)
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .build();
    }




    public static ProductResponseDto getAllFromEntity(
            Product product) {
        return ProductResponseDto.builder()
                .postId(product.getId())
                .title(product.getTitle())
                .imageUrl(product.getImageUrl())
                .content(product.getDescription())
                .view(product.getView())
                .category(product.getCategory())
                .nickname(product.getUser().getNickname())
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .build();
    }
}



