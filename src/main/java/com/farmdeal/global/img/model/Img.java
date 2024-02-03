package com.farmdeal.global.img.model;

import com.farmdeal.domain.product.model.Product;
import com.farmdeal.domain.user.model.User;
import com.farmdeal.global.img.dto.ImageUpdateRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Img {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public Img(String imageUrl, Product product) {
        this.imageUrl = imageUrl;
        this.product = product;
    }

    public Img(String imageUrl, User user) {
        this.imageUrl = imageUrl;
        this.user = user;
    }

    public void update(ImageUpdateRequestDto requestDto) {
        this.imageUrl = requestDto.getImageUrl();
    }
}