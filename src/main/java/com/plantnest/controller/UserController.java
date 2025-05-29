package com.plantnest.controller;

import com.plantnest.model.User;
import com.plantnest.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String showProfileForm(Model model, HttpSession session,
                                  @RequestParam(name = "updateSuccess", required = false) String updateSuccess) {
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        User freshUser = userService.findById(sessionUser.getId());

        if (freshUser == null) {
            System.err.println("Error: Authenticated user '" + sessionUser.getUsername() + "' with ID " + sessionUser.getId() + " not found in database during profile retrieval.");
            session.invalidate();
            return "redirect:/login";
        }

        model.addAttribute("user", freshUser);
        model.addAttribute("cartCount", 0);
        model.addAttribute("query", "");

        if ("true".equals(updateSuccess)) {
            model.addAttribute("message", "Profile updated successfully!");
        }

        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid @ModelAttribute("user") User updatedUser,
            BindingResult result,
            HttpSession session,
            Model model) {

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        updatedUser.setId(sessionUser.getId());

        if (result.hasErrors()) {
            model.addAttribute("user", userService.findById(sessionUser.getId()));
            return "profile";
        }

        try {
            User currentDbUser = userService.findById(sessionUser.getId());
            if (currentDbUser != null) {
                if (!currentDbUser.getEmail().equalsIgnoreCase(updatedUser.getEmail()) && userService.existsByEmail(updatedUser.getEmail())) {
                    model.addAttribute("emailError", "Email already registered by another user.");
                    model.addAttribute("user", currentDbUser);
                    return "profile";
                }
                if (!currentDbUser.getUsername().equalsIgnoreCase(updatedUser.getUsername()) && userService.existsByUsername(updatedUser.getUsername())) {
                    model.addAttribute("usernameError", "Username already taken by another user.");
                    model.addAttribute("user", currentDbUser);
                    return "profile";
                }
            }

            User savedUser = userService.saveOrUpdateUser(updatedUser);
            session.setAttribute("user", savedUser);

            return "redirect:/profile?updateSuccess=true";
        } catch (RuntimeException e) {
            System.err.println("Profile update failed: " + e.getMessage());
            model.addAttribute("error", "Failed to update profile. " + e.getMessage());
            model.addAttribute("user", userService.findById(sessionUser.getId()));
            return "profile";
        }
    }
}