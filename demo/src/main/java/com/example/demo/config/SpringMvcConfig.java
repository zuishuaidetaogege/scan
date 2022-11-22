package com.example.demo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

/**
 * Spring 相关配置
 * @author www.dibo.ltd
 * @version v1.0
 * @date 2020/10/23
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages={"com.example"})
@MapperScan(basePackages={"com.example.demo.mapper"})
public class SpringMvcConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}