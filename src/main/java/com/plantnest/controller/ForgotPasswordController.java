package com.plantnest.controller;


import com.plantnest.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;


@Controller
public class ForgotPasswordController {

    @Autowired
    private PasswordService passwordService;

    // IMPORTANT:
    // The @GetMapping("/forgot-password") and @PostMapping("/forgot-password") methods
    // are now handled by AuthController.java to avoid "Ambiguous mapping" errors.
    // Therefore, they are REMOVED from this ForgotPasswordController.java.

    /*
    // Serve the forgot password page - REMOVED from here
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    // Handle form submission (assuming you're using AJAX or similar) - REMOVED from here
    @PostMapping("/forgot-password")
    @ResponseBody
    public ResponseEntity<?> handleForgotPassword(@RequestBody ForgotPasswordRequest request) {
        passwordService.sendResetLink(request.getEmail());
        return ResponseEntity.status(303)  // HTTP 303 See Other
                .header("Location", "/login?resetSent")
                .build();
    }
    */
}