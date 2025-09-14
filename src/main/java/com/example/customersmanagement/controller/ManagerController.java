package com.example.customersmanagement.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping("/dashboard")
    public Map<String, Object> managerDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        response.put("message", "🎯 ברוך הבא לממשק המנהלים");
        response.put("username", auth.getName());
        response.put("role", "MANAGER");
        response.put("description", "זהו איזור מוגבל למנהלים בלבד");
        response.put("authorities", auth.getAuthorities().toString());
        response.put("availableActions", List.of(
            "צפיה בדוחות צוות",
            "ניהול פרויקטים", 
            "אישור בקשות",
            "צפיה בנתונים"
        ));
        
        return response;
    }

    @GetMapping("/reports")
    public Map<String, Object> managerReports() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        response.put("message", "📊 דוחות מנהלים");
        response.put("access_level", "MANAGER_ONLY");
        response.put("note", "נתונים אלו זמינים רק למנהלים ומנהלי מערכת");
        
        return response;
    }

    @GetMapping("/team")
    public Map<String, Object> teamManagement() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "👥 ניהול צוות");
        response.put("access_level", "MANAGER_ONLY");
        return response;
    }
}