package com.farmdeal.domain.order.service;



import com.farmdeal.domain.order.dto.OrderInfoUpdateRequestDto;
import com.farmdeal.domain.order.dto.OrderResponseDto;
import com.farmdeal.domain.order.enums.OrderStatusEnum;
import com.farmdeal.domain.order.model.Order;
import com.farmdeal.domain.order.repository.OrderRedisRepository;
import com.farmdeal.domain.order.repository.OrderRepository;
import com.farmdeal.domain.product.model.Product;
import com.farmdeal.domain.user.model.User;
import com.farmdeal.global.dto.ApiResponseDto;
import com.farmdeal.global.dto.MessageResponseDto;
import com.farmdeal.global.enums.ErrorCode;
import com.farmdeal.global.enums.SuccessCode;
import com.farmdeal.global.exception.CustomException;
import com.farmdeal.global.role.Authority;
import com.farmdeal.global.service.EntityValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final EntityValidatorService entityValidatorService;
    private final OrderRedisRepository orderRedisRepository;
    private final OrderRepository orderRepository;



    @Async("orderThreadPoolTaskExecutor")
    @Transactional
    public CompletableFuture<ApiResponseDto<OrderResponseDto>> createOrder(Long productId, User user) {
        Product product = entityValidatorService.validateProduct(productId);
        // 주문 중복 검사
        duplicateOrder(user, productId);
        // 구매 전 구매수량 증가
        long quantity = orderRedisRepository.increment(product);
        // 재고량과 구매수량 비교 (등록된 재고량보다 구매수량이 많으면 구매 불가)
        long registerStock = product.getStock();
        if(quantity > registerStock) {
            orderRedisRepository.decrement(product); // 구매수량 롤백
            throw new CustomException(ErrorCode.NOT_ENOUGH_STOCK);
        }

        Order order = createOrder(product, user);
        long currentStock = registerStock - quantity;
        orderRepository.save(order);

        return CompletableFuture.completedFuture(ApiResponseDto.success(SuccessCode.SUCCESS.getMessage(),
                OrderResponseDto.fromEntity(order, currentStock)));

    }

    private Order createOrder(Product product, User user) {
        return Order.builder()
                .product(product)
                .user(user)
                .orderStatus(OrderStatusEnum.ORDER_REGISTERED)
                .build();
    }

    public ApiResponseDto<OrderResponseDto> updateOrderInfo(Long orderId,
                                                            OrderInfoUpdateRequestDto orderInfoUpdateRequestDto,
                                                            User user) {
        Order order = entityValidatorService.validateOrder(orderId);
        // 주문자나 관리자인지 확인
        validateOrderOwnerOrAdmin(orderId, user);
        // 주문 상세정보 업데이트
        order.updateOrderInfo(orderInfoUpdateRequestDto);

        return ApiResponseDto.success(SuccessCode.SUCCESS.getMessage(),
                OrderResponseDto.getOrderInfo(order));

    }


    /**
     * 주문 정보 조회
     * @param orderId
     * @return
     */
    @Transactional(readOnly = true)
    public ApiResponseDto<OrderResponseDto> getOrderInfo(Long orderId, User user) {
        Order order = entityValidatorService.validateOrder(orderId);
        // 주문자나 관리자인지 확인
        validateOrderOwnerOrAdmin(orderId, user);
        return ApiResponseDto.success(SuccessCode.SUCCESS.getMessage(),
                OrderResponseDto.getOrderInfo(order));
    }

    /**
     * 주문 취소
     * @param orderId
     * @return
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> cancelOrder(Long orderId, User user) {
        Order order = entityValidatorService.validateOrder(orderId);

        // 주문자 확인
        validateOrderOwner(orderId, user);
        // 주문상태 취소로 변경
        order.cancelOrder();
            // 구매수량 롤백
        orderRedisRepository.decrement(order.getProduct());

        return ApiResponseDto.success(SuccessCode.SUCCESS.getMessage());
    }

    private void validateOrderOwner(Long orderId, User user) {
        Order order = entityValidatorService.validateOrder(orderId);
        if (!order.getUser().equals(user)) {
            throw new CustomException(ErrorCode.NOT_ORDER_OWNER);
        }
    }
    private void validateOrderOwnerOrAdmin(Long orderId, User user) {
        Order order = entityValidatorService.validateOrder(orderId);
        if (!order.getUser().equals(user) && !user.getRole().equals(Authority.ADMIN)) {
            throw new CustomException(ErrorCode.NOT_ORDER_OWNER);
        }
    }
    // 주문 중복체크
    private void duplicateOrder(User user, Long productId) {
        Optional<Order> order = orderRepository.findByUserAndProductId(user, productId);
        if(order.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_ORDER);
        }
    }
}
