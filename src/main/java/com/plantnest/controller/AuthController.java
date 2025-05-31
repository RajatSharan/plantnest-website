package com.plantnest.controller;

import com.plantnest.dto.RegistrationRequest; 
import com.plantnest.model.User;
import com.plantnest.repository.UserRepository;
import com.plantnest.service.UserService;
import jakarta.validation.Valid;
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

    private final UserRepository userRepository;
    private final UserService userService;

    public AuthController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, @RequestParam(name = "registered", required = false) String registered,
                                 @RequestParam(name = "resetSent", required = false) String resetSent) {
        if ("true".equals(registered)) {
            model.addAttribute("registrationSuccess", "Registration successful! Please log in.");
        }
        if ("true".equals(resetSent)) {
            model.addAttribute("resetSent", "true");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid RegistrationRequest registrationRequest, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        
        if (!registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.registrationRequest", "Passwords do not match");
            return "register";
        }

        if (userService.existsByEmail(registrationRequest.getEmail())) {
            bindingResult.rejectValue("email", "error.registrationRequest", "Email already registered");
            return "register";
        }
        if (userService.existsByUsername(registrationRequest.getUsername())) {
            bindingResult.rejectValue("username", "error.registrationRequest", "Username already taken");
            return "register";
        }

        try {
            
            User user = new User();
            user.setFirstName(registrationRequest.getFirstName());
            user.setLastName(registrationRequest.getLastName());
            user.setUsername(registrationRequest.getUsername());
            user.setEmail(registrationRequest.getEmail());
            user.setPhoneNumber(registrationRequest.getPhoneNumber());
            user.setAddress(registrationRequest.getAddress());
            user.setPassword(registrationRequest.getPassword()); 

            
            user.setRole("USER"); 

            userService.save(user); 
            redirectAttributes.addAttribute("registered", "true");
            return "redirect:/login";
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            model.addAttribute("globalError", "An error occurred during registration. Please try again.");
            return "register";
        }
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPasswordRequest(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("message", "If an account with that email exists, a password reset link has been sent.");
        } else {
            redirectAttributes.addFlashAttribute("message", "If an account with that email exists, a password reset link has been sent.");
        }

        redirectAttributes.addAttribute("resetSent", "true");
        return "redirect:/login";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String newPassword,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/reset-password?token=" + token;
        }

        redirectAttributes.addFlashAttribute("message", "Your password has been reset successfully. Please log in.");
        return "redirect:/login";
    }
}