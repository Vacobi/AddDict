package ru.vstu.adddict.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TranslationConfig {

    @Value("${spring.translation-validation-rule.origin-text.length}")
    private int originTextLength;

    @Value("${spring.translation-validation-rule.translation-text.length}")
    private int translationTextLength;

    @Bean
    public int originTextLength() {
        return originTextLength;
    }

    @Bean
    public int translationTextLength() {
        return translationTextLength;
    }
}
