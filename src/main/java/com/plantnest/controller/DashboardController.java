package com.plantnest.controller;

import com.plantnest.model.Plant;
import com.plantnest.model.User;
import com.plantnest.service.CartService;
import com.plantnest.service.PlantService;
import com.plantnest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private PlantService plantService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam(value = "query", required = false) String query,
                                Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        int cartCount = cartService.countCartItemsByUser(user);

        List<Plant> products;

        if (query != null && !query.trim().isEmpty()) {
            products = plantService.searchPlants(query.trim());
        } else {
            products = plantService.getAllPlants();
            query = "";
        }

        model.addAttribute("user", user);
        model.addAttribute("cartCount", cartCount);
        model.addAttribute("query", query);
        model.addAttribute("products", products);

        return "dashboardView";
    }
}