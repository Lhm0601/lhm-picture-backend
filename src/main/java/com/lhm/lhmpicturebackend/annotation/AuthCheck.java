package com.lhm.lhmpicturebackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AuthCheck注解用于方法级别的角色权限检查
 * 它指定了方法执行前需要进行的角色权限验证
 *
 * @mustRole 角色名称，指定方法执行所必需的角色
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    String mustRole();
}
