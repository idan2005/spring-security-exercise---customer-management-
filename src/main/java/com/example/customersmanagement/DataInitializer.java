package com.example.customersmanagement;

import com.example.customersmanagement.entity.Role;
import com.example.customersmanagement.entity.User;
import com.example.customersmanagement.repository.RoleRepository;
import com.example.customersmanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initializeUsers(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Ensure ADMIN role exists
            Role adminRole = roleRepository.findByRoleName("ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ADMIN", "Administrator role")));

                //  Create admin  user if not exists
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRoles(new HashSet<>(Set.of(adminRole)));
                userRepository.save(admin);
                System.out.println("✅ Default admin created (admin/admin123)");
            }

            // Ensure USER role exists
            Role userRole = roleRepository.findByRoleName("USER")
                    .orElseGet(() -> roleRepository.save(new Role("USER", "Regular user role")));

            // Create regular user if not exists
            if (!userRepository.existsByUsername("regular")) {
                User regular = new User();
                regular.setUsername("regular");
                regular.setPassword(passwordEncoder.encode("12345"));
                regular.setRoles(new HashSet<>(Set.of(userRole)));
                userRepository.save(regular);
                System.out.println("✅ Default regular created (regular/12345)");
            }
        };
    }


}