package com.lhm.lhmpicturebackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置类用于设置跨域资源共享(CORS)
 * 跨域资源共享是一种机制，它允许一个用户代理（通常是浏览器）在跨域HTTP请求中从另一个域获取资源
 * 这对于前端应用尤其重要，因为它允许前端应用与后端服务进行交互，即使它们部署在不同的域中
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 添加跨域映射配置
     *
     * @param registry CorsRegistry对象，用于注册跨域映射规则
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                .allowedOriginPatterns("*")
                // 允许所有方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许所有头部
                .allowedHeaders("*")
                // 暴露所有头部
                .exposedHeaders("*");
    }
}
