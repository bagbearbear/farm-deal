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
public class UserResponseDto {

    private Long userId;

    private String nickname;

    private String imageUrl;

    private List<String> imgList;

    private String role;

    private String email;

    public static UserResponseDto createFromEntity(User user) {
        return UserResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .build();
    }

    public static UserResponseDto getFromEntity(User user) {
        return UserResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .role(String.valueOf(user.getRole()))
                .build();
    }

}
