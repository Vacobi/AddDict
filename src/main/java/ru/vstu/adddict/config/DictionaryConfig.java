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

    @Bean
    public int dictionaryNameLength() {
        return dictionaryNameLength;
    }

    @Bean
    public int dictionaryDescriptionLength() {
        return dictionaryDescriptionLength;
    }
}
