package ru.vstu.adddict.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubscribesDictionariesConfig {

    @Value("${spring.subscribes-dictionaries.subscribes-dictionaries-page-size}")
    private int subscribesDictionariesPageSize;

    @Bean
    public int subscribesDictionariesPageSize() {
        return subscribesDictionariesPageSize;
    }
}
