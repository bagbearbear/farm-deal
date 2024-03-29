package com.farmdeal.domain.user.controller;

import com.farmdeal.domain.user.dto.SignupRequestDto;
import com.farmdeal.domain.user.dto.UserUpdateRequestDto;
import com.farmdeal.domain.user.dto.UserResponseDto;
import com.farmdeal.domain.user.service.UserService;
import com.farmdeal.global.dto.ApiResponseDto;
import com.farmdeal.global.dto.MessageResponseDto;
import com.farmdeal.global.external.AwsS3UploadService;
import com.farmdeal.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/")
@Tag(name = "회원 API", description = "회원 가입, 회원 정보 수정, 회원 탈퇴, 회원 조회")
public class UserController {

    private final UserService userService;
    private final AwsS3UploadService s3Service;

    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = "회원 가입")
    @Parameter(name = "signupRequestDto", description = "회원 가입 정보", required = false)
    @Parameter(name = "userInfoRequestDto", description = "회원 가입 정보", required = false)
    @Parameter(name = "multipartFiles", description = "회원 가입 정보", required = false)
    public ApiResponseDto<UserResponseDto> signup(@RequestPart(value = "info", required = false) @Valid final SignupRequestDto signupRequestDto,
                                                  @NotNull @RequestPart(value = "imageUrl", required = false) final MultipartFile multipartFiles) {
        String imgPaths = s3Service.uploadOne(multipartFiles);

        return userService.createUser(signupRequestDto, imgPaths);
    }

    // login Filter단에서 이루어지게 구현
    @PostMapping("/idCheck/{username}")
    @Operation(summary = "회원 아이디 중복 체크", description = "회원 아이디 중복 체크")
    @Parameter(name = "username", description = "중복 체크할 아이디", required = true)
    public ApiResponseDto<MessageResponseDto> checkUser(@PathVariable final String username) {

        return userService.checkUser(username);
    }

    @PostMapping("/nicknameCheck/{nickname}")
    @Operation(summary = "회원 닉네임 중복 체크", description = "회원 닉네임 중복 체크")
    @Parameter(name = "nickname", description = "중복 체크할 닉네임", required = true)
    public ApiResponseDto<MessageResponseDto> checkNickname(@PathVariable final String nickname) {

        return userService.checkNickname(nickname);
    }

    @PutMapping("/mypage/password")
    @Operation(summary = "회원 비밀번호 수정", description = "회원 비밀번호 수정")
    @Parameter(name = "userUpdateRequestDto", description = "회원 비밀번호 수정 정보", required = true)
    @Parameter(name = "userDetails", description = "회원 비밀번호를 수정할 사용자의 정보", required = true)
    public ApiResponseDto<MessageResponseDto> passwordUpdate(@RequestBody final UserUpdateRequestDto userUpdateRequestDto,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return userService.updatePassword(userUpdateRequestDto, userDetails.getUser());
    }

    @PutMapping( "/mypage/image")
    @Operation(summary = "회원 프로필 이미지 수정", description = "회원 프로필 이미지 수정")
    @Parameter(name = "multipartFiles", description = "회원 프로필 이미지 수정 정보")
    @Parameter(name = "userDetails", description = "회원 프로필 이미지를 수정할 사용자의 정보")
    public ApiResponseDto<MessageResponseDto> imageUpdate(@NotNull@RequestPart("imageUrl") final MultipartFile multipartFiles,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

        String imgPaths = s3Service.uploadOne(multipartFiles);
        return userService.updateImage(userDetails.getUser(), imgPaths);
    }


    @GetMapping( "/user/me")
    @Operation(summary = "회원 조회", description = "회원 조회")
    @Parameter(name = "userDetails", description = "조회할 회원의 정보", required = true)
    public ApiResponseDto<UserResponseDto> getUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        return userService.getUser(userDetails.getUser());
    }

    @PostMapping( "/user/logout")
    @Operation(summary = "로그아웃", description = "로그아웃")
    @Parameter(name = "request", description = "로그아웃 요청", required = true)
    public ApiResponseDto<MessageResponseDto> logout(HttpServletRequest request, HttpServletResponse response) {

        return userService.logout(request,response);
    }


    @PostMapping("/user/disabled")
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴")
    @Parameter(name = "userDetails", description = "탈퇴할 회원의 정보", required = true)
    public ApiResponseDto<MessageResponseDto> disableUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        return userService.disableUser(userDetails.getUser());
    }

}

