package com.farmdeal.global.config.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionTimeAdvice.class);

    @Around("execution(* com.farmdeal.repository..*.*(..)) || execution(* com.farmdeal.service..*.*(..))" +
            "|| execution(* com.farmdeal.global.security.UserDetailsServiceImpl.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        LOGGER.info(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName()
                + " executed in " + executionTime + "ms");

        return proceed;
    }
}