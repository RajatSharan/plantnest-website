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

        if (freshUser == null) {
            System.err.println("Error: Authenticated user '" + sessionUser.getUsername() + "' with ID " + sessionUser.getId() + " not found in database during profile retrieval.");
            session.invalidate(); // Invalidate session as user data is inconsistent
            return "redirect:/login";
        }

        model.addAttribute("user", freshUser);
        model.addAttribute("cartCount", 0); // TODO: Implement actual cart count logic
        model.addAttribute("query", "");     // TODO: Implement actual search query
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

        updatedUser.setId(sessionUser.getId());

        if (result.hasErrors()) {
            return "profile";
        }

        if (updatedUser.getPassword() == null || updatedUser.getPassword().trim().isEmpty()) {
            User existingUser = plantNestUserService.findById(sessionUser.getId());

            if (existingUser != null) {
                updatedUser.setPassword(existingUser.getPassword());
            } else {
                System.err.println("Error: Existing user not found for ID " + sessionUser.getId() + " during profile update.");
                session.invalidate();
                return "redirect:/login";
            }
        }

        plantNestUserService.saveOrUpdateUser(updatedUser);
        session.setAttribute("user", updatedUser);

        model.addAttribute("message", "Profile updated successfully!");
        return "redirect:/profile?updateSuccess=true";
    }
}