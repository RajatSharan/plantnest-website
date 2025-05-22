package com.plantnest.controller;

import com.plantnest.model.User;
import com.plantnest.model.CartItem;
import com.plantnest.service.CartService;
import com.plantnest.service.OrderService;
import com.plantnest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class CheckoutController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    // ✅ Helper: get User from either email or username
    private Optional<User> getUser(UserDetails userDetails) {
        if (userDetails == null) return Optional.empty();
        Optional<User> userOpt = userService.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            userOpt = userService.findByUsername(userDetails.getUsername());
        }
        return userOpt;
    }

    // ✅ GET /checkout — show the checkout page
    @GetMapping("/checkout")
public String showCheckout(@AuthenticationPrincipal UserDetails userDetails, Model model,
                           @ModelAttribute("success") String success) {
    if (userDetails == null) return "redirect:/login";
    Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
    if (optionalUser.isEmpty()) {
        optionalUser = userService.findByUsername(userDetails.getUsername());
        if (optionalUser.isEmpty()) return "redirect:/login";
    }

    User user = optionalUser.get();
    List<CartItem> cartItems = cartService.getCartItemsByUser(user);
    if (cartItems == null) cartItems = List.of();

    double total = cartItems.stream()
            .filter(item -> item.getPlant() != null)
            .mapToDouble(item -> item.getPlant().getPrice() * item.getQuantity())
            .sum();

    model.addAttribute("user", user);
    model.addAttribute("cartItems", cartItems);
    model.addAttribute("total", total);
    model.addAttribute("success", success);
    return "checkout";
}
    // ✅ POST /place-order — handle form submission
    @PostMapping("/place-order")
    public String placeOrder(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam("shippingAddress") String shippingAddress,
                             @RequestParam("deliveryOption") String deliveryOption,
                             @RequestParam("paymentMethod") String paymentMethod,
                             RedirectAttributes redirectAttributes) {

        Optional<User> optionalUser = getUser(userDetails);
        if (optionalUser.isEmpty()) return "redirect:/login";

        User user = optionalUser.get();

        // ✅ Place order and clear cart
        orderService.placeOrder(user, shippingAddress, deliveryOption, paymentMethod);

        // ✅ Flash success message for GET /checkout
        redirectAttributes.addFlashAttribute("success", "Order placed successfully!");

        return "redirect:/checkout";
    }
}
