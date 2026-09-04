package com.banking.Transaction.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.auth.BasicAuthRequestInterceptor;

@Configuration
public class AccountFeignConfig {

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor(
            @Value("${account.service.username}") String username,
            @Value("${account.service.password}") String password) {

        return new BasicAuthRequestInterceptor(username, password);
    }
}