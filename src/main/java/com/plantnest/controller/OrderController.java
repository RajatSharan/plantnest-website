package com.plantnest.controller;

import com.plantnest.model.Order;
import com.plantnest.model.User;
import com.plantnest.service.OrderService;
import com.plantnest.service.UserService;
import com.plantnest.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

@Controller
public class OrderController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @GetMapping("/orders")
    public String viewMyOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            System.out.println("User not found for email: " + userDetails.getUsername());
            return "redirect:/login";
        }

        User user = optionalUser.get();
        List<Order> orders = orderService.getOrdersByUser(user);

        if (orders == null) {
            orders = Collections.emptyList();
        }

        model.addAttribute("user", user);
        model.addAttribute("cartCount", cartService.countCartItemsByUser(user));
        model.addAttribute("query", "");

        model.addAttribute("orders", orders);
        return "my-orders";
    }

    @PostMapping("/place-order")
    public String placeOrder(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam("address") String address,
                             @RequestParam("delivery") String delivery,
                             @RequestParam("payment") String payment) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            System.out.println("User not found during place-order for email: " + userDetails.getUsername());
            return "redirect:/login";
        }

        User user = optionalUser.get();
        Order order = orderService.placeOrderAndReturn(user, address, delivery, payment);

        return "redirect:/order-confirmation?orderId=" + order.getId();
    }

    @GetMapping("/order-confirmation")
    public String showOrderConfirmation(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestParam("orderId") Long orderId,
                                        Model model) {
        try {
            if (userDetails == null) {
                System.out.println("User not authenticated for order confirmation.");
                return "redirect:/login";
            }

            System.out.println("Requesting order ID: " + orderId);

            Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
            if (optionalUser.isEmpty()) {
                System.out.println("User not found for order confirmation email: " + userDetails.getUsername());
                return "redirect:/login";
            }

            User user = optionalUser.get();
            Order order = orderService.getOrderById(orderId);

            if (order == null) {
                System.out.println("Order not found with ID: " + orderId);
                return "redirect:/orders";
            }

            if (!order.getUser().getId().equals(user.getId())) {
                System.out.println("Order " + orderId + " does not belong to user " + user.getId());
                return "redirect:/orders";
            }

            if (order.getOrderItems() == null) {
                order.setOrderItems(List.of());
            }

            model.addAttribute("user", user);
            model.addAttribute("cartCount", cartService.countCartItemsByUser(user));
            model.addAttribute("query", "");

            model.addAttribute("order", order);
            System.out.println("Order " + orderId + " loaded successfully for user " + user.getId());

            return "order-confirmation";
        } catch (Exception e) {
            System.err.println("Error showing order confirmation for ID " + orderId + ": " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "error";
        }
    }
}