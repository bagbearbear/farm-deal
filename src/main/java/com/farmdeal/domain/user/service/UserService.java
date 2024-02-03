package com.farmdeal.domain.user.service;

import com.farmdeal.domain.user.dto.SignupRequestDto;
import com.farmdeal.domain.user.dto.UserUpdateRequestDto;

import com.farmdeal.domain.user.dto.UserResponseDto;
import com.farmdeal.domain.user.model.User;
import com.farmdeal.domain.user.repository.UserRepository;
import com.farmdeal.global.dto.ApiResponseDto;
import com.farmdeal.global.dto.MessageResponseDto;
import com.farmdeal.global.enums.ErrorCode;
import com.farmdeal.global.enums.SuccessCode;
import com.farmdeal.global.exception.CustomException;

import com.farmdeal.global.external.AwsS3UploadService;
import com.farmdeal.global.redis.cacheRepository.CountCacheRepository;
import com.farmdeal.global.role.Authority;
import com.farmdeal.global.security.jwt.JwtTokenProvider;
import com.farmdeal.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.usertype.UserType;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.farmdeal.global.enums.SuccessCode.*;

@RequiredArgsConstructor
@Service
@Slf4j(topic = "회원가입 및 로그인")
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AwsS3UploadService awsS3UploadService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CountCacheRepository countCacheRepository;

    private static final String ADMIN_TOKEN = ("AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC");


    /**
     * 아이디 체크
     * @param username
     * @return ApiResponseDto<MessageResponseDto>
     * 아이디 체크. DB에 저장되어 있는 usernaeme을 찾아 유저가 존재한다면 에러메시지 전송
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> checkUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (null != isPresentUser(username))
            throw new CustomException(ErrorCode.DUPLICATED_USERNAME);
        return ApiResponseDto.success("사용 가능한 ID입니다.");
    }


    @Transactional
    public ApiResponseDto<MessageResponseDto> checkEmail(String email, boolean reJoin) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            // 재가입에 동의한다면 재가입 처리
            if(reJoin && user.getRole().equals(Authority.DISABLED)) {
                user.enableUser();
                log.info("{} 회원 재가입 처리", user.getUsername());
            } else {
                throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
            }
        }
        return ApiResponseDto.success("사용 가능한 이메일입니다.");
    }

    /**
     * 닉네임 체크
     * @param nickname
     * @return ApiResponseDto<MessageResponseDto>
     * DB에 저장되어 있는 usernaeme을 찾아 유저가 존재한다면 에러메시지 전송
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> checkNickname(String nickname) {
        Optional<User> user = userRepository.findByNickname(nickname);
        if (null != isPresentNickname(nickname))
            throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
        return ApiResponseDto.success(SUCCESS_NICKNAME_CHANGE.getMessage());
    }

    /**
     * 회원가입
     * @param signupRequestDto
     * @param imgPaths
     * @return ApiResponseDto<UserResponseDto>
     */
    @Transactional
    public ApiResponseDto<UserResponseDto> createUser(SignupRequestDto signupRequestDto, String imgPaths) {
        if (!signupRequestDto.getPassword().equals(signupRequestDto.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCHED);
        }


        Authority role = getAuthority(signupRequestDto);
        // isSeller = true라면 판매자로 가입완료. 상품글 판매자만 작성 가능

        User user = userFromRequest(signupRequestDto,imgPaths, role);
        userRepository.save(user);

//        현재 서비스에서 회원가입 이후 바로 서비스를 이용할 수 있도록 설정하였기에 회원가입이 진행될 때 토큰이 발행되도록 설정
        jwtTokenProvider.reissueToken(user.getUsername());

        if (user.getRole().equals(Authority.ADMIN)) {
            return ApiResponseDto.success(SUCCESS_ADMIN_SIGNUP.getMessage());
        }

        return ApiResponseDto.success(
                SUCCESS_SIGNUP.getMessage(),
                UserResponseDto.createFromEntity(user));

    }

    private Authority getAuthority(SignupRequestDto signupRequestDto) {
        Authority role;
        if (Authority.SELLER.equals(signupRequestDto.getRole())) {
            role = Authority.SELLER;
        } else {
            role = Authority.USER;
        }
        // default는 User, isAdmin이 true 상태에서 adminToken이 일치하면 Admin권한 부여
        return checkAdminRole(role, signupRequestDto);
    }

    private Authority checkAdminRole(Authority role,SignupRequestDto signupRequestDto) {

        // default는 User, isAdmin이 true 상태에서 adminToken이 일치하면 Admin권한 부여
        if (signupRequestDto.isAdmin()) {
            if (!signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new CustomException(ErrorCode.ADMIN_PASSWORD_NOT_MATCHED);

            }
            role = Authority.ADMIN;
        }
        return role;
    }

    private User userFromRequest(SignupRequestDto signupRequestDto, String imgPaths, Authority role) {
        return User.builder()
                .username(signupRequestDto.getUsername())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .nickname(signupRequestDto.getNickname())
                .email(signupRequestDto.getEmail())
                .imageUrl(imgPaths)
                .role(role)
                .build();
    }

    /**
     * 내 정보 조회
     * @param user
     * @return ApiResponseDto<UserResponseDto>
     */

    @Transactional
    public ApiResponseDto<UserResponseDto> getUser(User user) {

        return ApiResponseDto.success(
                SUCCESS_GET_USER.getMessage(),
                UserResponseDto.getFromEntity(user)
        );
    }

    /**
     * 비밀번호 수정
     * @param requestDto
     * @param user
     * @return ApiResponseDto<MessageResponseDto>
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> updatePassword(UserUpdateRequestDto requestDto, User user) {

        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCHED);
        }

//  변경 비밀번호를 입력하여 정상처리될 경우 비밀번호 업데이트 실행. 이후 password를 set을 이용하여 복호화 적용.
        user.update(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(user);

        return ApiResponseDto.success(
                SUCCESS_PASSWORD_CHANGE.getMessage());
    }

    /**
     * 이미지 수정
     * @param user
     * @param imgPaths
     * @return ApiResponseDto<MessageResponseDto>
     */
    @Transactional
    @CachePut(value = "user", key = "#user.id")
    public ApiResponseDto<MessageResponseDto> updateImage(User user, String imgPaths) {

//        이미지를 확인하고 user의 기존 이미지를 삭제. 이후 새로 넣은 이미지로 업데이트 되도록 설정.
        if (imgPaths != null) {
            String deleteImage = user.getImageUrl();
            awsS3UploadService.deleteFile(AwsS3UploadService.getFileNameFromURL(deleteImage));
        }
//        수정된 이미지를 imgList의 첫번째 배열에 저장한 후 user에 저장.
        user.imageSave(imgPaths);

        return ApiResponseDto.success(SUCCESS_PROFILE_IMG_UPDATE.getMessage());
    }

    //  로그아웃. 토큰을 확인하여 일치할 경우 로그인 된 유저의 이미지와 토큰을 삭제.

    /**
     * 로그아웃
     * @param request
     * @param response
     * @return ApiResponseDto<MessageResponseDto>
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> logout(HttpServletRequest request, HttpServletResponse response) {
        // 리프레시 토큰 삭제, AccessToken 만료시간까지 저장
        jwtTokenProvider.logoutBlackListToken(request);
        // 쿠키에서 JWT 삭제
        CookieUtil.deleteJwtFromCookie(response);

        return ApiResponseDto.success(SUCCESS_LOGOUT.getMessage());
    }

    /**
     * 회원 탈퇴 - 계정 비활성화
     * @param user
     * @return
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> disableUser(User user) {
        user.disableUser();
        return ApiResponseDto.success(SUCCESS_USER_DISABLE.getMessage());
    }


    /**
     * 회원명 유효성체크
     * @param username
     * @return
     */
    public User isPresentUser(@NotNull String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.orElse(null);
    }

    /**
     * 닉네임 유효성체크
     * @param nickname
     * @return
     */
    public User isPresentNickname(@NotNull String nickname) {
        Optional<User> optionalUser = userRepository.findByNickname(nickname);
        return optionalUser.orElse(null);
    }

    /**
     * 이메일 유효성체크
     * @param email
     * @return
     */
    public User isPresentEmail(@NotNull String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElse(null);
    }

}
