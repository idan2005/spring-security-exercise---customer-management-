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
            // ודא שקיים Role ADMIN
            Role adminRole = roleRepository.findByRoleName("ADMIN")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setRoleName("ADMIN");
                        role.setDescription("Default administrator role");
                        return roleRepository.save(role);
                    });

            // אם אין משתמשים – צור משתמש admin
            if (userRepository.count() == 0) {
                User defaultUser = new User();
                defaultUser.setUsername("admin");
                defaultUser.setPassword(passwordEncoder.encode("admin123")); // שים לב: הודעת ההדפסה שלך אמרה סיסמה שונה
                defaultUser.setRoles(new HashSet<>());
                defaultUser.getRoles().add(adminRole);

                userRepository.save(defaultUser);

                System.out.println("Default admin user created: username='admin', password='admin123'");
            } else {
                System.out.println("Users already exist in the database.");
            }
        };
    }
}
