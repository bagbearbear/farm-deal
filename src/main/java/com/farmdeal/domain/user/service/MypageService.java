package com.farmdeal.domain.user.service;


import com.farmdeal.domain.order.model.Order;
import com.farmdeal.domain.order.repository.OrderRepository;
import com.farmdeal.domain.user.dto.MypageResponseDto;
import com.farmdeal.domain.user.model.User;
import com.farmdeal.domain.user.repository.UserRepository;
import com.farmdeal.global.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.farmdeal.global.enums.SuccessCode.*;

@RequiredArgsConstructor
@Service
public class MypageService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;


    /**
     * 마이페이지 조회
     * @param user
     * @param userId
     * @return ApiResponseDto<MypageResponseDto>
     * 마이페이지 조회. 토큰을 확인하고 정보를 불러올 때 나를 좋아요 한 사람과 서로 좋아요 한 사람의 리스트를 불러온다.
     * 이때 기준이 되는 id를 내 userId로 설정.
     */
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션을
    public ApiResponseDto<Page<MypageResponseDto>> getAllOrder(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orderPage = orderRepository.findAllByUser(user, pageable);
        Page<MypageResponseDto> mypageResponseDtoPage = orderPage
                .map(MypageResponseDto::allFromEntity); //

        return ApiResponseDto.success(SUCCESS.getMessage(),mypageResponseDtoPage);
    }



}