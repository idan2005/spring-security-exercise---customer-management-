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

    // נשאר כמו שהיה
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // נשאר CommandLineRunner (בלי @Transactional) – לא צריך, כל עוד Role.equals/hashCode לא נוגעות ב-users
    @Bean
    public CommandLineRunner initializeUsers(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            try {
                System.out.println("🚀 התחלת יצירת נתוני בדיקה...");

                // ⚠️ חשוב: ודא ש-Role.equals/hashCode לא כוללות את השדה users (כדי לא לגרום לטעינה עצלה)
                Role adminRole = roleRepository.findByRoleName("ADMIN")
                        .orElseGet(() -> roleRepository.save(new Role(null, "ADMIN", "מנהל מערכת - גישה מלאה", null)));

                Role userRole = roleRepository.findByRoleName("USER")
                        .orElseGet(() -> roleRepository.save(new Role(null, "USER", "משתמש רגיל - גישה מוגבלת", null)));

                Role managerRole = roleRepository.findByRoleName("MANAGER")
                        .orElseGet(() -> roleRepository.save(new Role(null, "MANAGER", "מנהל - גישה חלקית", null)));

                // admin
                if (!userRepository.existsByUsername("admin")) {
                    User admin = new User();
                    admin.setUsername("admin");
                    admin.setPassword(passwordEncoder.encode("admin123"));
                    admin.setRoles(new HashSet<>(Set.of(adminRole)));
                    userRepository.save(admin);
                    System.out.println("✅ מנהל ראשי נוצר: admin/admin123");
                }

                // user
                if (!userRepository.existsByUsername("user")) {
                    User user = new User();
                    user.setUsername("user");
                    user.setPassword(passwordEncoder.encode("user123"));
                    user.setRoles(new HashSet<>(Set.of(userRole)));
                    userRepository.save(user);
                    System.out.println("✅ משתמש רגיל נוצר: user/user123");
                }

                // manager
                if (!userRepository.existsByUsername("manager")) {
                    User manager = new User();
                    manager.setUsername("manager");
                    manager.setPassword(passwordEncoder.encode("manager123"));
                    manager.setRoles(new HashSet<>(Set.of(managerRole, userRole)));
                    userRepository.save(manager);
                    System.out.println("✅ מנהל מחלקה נוצר: manager/manager123");
                }

                // testuser
                if (!userRepository.existsByUsername("testuser")) {
                    User testUser = new User();
                    testUser.setUsername("testuser");
                    testUser.setPassword(passwordEncoder.encode("test123"));
                    testUser.setRoles(new HashSet<>(Set.of(userRole)));
                    userRepository.save(testUser);
                    System.out.println("✅ משתמש בדיקה נוצר: testuser/test123");
                }

                // poweruser
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
