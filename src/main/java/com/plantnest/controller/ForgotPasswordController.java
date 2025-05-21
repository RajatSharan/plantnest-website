package com.plantnest.controller;

import com.plantnest.dto.ForgotPasswordRequest;
import com.plantnest.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ForgotPasswordController {

    @Autowired
    private PasswordService passwordService;

    // Serve the forgot password page
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    // Handle form submission (assuming you're using AJAX or similar)
    @PostMapping("/forgot-password")
    @ResponseBody
    public ResponseEntity<?> handleForgotPassword(@RequestBody ForgotPasswordRequest request) {
        passwordService.sendResetLink(request.getEmail());
        return ResponseEntity.status(303)  // HTTP 303 See Other
                .header("Location", "/login?resetSent")
                .build();
    }
}
