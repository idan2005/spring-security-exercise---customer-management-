package com.example.customersmanagement.controller;

import com.example.customersmanagement.dto.CreateUserRequest;
import com.example.customersmanagement.dto.UpdateUserRequest;
import com.example.customersmanagement.dto.UserProfile;
import com.example.customersmanagement.entity.Role;
import com.example.customersmanagement.service.RoleService;
import com.example.customersmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    // admin/dashboard/ - דף ניהול ראשי
    @GetMapping("/dashboard")
    public Map<String, Object> adminDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("message", "Welcome to Admin Dashboard");
        dashboard.put("totalUsers", userService.getTotalUsers());
        return dashboard;
    }

    // admin/users/ - רשימת כל המשתמשים (DTO)
    @GetMapping("/users")
    public List<UserProfile> getAllUsers() {
        return userService.listUsersAsProfiles(); // מחזיר DTO בלי סיסמאות
    }

    // admin/users/create/ - יצירת משתמש חדש (DTO in/out)
    @PostMapping("/users/create")
    public ResponseEntity<UserProfile> createUser(@RequestBody CreateUserRequest req) {
        UserProfile created = userService.createUser(req);
        URI location = URI.create("/admin/users/" + created.getUsername());
        return ResponseEntity.created(location).body(created);
    }

    // admin/users/{username}/edit/ - עדכון משתמש (DTO in/out)
    @PutMapping("/users/{username}/edit")
    public ResponseEntity<UserProfile> editUser(@PathVariable String username,
                                                @RequestBody UpdateUserRequest req) {
        UserProfile updated = userService.updateUser(username, req);
        return ResponseEntity.ok(updated);
    }

    // admin/users/{username}/delete/ - מחיקת משתמש (ללא גוף)
    @DeleteMapping("/users/{username}/delete")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String username) {
        userService.deleteUserByUsername(username);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
        // לחלופין: return ResponseEntity.noContent().build();
    }

    // admin/roles/ - ניהול תפקידים (משאיר כ-Entity אם זה רק לצפייה/ניהול פנימי)
    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleService.findAll();
    }

    // admin/reports/ - דוחות מערכת (כמו שהיה)
    @GetMapping("/reports")
    public Map<String, Object> getSystemReports() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> reports = new HashMap<>();

        if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
            reports.put("totalUsers", userService.getTotalUsers());
            reports.put("usersByRole", userService.getUserCountByRole());
            reports.put("sensitiveData", "Admins can see this sensitive info");
        } else {
            reports.put("summary", "Basic report - no admin privileges");
        }
        return reports;
    }
}
