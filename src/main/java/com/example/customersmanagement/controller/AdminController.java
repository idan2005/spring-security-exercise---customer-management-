package com.example.customersmanagement.controller;

import com.example.customersmanagement.entity.Role;
import com.example.customersmanagement.entity.User;
import com.example.customersmanagement.service.UserService;
import com.example.customersmanagement.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

    // admin/users/ - רשימת כל המשתמשים
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    // admin/users/create/ - יצירת משתמש חדש
    @PostMapping("/users/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    // admin/users/{id}/edit/ - עריכת משתמש
    @PutMapping("/users/{username}/edit")
    public ResponseEntity<User> editUser(@PathVariable String username, @RequestBody User updatedUser) {
        User user = userService.updateUser(username, updatedUser);
        return ResponseEntity.ok(user);
    }

    // admin/users/{id}/delete/ - מחיקת משתמש
    @DeleteMapping("/users/{username}/delete")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    // admin/roles/ - ניהול תפקידים
    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleService.findAll();
    }

    // admin/reports/ - דוחות מערכת
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
