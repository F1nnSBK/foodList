package com.foodlist.service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Development Security Configuration.
 * This configuration is active only when the 'dev' Spring profile is active.
 * It is designed to allow all requests without any authentication or authorization,
 * which is useful for local development and testing.
 *
 * WARNING: This configuration should NEVER be used in production environments
 * as it disables all security measures.
 */
@Configuration // Marks this class as a Spring configuration class
@Profile("dev") // Activates this configuration only when the "dev" profile is active
public class DevSecurityConfig {

    /**
     * Configures the security filter chain to permit all requests.
     *
     * @param http The HttpSecurity object to configure.
     * @return A SecurityFilterChain instance.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection for development convenience.
                // In production, CSRF should generally be enabled.
                .csrf(csrf -> csrf.disable())
                // Authorize all HTTP requests.
                .authorizeHttpRequests(auth -> auth
                        // Permit all requests to any URL.
                        .anyRequest().permitAll()
                );
        // Build and return the configured SecurityFilterChain.
        return http.build();
    }
}
