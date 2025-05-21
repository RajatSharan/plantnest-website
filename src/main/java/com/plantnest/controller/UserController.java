package com.plantnest.controller;

import com.plantnest.model.User;
import com.plantnest.service.PlantNestUserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private PlantNestUserService plantNestUserService;

    // Show profile page
    @GetMapping("/profile")
    public String showProfileForm(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        User freshUser = plantNestUserService.findById(sessionUser.getId());
        model.addAttribute("user", freshUser);
        return "profile";
    }

    // Handle profile update with validation
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

        // Ensure user ID is preserved
        updatedUser.setId(sessionUser.getId());

        // Validate input
        if (result.hasErrors()) {
            return "profile";
        }

        // Retain old password if new password is empty
        if (updatedUser.getPassword() == null || updatedUser.getPassword().trim().isEmpty()) {
            updatedUser.setPassword(sessionUser.getPassword());
        }

        // Save updated user and update session
        plantNestUserService.saveOrUpdateUser(updatedUser);
        session.setAttribute("user", updatedUser);

        model.addAttribute("message", "Profile updated successfully!");
        return "redirect:/profile?updateSuccess=true";
    }
}
