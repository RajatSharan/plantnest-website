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
    public String showLoginForm(Model model,
                                @RequestParam(name = "registered", required = false) String registered,
                                @RequestParam(name = "resetSent", required = false) String resetSentParam, // Renamed param to differentiate from model attribute
                                @RequestParam(name = "profileLoadError", required = false) String profileLoadError) {

        System.out.println("DEBUG: AuthController - showLoginForm accessed.");
        System.out.println("DEBUG:   - registered parameter: " + registered);
        System.out.println("DEBUG:   - resetSent parameter (from URL): " + resetSentParam);
        System.out.println("DEBUG:   - profileLoadError parameter: " + profileLoadError);

        // Logic for 'registered' parameter
        if ("true".equals(registered)) {
            model.addAttribute("registrationSuccess", "Registration successful! Please log in.");
            System.out.println("DEBUG:   - Added 'registrationSuccess' to model.");
        }

        // Logic for 'profileLoadError' parameter
        if (profileLoadError != null) {
            model.addAttribute("profileLoadError", profileLoadError);
            System.out.println("DEBUG:   - Added 'profileLoadError' to model: " + profileLoadError);
        }

        // Logic for 'resetSent' parameter. CRITICAL: Only set if no profileLoadError is present.
        // This ensures profile-related errors don't trigger password reset messages.
        if (profileLoadError == null && "true".equals(resetSentParam)) {
            model.addAttribute("resetSent", true); // Pass as a boolean for Thymeleaf's th:if
            System.out.println("DEBUG:   - Added 'resetSent' to model (from URL param).");
        }

        // Note: Spring Security adds 'error=true' parameter for login failures automatically.
        // Thymeleaf's 'param.error' handles this directly, no need to add to model here.

        System.out.println("DEBUG: AuthController - Returning 'login' view.");
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

        if (userService.findByUsername(registrationRequest.getUsername()).isPresent() || userService.findByEmail(registrationRequest.getEmail()).isPresent()) {
            model.addAttribute("error", "Username or email already exists.");
            return "register";
        }

        userService.registerNewUser(registrationRequest);

        redirectAttributes.addFlashAttribute("registrationSuccess", "Registration successful! Please log in.");
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(@RequestParam(name = "resetSent", required = false) String resetSent, Model model) {
        System.out.println("DEBUG: AuthController - showForgotPasswordPage accessed.");
        System.out.println("DEBUG:   - resetSent parameter (from URL): " + resetSent);
        if ("true".equals(resetSent)) {
            model.addAttribute("resetSent", true); // Pass the parameter to the model for the template
            System.out.println("DEBUG:   - Added 'resetSent' to model for forgot_password view.");
        }
        return "forgot_password"; // Assuming this is your correct template name
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        System.out.println("DEBUG: AuthController - processForgotPassword accessed for email: " + email);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            // In a real application, you would send a password reset email here
        }
        redirectAttributes.addFlashAttribute("message", "If an account with that email exists, a password reset link has been sent.");
        redirectAttributes.addAttribute("resetSent", "true"); // Add as URL parameter for login page
        System.out.println("DEBUG: AuthController - Redirecting to login with resetSent=true.");
        return "redirect:/login";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        System.out.println("DEBUG: AuthController - showResetPasswordForm accessed with token: " + token);
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String newPassword,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        System.out.println("DEBUG: AuthController - processResetPassword accessed for token: " + token);
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            System.out.println("DEBUG: AuthController - Passwords do not match for reset.");
            return "redirect:/reset-password?token=" + token;
        }

        redirectAttributes.addFlashAttribute("message", "Your password has been reset successfully. Please log in.");
        System.out.println("DEBUG: AuthController - Password reset successful. Redirecting to login.");
        return "redirect:/login";
    }
}