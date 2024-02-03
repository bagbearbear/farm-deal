package com.farmdeal.global.service;

import com.farmdeal.domain.order.model.Order;
import com.farmdeal.domain.order.repository.OrderRepository;
import com.farmdeal.domain.product.model.Product;
import com.farmdeal.domain.product.repository.ProductRepository;
import com.farmdeal.domain.user.model.User;
import com.farmdeal.domain.user.repository.UserRepository;
import com.farmdeal.global.enums.ErrorCode;
import com.farmdeal.global.exception.CustomException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityValidatorService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    // 게시글 정보가 없을 경우 에러 메시지 전송.
    public Product validateProduct(@NotNull Long postId) {
        return validateOrThrow(productRepository, postId, ErrorCode.POST_NOT_FOUND);
    }

    // 회원 정보가 없을 경우 에러 메시지 전송.
    public User validateProfile(@NotNull Long userId) {
        return validateOrThrow(userRepository, userId,ErrorCode.PROFILE_NOT_FOUND);
    }

    public Order validateOrder(@NotNull Long orderId) {
        return validateOrThrow(orderRepository, orderId,ErrorCode.ORDER_NOT_FOUND);
    }


    private <T> T validateOrThrow(JpaRepository<T, Long> repository,
                                  Long id,
                                  ErrorCode errorCode){
        return repository.findById(id).orElseThrow(() -> new CustomException(errorCode));
    }

}
