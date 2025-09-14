package com.example.customersmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_ADMIN > ROLE_MANAGER > ROLE_USER";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        // Public paths - accessible to everyone
                        .requestMatchers("/", "/home", "/public/**", "/login**", "/register**", "/about", "/contact").permitAll()
                        
                        // Test endpoints with different security levels
                        .requestMatchers("/test/public").permitAll()  // Only this test endpoint is public
                        .requestMatchers("/test/authenticated", "/test/permissions").authenticated()  // These require login
                        .requestMatchers("/test/admin/**").hasRole("ADMIN")  // Admin test endpoints
                        .requestMatchers("/test/manager/**").hasRole("MANAGER")  // Manager test endpoints
                        .requestMatchers("/test/user/**").hasRole("USER")  // User test endpoints
                        
                        // Menu system - authenticated users only
                        .requestMatchers("/menu").authenticated()
                        
                        // Admin area - ADMIN only
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        
                        // Manager area - MANAGER and above
                        .requestMatchers("/manager/**").hasRole("MANAGER")
                        
                        // User area - USER and above
                        .requestMatchers("/user/**").hasRole("USER")
                        
                        // General API - authenticated users only
                        .requestMatchers("/api/**").authenticated()
                        
                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}