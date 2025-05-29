package com.plantnest.controller;

import com.plantnest.model.User;
import com.plantnest.model.CartItem;
import com.plantnest.model.Order;
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

import java.math.BigDecimal; // Import BigDecimal
import java.util.List;
import java.util.Optional;

@Controller
public class CheckoutController {

    @Autowired
    private UserService userService; //

    @Autowired
    private CartService cartService; //

    @Autowired
    private OrderService orderService; //

    private Optional<User> getUser(UserDetails userDetails) {
        if (userDetails == null) return Optional.empty(); //
        Optional<User> userOpt = userService.findByEmail(userDetails.getUsername()); //
        if (userOpt.isEmpty()) { //
            userOpt = userService.findByUsername(userDetails.getUsername()); //
        }
        return userOpt; //
    }

    // ✅ GET /checkout
    @GetMapping("/checkout")
    public String showCheckout(@AuthenticationPrincipal UserDetails userDetails, Model model,
                               @ModelAttribute("success") String success) {
        Optional<User> optionalUser = getUser(userDetails); //
        if (optionalUser.isEmpty()) return "redirect:/login"; //

        User user = optionalUser.get(); //
        List<CartItem> cartItems = cartService.getCartItemsByUser(user); //
        if (cartItems == null) cartItems = List.of(); //

        // Recalculate total using BigDecimal
        BigDecimal total = cartItems.stream()
                .filter(item -> item.getPlant() != null && item.getPlant().getPrice() != null)
                .map(item -> item.getPlant().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        model.addAttribute("user", user); //
        model.addAttribute("cartItems", cartItems); //
        model.addAttribute("total", total); //
        model.addAttribute("success", success); //

        return "checkout"; //
    }

    // ✅ POST /checkout/place-order
    @PostMapping("/checkout/place-order")
    public String placeOrder(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam("shippingAddress") String shippingAddress,
                             @RequestParam("deliveryOption") String deliveryOption,
                             @RequestParam("paymentMethod") String paymentMethod,
                             RedirectAttributes redirectAttributes) {

        Optional<User> optionalUser = getUser(userDetails); //
        if (optionalUser.isEmpty()) return "redirect:/login"; //

        User user = optionalUser.get(); //

        // ✅ Place order and return saved order
        Order order = orderService.placeOrderAndReturn(user, shippingAddress, deliveryOption, paymentMethod); //

        // ✅ Redirect to order confirmation page with order ID
        redirectAttributes.addFlashAttribute("success", "Order placed successfully!"); //
        return "redirect:/order-confirmation?orderId=" + order.getId(); //
    }
}