package com.farmdeal.domain.user.model;

import com.farmdeal.domain.user.dto.ProfileUpdateRequestDto;
import com.farmdeal.domain.user.enums.UserGreadeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.farmdeal.domain.user.dto.UserUpdateRequestDto;

import com.farmdeal.global.dto.Timestamped;
import com.farmdeal.global.img.dto.ImageUpdateRequestDto;
import com.farmdeal.global.role.Authority;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Table(name = "USER_TABLE")
public class User extends Timestamped {

    @Id
    @Column(name = "userId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column
    private String imageUrl;

    @Enumerated(value = EnumType.STRING)
    private Authority role;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private int point;


    @Column
    private String area;

    @Enumerated(value = EnumType.STRING)
    private UserGreadeEnum userGreadeEnum;


    @Column
    private long totalOrderAmount;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.password);
    }

    public void update(UserUpdateRequestDto requestDto) {
        this.password = requestDto.getPassword();
    }

    public void update(ImageUpdateRequestDto requestDto) {
        this.imageUrl = requestDto.getImageUrl();
    }

    //프로필 업데이트
    public void update(ProfileUpdateRequestDto requestDto) {
        this.area = requestDto.getArea();
        this.age = requestDto.getAge();

    }
    //리스트 첫번째 이미지 저장
    public void imageSave(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public void disableUser() {
        this.role = Authority.DISABLED;
    }

    public void enableUser() {
        this.role = Authority.USER;
    }

}
