package com.plantnest.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

   @GetMapping("/logout-message")
    public String logoutWithMessage(HttpSession session) {
        session.invalidate();
        session.setAttribute("logoutMessage", "You have been logged out successfully.");
        return "redirect:/login";
    }
}