package com.farmdeal.domain.wishlist.repository;

import com.farmdeal.domain.product.model.Product;
import com.farmdeal.domain.user.model.User;
import com.farmdeal.domain.wishlist.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {


    Optional<Wishlist> findByUserAndProductId(User user, Long productId);
}
