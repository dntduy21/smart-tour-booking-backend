package com.dinhngoctranduy.config;

import com.dinhngoctranduy.util.SecurityUtil;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    @Value("${jwt.base64-secret}")
    private String jwtKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
        String[] whiteList = {
                "/api/v1/ping",
//                "/api/v1/**",
                "/api/v1/login",
                "/api/v1/register",
                "/api/v1/verify",
                "/storage/**",
                "/api/v1/tours/search",
                "/api/v1/tour/custom",
                "/api/v1/tours/*",
                "/api/v1/bookings/create",
                "/api/v1/bookings/search",
                "/api/v1/bookings/*/cancel",
                "/api/v1/bookings/vnpay-return",
                "/api/v1/public/contact/primary",
                "/api/v1/forgot-password",

        };
        http
                .csrf(c -> c.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        authz -> authz
                                .requestMatchers(whiteList).permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/bookings/{id}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/reviews/tour/{tourId}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/tours").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/tours/*/export/pdf").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/categories").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/categories/{id}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/posts").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/posts/{id}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/posts/search").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/tours/upcoming").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/tours/finished").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/tours/ongoing").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/categories/{id}/tours").permitAll()
                                .anyRequest().authenticated())
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .exceptionHandling(
                        exceptions -> exceptions
                                .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())// 401
                                .accessDeniedHandler(new BearerTokenAccessDeniedHandler())) //403
                .formLogin(f -> f.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("dntd");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }


    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }
}
