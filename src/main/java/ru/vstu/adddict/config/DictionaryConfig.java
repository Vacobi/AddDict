package ru.vstu.adddict.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DictionaryConfig {

    @Value("${spring.dictionary-validation-rule.name.length}")
    public int dictionaryNameLength;

    @Value("${spring.dictionary-validation-rule.description.length}")
    public int dictionaryDescriptionLength;

    @Value("${spring.dictionary.dictionaries-page-size}")
    public int dictionariesPageSize;

    @Bean
    public int dictionaryNameLength() {
        return dictionaryNameLength;
    }

    @Bean
    public int dictionaryDescriptionLength() {
        return dictionaryDescriptionLength;
    }

    @Bean
    public int dictionariesPageSize() {
        return dictionariesPageSize;
    }

    @Bean
    public String userIdHeaderAttribute() {
        return "x-user-id";
    }
}
