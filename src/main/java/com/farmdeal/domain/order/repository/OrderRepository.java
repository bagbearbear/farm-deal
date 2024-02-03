package com.farmdeal.domain.order.repository;

import com.farmdeal.domain.order.model.Order;
import com.farmdeal.domain.product.model.Product;
import com.farmdeal.domain.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUser(User user, Pageable pageable);

    Optional<Order> findByUserAndProductId(User user, Long productId);
}
