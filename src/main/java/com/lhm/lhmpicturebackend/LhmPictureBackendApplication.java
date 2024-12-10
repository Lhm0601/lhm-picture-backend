package com.lhm.lhmpicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.lhm.lhmpicturebackend.mapper")
public class LhmPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LhmPictureBackendApplication.class, args);
    }

}
