package com.plantnest.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Login form page
    @GetMapping("/login")
    public String showLoginForm(HttpSession session, Model model) {

        // Flash attributes from Spring Security login flow
        Object error = session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
            session.removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        }

        // Optional custom logout success message
        Object logoutMessage = session.getAttribute("logoutMessage");
        if (logoutMessage != null) {
            model.addAttribute("message", logoutMessage.toString());
            session.removeAttribute("logoutMessage");
        }

        return "login"; // Loads login.html
    }

    // Optional: Logout handler if using session manually
    @GetMapping("/logout-message")
    public String logoutWithMessage(HttpSession session) {
        session.invalidate();
        session.setAttribute("logoutMessage", "You have been logged out successfully.");
        return "redirect:/login";
    }
}
