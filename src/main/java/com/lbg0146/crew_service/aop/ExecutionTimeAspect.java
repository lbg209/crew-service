package com.lbg0146.crew_service.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExecutionTimeAspect {

    //@Around("execution(* com.lbg0146.crew_service.service..*(..))")
    @Around("@annotation(com.lbg0146.crew_service.aop.ExecutionTime)")
    public Object timeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            log.info("[{}.{}] 실행 시간 : {}ms", className, methodName, end - start);
        }
    }
}
