package com.farmdeal.domain.product.model;

import com.farmdeal.domain.product.enums.ProductCategoryEnum;
import com.farmdeal.domain.product.dto.ProductRequestDto;
import com.farmdeal.domain.product.enums.ProductApprovalEnum;
import com.farmdeal.domain.user.model.User;
import com.farmdeal.global.dto.Timestamped;
import com.farmdeal.global.img.model.Img;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product extends Timestamped {

    @Id
    @Column(name = "productId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //상품 판매글 제목
    @Column(nullable = false)
    private String title;
    //게시글내용
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long price;

    @Column(nullable = false)
    private long stock;

    @Transient
    @OneToMany(fetch = FetchType.LAZY)
    private final List<Img> imageList = new ArrayList<>();

    @JoinColumn(name = "userId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    //조회수 count
    @Column(nullable = false)
    private int view;

    @Enumerated(EnumType.STRING)
    private ProductCategoryEnum category;
    //단일 게시글 리스트 이미지 처리를 위한 컬럼
    @Column
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ProductApprovalEnum productApproval;


    public void update(ProductRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.category= requestDto.getCategory();
    }

    // 회원정보 검증
    public boolean validateUser(User user) {

        return !this.user.equals(user);
    }
    //조회수 증가
    public void increasePostView(){
        this.view +=1;
    }

    public void saveImage(String s) {
        this.imageUrl = s;
    }

    public void updateApprovalStatus(ProductApprovalEnum approvalStatus) {
        this.productApproval = approvalStatus;
    }
}


