package com.example.customersmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                    .anyRequest().authenticated() // כל הבקשות דורשות התחברות
            )
            .formLogin(form -> form
                    .permitAll()         // מאפשר גישה ל-login ללא authentication
            )
            .logout(logout -> logout.permitAll()); // מאפשר logout לכל המשתמשים

        return http.build();
    }
}
