package com.sparta.picboy.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "https://picboy.co.kr")  //클라이언트에서 허용
                .allowedHeaders("*")
                .allowCredentials(true)
                .exposedHeaders(HttpHeaders.AUTHORIZATION)
                .exposedHeaders("AUTHORIZATION")
                .exposedHeaders("Refresh-Token")
                .allowedMethods("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

    }
}