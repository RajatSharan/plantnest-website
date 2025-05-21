package com.plantnest.controller;

import com.plantnest.model.CartItem;
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
import java.util.Optional;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private PlantService plantService;

    @Autowired
    private UserService userService;

    // ✅ Add a plant to the cart
    @PostMapping("/add-to-cart/{plantId}")
    public String addToCart(@PathVariable Long plantId,
                            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";

        Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
        Optional<Plant> optionalPlant = plantService.getPlantById(plantId);

        if (optionalUser.isPresent() && optionalPlant.isPresent()) {
            cartService.addToCart(optionalUser.get(), optionalPlant.get());
        }

        return "redirect:/dashboard";
    }
    // ✅ View cart page with cart items and total price
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
        model.addAttribute("totalPrice", totalPrice);

        int cartCount = cartService.countCartItemsByUser(user);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("cartCount", cartCount);
        model.addAttribute("user", user); // useful for greeting or header

        return "cart";
    }

    // ✅ Place order (clears cart and redirects with success message)
    @PostMapping("/cart/place-order")
    public String placeOrder(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";

        Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
        if (optionalUser.isPresent()) {
            cartService.clearCart(optionalUser.get());
        }

        return "redirect:/dashboard?orderSuccess=true";
    }
}
