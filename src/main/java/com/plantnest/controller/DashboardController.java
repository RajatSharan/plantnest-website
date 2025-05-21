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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    private final UserService userService;
    private final PlantService plantService;
    private final CartService cartService;

    @Autowired
    public DashboardController(UserService userService, PlantService plantService, CartService cartService) {
        this.userService = userService;
        this.plantService = plantService;
        this.cartService = cartService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal UserDetails loggedInUser,
                                @RequestParam(name = "loginSuccess", required = false) String loginSuccess,
                                @RequestParam(name = "orderSuccess", required = false) String orderSuccess,
                                @RequestParam(name = "query", required = false) String query,
                                Model model) {

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        Optional<User> optionalUser = userService.findByEmail(loggedInUser.getUsername());
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        model.addAttribute("user", user);

        // If query is empty or null, show all products
        List<Plant> plants;
        if (query != null && !query.trim().isEmpty()) {
            plants = plantService.searchByName(query.trim());
        } else {
            plants = plantService.getAllPlants();
        }
        model.addAttribute("products", plants);
        model.addAttribute("query", query != null ? query.trim() : "");

        int cartCount = cartService.countCartItemsByUser(user);
        model.addAttribute("cartCount", cartCount);

        model.addAttribute("orders", user.getOrders() != null ? user.getOrders() : Collections.emptyList());
        model.addAttribute("activities", user.getActivities() != null ? user.getActivities() : Collections.emptyList());

        if ("true".equals(loginSuccess)) {
            model.addAttribute("message", "Successfully logged in!");
        } else if ("true".equals(orderSuccess)) {
            model.addAttribute("message", "Order placed successfully!");
        }

        return "dashboardView";
    }
}