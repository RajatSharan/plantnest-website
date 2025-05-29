package com.plantnest.controller;

import com.plantnest.model.User;
// Assuming you have a UserRepository or UserService here as well
// import com.plantnest.repository.UserRepository; 
// import com.plantnest.service.PlantNestUserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping; // Removed this import if no other @GetMapping is used.
import org.springframework.web.bind.annotation.RequestMapping; // Keep if you use @RequestMapping at class level, otherwise remove.

@Controller

public class ProfileController {

  
    public String showProfile(Model model, User user) {

        return "redirect:/"; 
    }


}