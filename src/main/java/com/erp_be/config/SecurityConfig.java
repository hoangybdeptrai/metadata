package com.erp_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. KÍCH HOẠT CẤU HÌNH CORS TRONG SPRING SECURITY
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/public/**",
                                "/scalar/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    // 2. ĐỊNH NGHĨA BEAN CẤU HÌNH CORS CHUẨN ĐỂ FIX LỖI
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // SỬA TẠI ĐÂY: Dùng allowedOriginPatterns thay cho allowedOrigins
        // Việc này giúp allowCredentials(true) hoạt động hợp lệ mà không bị crash hệ thống
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));

        // Cho phép các phương thức HTTP cơ bản
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Cho phép tất cả các Headers từ Frontend gửi lên (Authorization, Content-Type,...)
        configuration.setAllowedHeaders(Collections.singletonList("*"));

        // Cho phép Frontend gửi kèm Cookie / Token xác thực tự động
        configuration.setAllowCredentials(true);

        // Thời gian trình duyệt được phép cache lại cấu hình CORS này (3600 giây = 1 giờ)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cho toàn bộ API
        return source;
    }
}