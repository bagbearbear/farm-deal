package com.farmdeal.domain.user.controller;

import com.farmdeal.domain.user.dto.MypageResponseDto;
import com.farmdeal.domain.user.service.MypageService;
import com.farmdeal.global.dto.ApiResponseDto;
import com.farmdeal.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "마이페이지 API", description = "마이페이지 조회")
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/mypage/{userId}")
    @Operation(summary = "마이페이지 조회", description = "마이페이지 조회")
    @Parameter(name = "userId", description = "조회할 회원의 id", required = true)
    @Parameter(name = "userDetails", description = "마이페이지를 조회할 사용자의 정보", required = true)
    public ApiResponseDto<Page<MypageResponseDto>> getMypage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                             int page, int size){

        return mypageService.getAllOrder(userDetails.getUser(), page, size);
    }

}
