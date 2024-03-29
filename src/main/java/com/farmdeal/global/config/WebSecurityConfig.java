package com.farmdeal.global.config;

import com.farmdeal.domain.user.repository.UserRepository;
import com.farmdeal.global.exception.CustomAuthenticationEntryPoint;
import com.farmdeal.global.redis.cacheRepository.UserCacheRepository;
import com.farmdeal.global.redis.repository.TokenRepository;
import com.farmdeal.global.security.UserDetailsServiceImpl;
import com.farmdeal.global.security.jwt.JwtAuthenticationFilter;
import com.farmdeal.global.security.jwt.JwtAuthorizationFilter;
import com.farmdeal.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserRepository userRepository;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final TokenRepository tokenRepository;
    private final UserCacheRepository userCacheRepository;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, userRepository, userCacheRepository);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }


    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtTokenProvider, userDetailsService, tokenRepository);
    }

    private static final String[] PERMIT = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/ws/chat",
            "/pub/**",
            "/sub/**"
            ,"/","/swagger-ui/**", "/v3/api-docs/**"
    };


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .and().ignoring().requestMatchers(PERMIT);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable());
        // Security의 기본방식인 Session을 사용하지 않도록 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeRequests()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/main/**").permitAll()
                .requestMatchers("/user/**").permitAll()
                .requestMatchers("/report/**").permitAll()
                .requestMatchers("/product/**").permitAll()
                .requestMatchers("/search/**").permitAll()
                .requestMatchers("/order/**").permitAll()
                .requestMatchers("/wishlist/**").permitAll()
                .requestMatchers(PERMIT).permitAll()
                .anyRequest().authenticated();
        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint));

        // 필터 관리
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}