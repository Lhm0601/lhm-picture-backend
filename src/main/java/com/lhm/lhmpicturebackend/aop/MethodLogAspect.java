package com.lhm.lhmpicturebackend.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
/**
 * 业务方法执行前输出执行信息的 AOP 切面
 */
public class MethodLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(MethodLogAspect.class);

    @Before("execution(* com.lhm.lhmpicturebackend.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString(); // 获取方法名
        logger.info("执行方法: {}", methodName); // 只打印方法名
    }

    @AfterReturning(pointcut = "execution(* com.lhm.lhmpicturebackend.service.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString(); // 获取方法名
        logger.info("方法返回: {}", methodName); // 只打印方法名
    }
}
