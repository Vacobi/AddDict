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

    @Value("${spring.translation.translation-page-size}")
    private int translationsPageSize;

    @Value("${spring.translation.shuffle-particion-page-size}")
    private int shuffleParticionPageSize;

    @Bean
    public int originTextLength() {
        return originTextLength;
    }

    @Bean
    public int translationTextLength() {
        return translationTextLength;
    }

    @Bean
    public int translationsPageSize() {
        return translationsPageSize;
    }

    @Bean
    public int shuffleParticionPageSize() {
        return shuffleParticionPageSize;
    }
}
