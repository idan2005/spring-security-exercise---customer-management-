package com.example.customersmanagement.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/test")
public class SecurityTestController {

    // ✅ Public test - accessible to everyone (no login required)
    @GetMapping("/public")
    public Map<String, Object> publicTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "✅ Public endpoint - accessible to everyone");
        response.put("status", "PUBLIC_ACCESS");
        response.put("description", "This endpoint should be accessible without login");
        response.put("authRequired", false);
        return response;
    }

    // 🔐 Authentication test - requires login (any authenticated user)
    @GetMapping("/authenticated")
    public Map<String, Object> authenticatedTest() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        response.put("message", "✅ You are successfully authenticated!");
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        response.put("status", "AUTHENTICATED");
        response.put("description", "This endpoint requires login but no specific role");
        
        return response;
    }

    // 📋 Permissions test - requires login
    @GetMapping("/permissions")
    public Map<String, Object> permissionsTest() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> permissions = new ArrayList<>();
        
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities().toString());
        
        // Check specific roles
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
        boolean isManager = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_MANAGER"));
        boolean isUser = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_USER"));
        
        // Permission information
        if (isAdmin) {
            permissions.add(createPermission("admin", "✅ Full system access", "Can access all endpoints"));
            permissions.add(createPermission("manager", "✅ Manager access", "Inherits manager permissions"));
            permissions.add(createPermission("user", "✅ User access", "Inherits user permissions"));
        } else if (isManager) {
            permissions.add(createPermission("admin", "❌ No admin access", "Requires ROLE_ADMIN"));
            permissions.add(createPermission("manager", "✅ Manager access", "Can access /manager/**"));
            permissions.add(createPermission("user", "✅ User access", "Inherits user permissions"));
        } else if (isUser) {
            permissions.add(createPermission("admin", "❌ No admin access", "Requires ROLE_ADMIN"));
            permissions.add(createPermission("manager", "❌ No manager access", "Requires ROLE_MANAGER"));
            permissions.add(createPermission("user", "✅ User access", "Can access /user/**"));
        }
        
        response.put("permissions", permissions);
        response.put("testEndpoints", getTestEndpoints());
        response.put("recommendedTests", getRecommendedTests(isAdmin, isManager, isUser));
        
        return response;
    }

    // 👤 User-level test endpoint
    @GetMapping("/user/dashboard")
    public Map<String, Object> userLevelTest() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        response.put("message", "✅ User-level test passed!");
        response.put("username", auth.getName());
        response.put("requiredRole", "USER or higher");
        response.put("status", "USER_ACCESS_GRANTED");
        
        return response;
    }

    // 👨‍💼 Manager-level test endpoint
    @GetMapping("/manager/dashboard")
    public Map<String, Object> managerLevelTest() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        response.put("message", "✅ Manager-level test passed!");
        response.put("username", auth.getName());
        response.put("requiredRole", "MANAGER or higher");
        response.put("status", "MANAGER_ACCESS_GRANTED");
        
        return response;
    }

    // 🔑 Admin-level test endpoint
    @GetMapping("/admin/system")
    public Map<String, Object> adminLevelTest() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        response.put("message", "✅ Admin-level test passed!");
        response.put("username", auth.getName());
        response.put("requiredRole", "ADMIN only");
        response.put("status", "ADMIN_ACCESS_GRANTED");
        
        return response;
    }

    // 🛡️ Security boundary test - shows what should be blocked
    @GetMapping("/security-boundaries")
    public Map<String, Object> securityBoundariesTest() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
        boolean isManager = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_MANAGER"));
        boolean isUser = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_USER"));

        response.put("currentUser", auth.getName());
        response.put("currentRoles", auth.getAuthorities().toString());
        
        List<Map<String, Object>> tests = new ArrayList<>();
        
        // Test suggestions based on current role
        if (isUser && !isManager && !isAdmin) {
            tests.add(createSecurityTest("GET /test/user/dashboard", "✅ Should PASS", "You have USER role"));
            tests.add(createSecurityTest("GET /test/manager/dashboard", "❌ Should FAIL (403)", "You don't have MANAGER role"));
            tests.add(createSecurityTest("GET /test/admin/system", "❌ Should FAIL (403)", "You don't have ADMIN role"));
            tests.add(createSecurityTest("GET /user/dashboard", "✅ Should PASS", "You have USER role"));
            tests.add(createSecurityTest("GET /manager/dashboard", "❌ Should FAIL (403)", "You don't have MANAGER role"));
            tests.add(createSecurityTest("GET /admin/dashboard", "❌ Should FAIL (403)", "You don't have ADMIN role"));
        } else if (isManager && !isAdmin) {
            tests.add(createSecurityTest("GET /test/user/dashboard", "✅ Should PASS", "You inherit USER role"));
            tests.add(createSecurityTest("GET /test/manager/dashboard", "✅ Should PASS", "You have MANAGER role"));
            tests.add(createSecurityTest("GET /test/admin/system", "❌ Should FAIL (403)", "You don't have ADMIN role"));
        } else if (isAdmin) {
            tests.add(createSecurityTest("GET /test/user/dashboard", "✅ Should PASS", "You inherit USER role"));
            tests.add(createSecurityTest("GET /test/manager/dashboard", "✅ Should PASS", "You inherit MANAGER role"));
            tests.add(createSecurityTest("GET /test/admin/system", "✅ Should PASS", "You have ADMIN role"));
        }
        
        response.put("suggestedTests", tests);
        response.put("instructions", "Try accessing the URLs above to verify security boundaries");
        
        return response;
    }

    private Map<String, Object> createPermission(String level, String status, String description) {
        Map<String, Object> permission = new HashMap<>();
        permission.put("level", level);
        permission.put("status", status);
        permission.put("description", description);
        return permission;
    }

    private Map<String, Object> createSecurityTest(String endpoint, String expectedResult, String reason) {
        Map<String, Object> test = new HashMap<>();
        test.put("endpoint", endpoint);
        test.put("expectedResult", expectedResult);
        test.put("reason", reason);
        return test;
    }

    private List<String> getTestEndpoints() {
        return List.of(
            "GET /test/public - Public access (no login)",
            "GET /test/authenticated - Requires login (any role)",
            "GET /test/permissions - Requires login (any role)",
            "GET /test/user/dashboard - Requires USER role",
            "GET /test/manager/dashboard - Requires MANAGER role", 
            "GET /test/admin/system - Requires ADMIN role"
        );
    }

    private List<String> getRecommendedTests(boolean isAdmin, boolean isManager, boolean isUser) {
        List<String> tests = new ArrayList<>();
        
        if (isAdmin) {
            tests.add("✅ Try: GET /test/admin/system - Should work");
            tests.add("✅ Try: GET /admin/dashboard - Should work");
        } else if (isManager) {
            tests.add("❌ Try: GET /test/admin/system - Should get 403 Forbidden");
            tests.add("✅ Try: GET /test/manager/dashboard - Should work");
            tests.add("❌ Try: GET /admin/dashboard - Should get 403 Forbidden");
        } else if (isUser) {
            tests.add("❌ Try: GET /test/admin/system - Should get 403 Forbidden");
            tests.add("❌ Try: GET /test/manager/dashboard - Should get 403 Forbidden");
            tests.add("✅ Try: GET /test/user/dashboard - Should work");
        }
        
        return tests;
    }
}