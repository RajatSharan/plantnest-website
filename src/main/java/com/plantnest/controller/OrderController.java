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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        List<Order> orders = orderService.getOrdersByUser(user);

        model.addAttribute("orders", orders);
        return "orders";
    }

    @PostMapping("/place-order")
    public String placeOrder(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam("address") String address,
                             @RequestParam("delivery") String delivery,
                             @RequestParam("payment") String payment) {

        Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
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
        System.out.println("Requesting order ID: " + orderId);

        Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            System.out.println("User not found");
            return "redirect:/login";
        }

        User user = optionalUser.get();
        Order order = orderService.getOrderById(orderId);

        if (order == null) {
            System.out.println("Order not found");
            return "redirect:/orders";
        }

        if (!order.getUser().getId().equals(user.getId())) {
            System.out.println("Order does not belong to this user");
            return "redirect:/orders";
        }

        if (order.getOrderItems() == null) {
            order.setOrderItems(List.of()); // protect from null
        }

        model.addAttribute("order", order);
        model.addAttribute("user", user);
        System.out.println("Order loaded successfully");

        return "order-confirmation";
    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("error", e.getMessage());
        return "error"; // fallback page
    }
}

}