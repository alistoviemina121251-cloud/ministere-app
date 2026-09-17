package com.ministre.archive.security;

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

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {

        http
                /*
                 * =================================================
                 * CORS
                 * =================================================
                 */
                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                /*
                 * =================================================
                 * CSRF
                 *
                 * Nous utilisons une authentification par session
                 * avec notre propre endpoint de login.
                 * =================================================
                 */
                .csrf(csrf -> csrf.disable())

                /*
                 * =================================================
                 * SESSION
                 * =================================================
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )

                /*
                 * =================================================
                 * AUTORISATIONS
                 * =================================================
                 */
                .authorizeHttpRequests(auth -> auth

                        /*
                         * Health check Render
                         */
                        .requestMatchers(
                                new AntPathRequestMatcher(
                                        "/actuator/health"
                                )
                        ).permitAll()

                        /*
                         * Authentification publique
                         */
                        .requestMatchers(
                                new AntPathRequestMatcher(
                                        "/api/auth/**"
                                )
                        ).permitAll()

                        /*
                         * OPTIONS nécessaire pour CORS
                         */
                        .requestMatchers(
                                new AntPathRequestMatcher(
                                        "/**",
                                        HttpMethod.OPTIONS.name()
                                )
                        ).permitAll()

                        /*
                         * Lecture des marchés publique.
                         *
                         * Si ton site doit être entièrement privé,
                         * on pourra changer ces GET en authenticated().
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/marches/**"
                        ).permitAll()

                        /*
                         * Création d'un marché
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/marches"
                        ).authenticated()

                        /*
                         * Modification d'un marché
                         */
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/marches/**"
                        ).authenticated()

                        /*
                         * Suppression d'un marché
                         */
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/marches/**"
                        ).authenticated()

                        /*
                         * Toutes les autres routes API
                         */
                        .requestMatchers(
                                "/api/**"
                        ).authenticated()

                        /*
                         * Tout le reste
                         */
                        .anyRequest().permitAll()
                )

                /*
                 * =================================================
                 * HEADERS
                 * =================================================
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

        configuration.setAllowedOriginPatterns(
                Arrays.asList(
                        "http://localhost:4200",
                        "http://127.0.0.1:4200",
                        "https://*.onrender.com"
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