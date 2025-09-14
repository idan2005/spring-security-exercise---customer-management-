package com.example.customersmanagement.controller;

import com.example.customersmanagement.entity.User;
import com.example.customersmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/menu")
    public Map<String, Object> getMenu() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> menu = new HashMap<>();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
            boolean isUser = auth.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ROLE_USER"));

            if (isAdmin) {
                menu.put("items", List.of("Admin Dashboard", "Manage Users", "Reports", "Settings"));
            } else if (isUser) {
                menu.put("items", List.of("User Dashboard", "Profile", "Settings", "Activity"));
            }
        } else {
            menu.put("items", List.of("Home", "Login", "Register", "About", "Contact"));
        }

        return menu;
    }

    // / - דף בית
    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Customer Management System");
        return response;
    }

    // register/ - דף הרשמה
    @GetMapping("/register")
    public Map<String, String> registerPage() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration page");
        return response;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        User createdUser = userService.registerUser(user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("username", createdUser.getUsername());
        return ResponseEntity.ok(response);
    }

    // about/ - אודות
    @GetMapping("/about")
    public Map<String, String> about() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "About Customer Management System");
        response.put("version", "1.0.0");
        return response;
    }

    // contact/ - צור קשר
    @GetMapping("/contact")
    public Map<String, String> contact() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Contact us");
        response.put("email", "support@example.com");
        return response;
    }
}
