package com.farmdeal.domain.user.dto;


import com.farmdeal.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDto{

    private Long userId;

    private String gender;

    private String nickname;

    private String imageUrl;

    private List<String> imgList;

    private Integer age;

    private String mbti;

    private String introduction;

    private String idealType;

    private String job;

    private String hobby;

    private String drink;

    private String pet;

    private String smoke;

    private String likeMovieType;

    private String area;

    private Integer likes;

    private Integer unLike;

    public static ProfileResponseDto allFromEntity(User user) {
        return ProfileResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .age(user.getAge())
                .imageUrl(user.getImageUrl())
                .area(user.getArea())
                .build();
    }

    public static ProfileResponseDto detailFromEntity(User user) {
        return ProfileResponseDto.builder()
                .userId(user.getId())
                .imageUrl(user.getImageUrl())
                .nickname(user.getNickname())
                .age(user.getAge())
                .area(user.getArea())
                .build();
    }

}