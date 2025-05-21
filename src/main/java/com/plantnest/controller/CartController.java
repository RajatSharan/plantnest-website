package com.plantnest.controller;

import com.plantnest.model.Plant;
import com.plantnest.model.User;
import com.plantnest.model.CartItem;
import com.plantnest.service.CartService;
import com.plantnest.service.PlantService;
import com.plantnest.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private PlantService plantService;

    @Autowired
    private UserService userService;

    // ✅ Handles AJAX "Add to Cart"
    @PostMapping("/add-to-cart/{plantId}")
    @ResponseBody
    public Map<String, Object> addToCartAjax(@PathVariable Long plantId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> result = new HashMap<>();
        if (userDetails == null) {
            result.put("cartCount", 0);
            return result;
        }

        Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
        Optional<Plant> optionalPlant = plantService.getPlantById(plantId);

        if (optionalUser.isPresent() && optionalPlant.isPresent()) {
            cartService.addToCart(optionalUser.get(), optionalPlant.get());
        }

        int cartCount = optionalUser.map(cartService::countCartItemsByUser).orElse(0);
        result.put("cartCount", cartCount);
        return result;
    }

    // ✅ Handles /cart page
    @GetMapping("/cart")
    public String viewCart(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";

        Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
        if (optionalUser.isEmpty()) return "redirect:/login";

        User user = optionalUser.get();
        List<CartItem> cartItems = cartService.getCartItemsByUser(user);

        double totalPrice = cartItems.stream()
                .filter(item -> item.getPlant() != null)
                .mapToDouble(item -> item.getPlant().getPrice() * item.getQuantity())
                .sum();

        int cartCount = cartService.countCartItemsByUser(user);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("cartCount", cartCount);
        model.addAttribute("user", user);

        return "cart"; // Renders templates/cart.html
    }

    // ✅ AJAX: Update quantity
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

        Optional<User> userOpt = userService.findByEmail(userDetails.getUsername());
        if (userOpt.isPresent()) {
            cartService.updateCartItemQuantity(itemId, quantity);
            User user = userOpt.get();
            double total = cartService.getCartItemsByUser(user)
                    .stream()
                    .mapToDouble(i -> i.getPlant().getPrice() * i.getQuantity())
                    .sum();

            response.put("success", true);
            response.put("cartCount", cartService.countCartItemsByUser(user));
            response.put("totalPrice", total);
        }
        return response;
    }

    // ✅ AJAX: Remove cart item
    @PostMapping("/cart/remove/{id}")
    @ResponseBody
    public Map<String, Object> removeCartItem(@PathVariable Long id,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> result = new HashMap<>();
        if (userDetails == null) {
            result.put("success", false);
            return result;
        }

        cartService.removeCartItem(id);
        result.put("success", true);
        return result;
    }
}
