package com.farmdeal.domain.order.model;

import com.farmdeal.domain.order.dto.OrderInfoUpdateRequestDto;
import com.farmdeal.domain.order.enums.OrderStatusEnum;
import com.farmdeal.domain.order.enums.PaymentEnum;
import com.farmdeal.domain.product.model.Product;
import com.farmdeal.domain.user.model.User;
import com.farmdeal.global.dto.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderId")
    private Long id;

    @JoinColumn(name = "productId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @JoinColumn(name = "userId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum orderStatus;

    @Embedded
    private OrderInfo orderInfo;



    public void cancelOrder() {
        this.orderStatus = OrderStatusEnum.ORDER_CANCELLED;
    }
    // 회원정보 검증
    public boolean validateUser(User user) {

        return !this.user.equals(user);
    }

    public void updateOrderInfo(OrderInfoUpdateRequestDto orderInfoUpdateRequestDto) {
        this.orderInfo.updateOrderInfo(orderInfoUpdateRequestDto);
        // 결제완료로 변경
        this.orderStatus = OrderStatusEnum.PAYMENT_COMPLETED;
    }

    public void updateOrderStatus(OrderStatusEnum orderStatus) {
        this.orderStatus = orderStatus;
    }
}
