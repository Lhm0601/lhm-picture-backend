package com.lhm.lhmpicturebackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Spring MVC Json 配置
 * 这个类用于配置Jackson序列化和反序列化的全局设置
 */
@JsonComponent
public class JsonConfig {

    /**
     * 添加 Long 转 json 精度丢失的配置
     * 使用Jackson2ObjectMapperBuilder构建一个ObjectMapper实例，并配置Long类型到json序列化时避免精度丢失
     *
     * @param builder Jackson2ObjectMapperBuilder的实例，用于构建ObjectMapper
     * @return ObjectMapper 实例，用于应用全局的序列化和反序列化配置
     */
    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        // 创建一个不支持XML映射的ObjectMapper实例
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        // 创建一个简单的模块，用于注册自定义的序列化器
        SimpleModule module = new SimpleModule();
        // 为Long类添加一个序列化器，确保在序列化时不会丢失精度
        module.addSerializer(Long.class, ToStringSerializer.instance);
        // 为long基本类型添加一个序列化器，确保在序列化时不会丢失精度
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        // 在ObjectMapper中注册自定义的模块
        objectMapper.registerModule(module);
        // 返回配置好的ObjectMapper实例
        return objectMapper;
    }
}
