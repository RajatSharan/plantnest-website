package com.plantnest.controller;

import com.plantnest.model.User;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;


@Controller

public class ProfileController {

  
    public String showProfile(Model model, User user) {

        return "redirect:/"; 
    }


}