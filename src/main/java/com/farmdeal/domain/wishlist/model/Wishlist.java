package com.farmdeal.domain.wishlist.model;

import com.farmdeal.domain.product.model.Product;
import com.farmdeal.domain.user.model.User;
import com.farmdeal.global.enums.ErrorCode;
import com.farmdeal.global.exception.CustomException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlistId")
    private Long id;

    @JoinColumn(name= "productId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @JoinColumn(name = "userId")
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    // 회원정보 검증
    public boolean validateUser(User user) {
        return !this.user.equals(user);
    }
}
