package com.plantnest.controller;

import com.plantnest.model.User;
import com.plantnest.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthController(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    // Show login form
    @GetMapping("/login")
    public String showLoginForm(Model model, @RequestParam(name = "registered", required = false) String registered) {
        if ("true".equals(registered)) {
            model.addAttribute("registrationSuccess", "Registration successful! Please log in.");
        }
        return "login"; // loads login.html
    }

    // Show registration form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // loads register.html
    }

    // Handle registration form submission
    @PostMapping("/register")
    public String registerUser(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("emailError", "Email already registered");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return "redirect:/login?registered=true"; // Redirect to login with success flag
    }

    // --- Forgot Password Functionality ---

    /**
     * Displays the forgot password form where the user enters their email.
     * Maps to your HTML link: <a th:href="@{/forgot-password}">Forgot password?</a>
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot_password"; // This will load src/main/resources/templates/forgot_password.html
    }

    /**
     * Handles the submission of the forgot password form (email submission).
     * In a real application, this would send a password reset email.
     */
    @PostMapping("/forgot-password")
    public String processForgotPasswordRequest(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        // --- IMPORTANT: Implement actual email sending and token generation logic here ---
        // For demonstration, just redirect with a message.
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            // In a real app: generate token, save it, send email.
            redirectAttributes.addFlashAttribute("message", "If an account with that email exists, a password reset link has been sent.");
        } else {
            // For security, it's often better to give a generic success message even if email not found
            // to avoid revealing which emails are registered.
            redirectAttributes.addFlashAttribute("message", "If an account with that email exists, a password reset link has been sent.");
        }

        return "redirect:/forgot-password"; // Redirect back to the form or a confirmation page
    }

    /**
     * Displays the password reset form when a user clicks the link from their email.
     * This method would typically validate the token in the URL.
     */
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model, RedirectAttributes redirectAttributes) {
        // --- IMPORTANT: Implement token validation logic here ---
        // For demonstration, just pass the token.
        model.addAttribute("token", token);
        return "reset_password"; // This will load src/main/resources/templates/reset_password.html
    }

    /**
     * Handles the submission of the new password from the reset password form.
     * This method would typically update the user's password in the database.
     */
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String newPassword,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        // --- IMPORTANT: Implement password update and token invalidation logic here ---
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/reset-password?token=" + token;
        }

        // For demonstration, assume success
        redirectAttributes.addFlashAttribute("message", "Your password has been reset successfully. Please log in.");
        return "redirect:/login"; // Redirect to login page after successful reset
    }
}