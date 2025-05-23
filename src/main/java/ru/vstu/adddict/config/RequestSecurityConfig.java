package ru.vstu.adddict.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import ru.vstu.adddict.controller.filter.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class RequestSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/dictionaries").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/dictionaries/{id}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/dictionaries/{id}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/dictionaries/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/dictionaries/list/me").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/dictionaries/list/{userId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/dictionaries/list/subscribed").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/dictionaries/list/feed").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/dictionaries/{dictionaryId}/words/{translationId}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/dictionaries/{dictionaryId}/words").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/dictionaries/{dictionaryId}/words").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/dictionaries/{dictionaryId}/words/{translationId}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/dictionaries/{dictionaryId}/words/{translationId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/dictionaries/words/shuffle").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/v1/subscribe/dictionary/{dictionaryId}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/subscribe/dictionary/subscribe/{id}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/subscribe/dictionary/{dictionaryId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/subscribe/dictionary/list/me").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/subscribe/subscribed/dictionary/{dictionaryId}").permitAll()

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/swagger-ui/**", "/v3/api-docs/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable); // Swagger doesn't need CORS

        return http.build();
    }
}
