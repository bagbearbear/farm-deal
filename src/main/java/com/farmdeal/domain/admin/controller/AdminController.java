package com.farmdeal.domain.admin.controller;



import com.farmdeal.domain.admin.service.AdminService;
import com.farmdeal.domain.order.enums.OrderStatusEnum;
import com.farmdeal.domain.product.enums.ProductApprovalEnum;
import com.farmdeal.global.dto.ApiResponseDto;
import com.farmdeal.global.dto.MessageResponseDto;
import com.farmdeal.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "관리자 API", description = "관리자 권한으로만 접근 가능한 API")
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "관리자 권한으로 상품 삭제", description = "관리자 권한으로 상품 삭제")
    @Parameter(name = "productId", description = "삭제할 게시글의 id", required = true)
    @Parameter(name = "userDetails", description = "관리자 권한을 가진 사용자의 정보", required = true)
    @DeleteMapping( "/admin/product/{productId}")
    public ApiResponseDto<MessageResponseDto> deleteProductByAdmin(@PathVariable final Long productId,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return adminService.deleteProductByAdmin(userDetails.getUser(), productId);
    }


    @Operation(summary = "관리자 권한으로 상품 승인", description = "관리자 권한으로 상품 승인")
    @Parameter(name = "productId", description = "승인할 게시글의 id", required = true)
    @Parameter(name = "ProductApprovalEnum", description = "승인 여부, APPROVED,REJECTED", required = true)
    @PostMapping("/admin/product/confirm/{productId}")
    public ApiResponseDto<MessageResponseDto> updateApprovalByAdmin(@PathVariable final Long productId,
            @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam ProductApprovalEnum approvalEnum) {

        return adminService.updateApprovalByAdmin(userDetails.getUser(), productId, approvalEnum);
    }


    @Operation(summary = "관리자 권한으로 주문 상태 변경")
    @Parameter(name = "orderId", description = "주문 상태를 변경할 주문의 id", required = true)
    @Parameter(name = "OrderStatusEnum", description = "변경할 주문 상태", required = true)
    @PostMapping("/admin/order/{orderId}")
    public ApiResponseDto<MessageResponseDto> updateOrderStatusByAdmin(@PathVariable final Long orderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam OrderStatusEnum orderStatusEnum) {

        return adminService.updateOrderStatusByAdmin(userDetails.getUser(), orderId, orderStatusEnum);
    }

}