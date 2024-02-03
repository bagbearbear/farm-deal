package com.farmdeal.domain.order.controller;

import com.farmdeal.domain.order.dto.OrderInfoUpdateRequestDto;
import com.farmdeal.domain.order.dto.OrderResponseDto;
import com.farmdeal.domain.order.service.OrderService;
import com.farmdeal.global.dto.ApiResponseDto;
import com.farmdeal.global.dto.MessageResponseDto;
import com.farmdeal.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/{productId}")
    public CompletableFuture<ApiResponseDto<OrderResponseDto>> createOrder(@PathVariable Long productId,
                                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return orderService.createOrder(productId, userDetails.getUser());
    }

    @PostMapping("/{orderId}")
    public ApiResponseDto<OrderResponseDto> updateOrderInfo(@PathVariable Long orderId,
                                                            @Valid @RequestBody OrderInfoUpdateRequestDto orderInfoUpdateRequestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return orderService.updateOrderInfo(orderId, orderInfoUpdateRequestDto, userDetails.getUser());
    }

    // 주문 조회
    @GetMapping("/{orderId}")
    public ApiResponseDto<OrderResponseDto> getOrder(@PathVariable Long orderId,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return orderService.getOrderInfo(orderId, userDetails.getUser());
    }

    // 주문 취소
    @DeleteMapping("/{orderId}")
    public ApiResponseDto<MessageResponseDto> cancelOrder(@PathVariable Long orderId,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return orderService.cancelOrder(orderId, userDetails.getUser());
    }
}
