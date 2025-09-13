package com.example.customersmanagement.controller;

import com.example.customersmanagement.dto.UserProfile;
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
@RequestMapping("/user")
public class UserRegularController {

    @Autowired
    private UserService userService;

    // user/dashboard/ - דף בית של המשתמש
    @GetMapping("/dashboard")
    public Map<String, Object> userDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("username", auth.getName());

        if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
            dashboard.put("message", "Welcome ADMIN " + auth.getName());
            dashboard.put("extraTools", List.of("User Management", "System Reports"));
        } else if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER"))) {
            dashboard.put("message", "Welcome USER " + auth.getName());
            dashboard.put("features", List.of("Profile", "Settings", "Activity"));
        } else {
            dashboard.put("message", "Welcome Guest");
        }

        return dashboard;
    }


    // user/profile/ - פרופיל אישי
    @GetMapping("/profile")
    public UserProfile getUserProfile(Authentication auth) {
        return userService.getUserProfile(auth.getName());
    }

    // user/profile/edit/ - עריכת פרופיל אישי
    @PutMapping("/profile/edit")
    public ResponseEntity<UserProfile> editProfile(
            Authentication auth,
            @RequestBody Map<String, String> updates) {
        UserProfile updatedProfile = userService.updateUserProfile(auth.getName(), updates);
        return ResponseEntity.ok(updatedProfile);
    }

    // user/settings/ - הגדרות אישיות
    @GetMapping("/settings")
    public Map<String, Object> getUserSettings(Authentication auth) {
        Map<String, Object> settings = new HashMap<>();
        settings.put("username", auth.getName());
        settings.put("accountInfo", userService.getUserAccountInfo(auth.getName()));
        return settings;
    }

    // user/activity/ - פעילות אישית
    @GetMapping("/activity")
    public Map<String, Object> getUserActivity(Authentication auth) {
        return userService.getUserActivity(auth.getName());
    }
}
