package com.plantnest.controller;

import com.plantnest.model.Order;
import com.plantnest.model.User;
import com.plantnest.service.OrderService;
import com.plantnest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class OrderController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    public String viewMyOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
        if (optionalUser.isEmpty()) return "redirect:/login";

        User user = optionalUser.get();
        List<Order> orders = orderService.getOrdersByUser(user);

        model.addAttribute("orders", orders);
        return "orders";
    }
}
