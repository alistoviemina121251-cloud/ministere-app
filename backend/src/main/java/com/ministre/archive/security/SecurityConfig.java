package com.ministre.archive.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {

        http
            /*
             * =====================================================
             * CORS
             * =====================================================
             */
            .cors(cors ->
                cors.configurationSource(
                    corsConfigurationSource()
                )
            )

            /*
             * =====================================================
             * CSRF
             * =====================================================
             */
            .csrf(csrf -> csrf.disable())

            /*
             * =====================================================
             * SESSION
             * =====================================================
             */
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.IF_REQUIRED
                )
            )

            /*
             * =====================================================
             * AUTORISATIONS
             *
             * IMPORTANT :
             * AntPathRequestMatcher est utilisé explicitement
             * parce que H2 ajoute un deuxième servlet.
             * =====================================================
             */
            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    new AntPathRequestMatcher(
                        "/actuator/health"
                    )
                ).permitAll()

                .requestMatchers(
                    new AntPathRequestMatcher(
                        "/api/auth/**"
                    )
                ).permitAll()

                .requestMatchers(
                    new AntPathRequestMatcher(
                        "/api/public/**"
                    )
                ).permitAll()

                .requestMatchers(
                    new AntPathRequestMatcher(
                        "/h2-console/**"
                    )
                ).permitAll()

                .requestMatchers(
                    new AntPathRequestMatcher(
                        "/api/**"
                    )
                ).permitAll()

                .requestMatchers(
                    new AntPathRequestMatcher(
                        "/**",
                        HttpMethod.OPTIONS.name()
                    )
                ).permitAll()

                .anyRequest().permitAll()
            )

            /*
             * =====================================================
             * H2 CONSOLE
             * =====================================================
             */
            .headers(headers ->
                headers.frameOptions(
                    frame -> frame.disable()
                )
            );

        return http.build();
    }

    /*
     * =========================================================
     * CORS
     * =========================================================
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        /*
         * Développement local + frontend Vercel.
         */
        configuration.setAllowedOriginPatterns(
            Arrays.asList(
                "http://localhost:4200",
                "http://127.0.0.1:4200",
                "https://*.onrender.com",
                "https://*.vercel.app"
            )
        );

        configuration.setAllowedMethods(
            Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"
            )
        );

        configuration.setAllowCredentials(true);

        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }

    /*
     * =========================================================
     * PASSWORD ENCODER
     * =========================================================
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}