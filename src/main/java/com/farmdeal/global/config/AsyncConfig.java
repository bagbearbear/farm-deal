package com.farmdeal.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    @Bean(name = "orderThreadPoolTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // 기본 스레드 수
        executor.setMaxPoolSize(8); // 최대 스레드 수
        executor.setQueueCapacity(100); // 대기 큐의 최대 사이즈
        executor.setThreadNamePrefix("ThreadNumber-"); // 스레드 이름 접두사
        executor.initialize(); // 스레드 풀을 초기화
        return executor;
    }
}