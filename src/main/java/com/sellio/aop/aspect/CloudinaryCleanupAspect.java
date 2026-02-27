package com.sellio.aop.aspect;

import com.sellio.model.dto.request.ShopRegisterRequest;
import com.sellio.service.concrete.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CloudinaryCleanupAspect {
    private final CloudinaryService cloudinaryService;

    @AfterThrowing(pointcut = "@annotation(com.sellio.aop.annotation.CleanupCloudinary)", throwing = "ex")
    public void handleCloudinaryCleanup(JoinPoint joinPoint, Exception ex) {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof ShopRegisterRequest request) {
                String folder = "shops/" + request.getName();
                cloudinaryService.forceRemoveFolder(folder);
                break;
            }
        }
    }
}
