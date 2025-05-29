package com.plantnest.controller;

import com.plantnest.model.Plant;
import com.plantnest.model.User;
import com.plantnest.model.CartItem;
import com.plantnest.service.CartService;
import com.plantnest.service.PlantService;
import com.plantnest.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.math.BigDecimal;
import java.util.*;

@Controller
@ControllerAdvice
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private PlantService plantService;

    @Autowired
    private UserService userService;

    // Helper method to get user by email or username
    private Optional<User> getUser(UserDetails userDetails) {
        if (userDetails == null) return Optional.empty();
        Optional<User> userOpt = userService.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            userOpt = userService.findByUsername(userDetails.getUsername());
        }
        return userOpt;
    }

    @ModelAttribute("cartItemCount")
    public int addCartItemCount(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return 0;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> userOpt = getUser(userDetails);
        return userOpt.map(cartService::countCartItemsByUser).orElse(0);
    }

    // Handles AJAX "Add to Cart"
    @PostMapping("/add-to-cart/{plantId}")
    @ResponseBody
    public Map<String, Object> addToCartAjax(@PathVariable Long plantId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> result = new HashMap<>();
        if (userDetails == null) {
            result.put("cartCount", 0);
            return result;
        }

        Optional<User> optionalUser = getUser(userDetails);
        Optional<Plant> optionalPlant = plantService.getPlantById(plantId);

        if (optionalUser.isPresent() && optionalPlant.isPresent()) {
            cartService.addToCart(optionalUser.get(), optionalPlant.get());
        }

        int cartCount = optionalUser.map(cartService::countCartItemsByUser).orElse(0);
        result.put("cartCount", cartCount);
        return result;
    }

    // Handles /cart page
    @GetMapping("/cart")
    public String viewCart(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";

        Optional<User> optionalUser = getUser(userDetails);
        if (optionalUser.isEmpty()) return "redirect:/login";

        User user = optionalUser.get();
        List<CartItem> cartItems = cartService.getCartItemsByUser(user);

        BigDecimal totalPrice = cartItems.stream()
                .filter(item -> item.getPlant() != null && item.getPlant().getPrice() != null)
                .map(item -> item.getPlant().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int cartCount = cartService.countCartItemsByUser(user);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("cartCount", cartCount);
        model.addAttribute("user", user);

        return "cart";
    }

    // AJAX: Update quantity
    @PostMapping("/cart/update-quantity")
    @ResponseBody
    public Map<String, Object> updateQuantity(@RequestParam Long itemId,
                                              @RequestParam int quantity,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        if (userDetails == null || quantity < 1) {
            response.put("success", false);
            return response;
        }

        Optional<User> userOpt = getUser(userDetails);
        if (userOpt.isPresent()) {
            cartService.updateCartItemQuantity(itemId, quantity);
            User user = userOpt.get();

            BigDecimal total = cartService.getCartItemsByUser(user)
                    .stream()
                    .filter(item -> item.getPlant() != null && item.getPlant().getPrice() != null)
                    .map(item -> item.getPlant().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            response.put("success", true);
            response.put("cartCount", cartService.countCartItemsByUser(user));
            response.put("totalPrice", total);
        } else {
            response.put("success", false);
        }
        return response;
    }

    // AJAX: Remove cart item
    @PostMapping("/cart/remove/{id}")
    @ResponseBody
    public Map<String, Object> removeCartItem(@PathVariable Long id,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> result = new HashMap<>();
        if (userDetails == null) {
            result.put("success", false);
            return result;
        }

        Optional<User> userOpt = getUser(userDetails);
        if (userOpt.isPresent()) {
            cartService.removeCartItem(id);
            result.put("success", true);
        } else {
            result.put("success", false);
        }
        return result;
    }

    // Optional: Handle GET on /cart/place-order gracefully
    @GetMapping("/cart/place-order")
    public String handleGetPlaceOrder() {
        return "redirect:/cart";
    }
}