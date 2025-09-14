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
            try {
                System.out.println("🚀 התחלת יצירת נתוני בדיקה...");

                // יצירת תפקידים
                Role adminRole = roleRepository.findByRoleName("ADMIN")
                        .orElseGet(() -> {
                            Role role = new Role("ADMIN", "מנהל מערכת - גישה מלאה");
                            return roleRepository.save(role);
                        });

                Role userRole = roleRepository.findByRoleName("USER")
                        .orElseGet(() -> {
                            Role role = new Role("USER", "משתמש רגיל - גישה מוגבלת");
                            return roleRepository.save(role);
                        });

                Role managerRole = roleRepository.findByRoleName("MANAGER")
                        .orElseGet(() -> {
                            Role role = new Role("MANAGER", "מנהל - גישה חלקית");
                            return roleRepository.save(role);
                        });

                // יצירת משתמש מנהל ראשי
                if (!userRepository.existsByUsername("admin")) {
                    User admin = new User();
                    admin.setUsername("admin");
                    admin.setPassword(passwordEncoder.encode("admin123"));
                    admin.setRoles(new HashSet<>(Set.of(adminRole)));
                    userRepository.save(admin);
                    System.out.println("✅ מנהל ראשי נוצר: admin/admin123");
                }

                // יצירת משתמש רגיל
                if (!userRepository.existsByUsername("user")) {
                    User user = new User();
                    user.setUsername("user");
                    user.setPassword(passwordEncoder.encode("user123"));
                    user.setRoles(new HashSet<>(Set.of(userRole)));
                    userRepository.save(user);
                    System.out.println("✅ משתמש רגיל נוצר: user/user123");
                }

                // יצירת מנהל מחלקה
                if (!userRepository.existsByUsername("manager")) {
                    User manager = new User();
                    manager.setUsername("manager");
                    manager.setPassword(passwordEncoder.encode("manager123"));
                    manager.setRoles(new HashSet<>(Set.of(managerRole, userRole)));
                    userRepository.save(manager);
                    System.out.println("✅ מנהל מחלקה נוצר: manager/manager123");
                }

                // יצירת משתמש לבדיקת אבטחה
                if (!userRepository.existsByUsername("testuser")) {
                    User testUser = new User();
                    testUser.setUsername("testuser");
                    testUser.setPassword(passwordEncoder.encode("test123"));
                    testUser.setRoles(new HashSet<>(Set.of(userRole)));
                    userRepository.save(testUser);
                    System.out.println("✅ משתמש בדיקה נוצר: testuser/test123");
                }

                // יצירת משתמש מתקדם
                if (!userRepository.existsByUsername("poweruser")) {
                    User powerUser = new User();
                    powerUser.setUsername("poweruser");
                    powerUser.setPassword(passwordEncoder.encode("power123"));
                    powerUser.setRoles(new HashSet<>(Set.of(userRole, managerRole)));
                    userRepository.save(powerUser);
                    System.out.println("✅ משתמש מתקדם נוצר: poweruser/power123");
                }

                System.out.println("\n📋 פרטי התחברות לבדיקה:");
                System.out.println("🔑 מנהל מערכת: admin/admin123 (גישה מלאה)");
                System.out.println("👤 משתמש רגיל: user/user123 (גישה מוגבלת)");
                System.out.println("👨‍💼 מנהל מחלקה: manager/manager123 (גישה חלקית)");
                System.out.println("🧪 משתמש בדיקה: testuser/test123 (גישה מוגבלת)");
                System.out.println("⚡ משתמש מתקדם: poweruser/power123 (גישה חלקית)");
                System.out.println("✅ יצירת נתוני בדיקה הושלמה!");

            } catch (Exception e) {
                System.err.println("❌ שגיאה ביצירת נתוני בדיקה: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}