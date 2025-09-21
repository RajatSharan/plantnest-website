package com.plantnest.controller;

import com.plantnest.model.Order;
import com.plantnest.model.User;
import com.plantnest.service.OrderService;
import com.plantnest.service.UserService;
import com.plantnest.service.CartService;

import jakarta.servlet.http.HttpServletRequest; // Import HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // Import Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute; // Import ModelAttribute

import java.math.BigDecimal; // Import BigDecimal for numerical operations
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

    /**
     * Helper method to retrieve the User entity from Spring Security's UserDetails.
     * Searches by email first, then by username if email is not found.
     * @param userDetails The UserDetails object from Spring Security context.
     * @return An Optional containing the User entity if found, otherwise empty.
     */
    private Optional<User> getUser(UserDetails userDetails) {
        if (userDetails == null) {
            System.out.println("DEBUG: getUser called with null UserDetails in OrderController.");
            return Optional.empty();
        }
        String principalName = userDetails.getUsername(); // This is typically the email or username
        System.out.println("DEBUG: OrderController: Attempting to find user by principal name: " + principalName);

        Optional<User> userOpt = userService.findByEmail(principalName);
        if (userOpt.isEmpty()) {
            userOpt = userService.findByUsername(principalName);
        }
        if (userOpt.isEmpty()) {
            System.out.println("DEBUG: OrderController: User not found for principal name: " + principalName);
        } else {
            System.out.println("DEBUG: OrderController: User found: " + userOpt.get().getUsername());
        }
        return userOpt;
    }

    /**
     * Populates the cartCount attribute for the header in all views handled by this controller.
     * This method runs before any @RequestMapping methods in this controller.
     * @param authentication The current user's authentication object.
     * @return The number of items in the user's cart, or 0 if not authenticated.
     */
    @ModelAttribute("cartCount")
    public int addCartCount(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return 0;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> userOpt = getUser(userDetails);
        int count = userOpt.map(cartService::countCartItemsByUser).orElse(0);
        System.out.println("DEBUG: OrderController: addCartCount - Counting items for User: " + userDetails.getUsername() + ", Count: " + count);
        return count;
    }

    /**
     * Displays the "My Orders" page, showing a list of orders for the authenticated user.
     * @param model The Model to add attributes for the view.
     * @param userDetails The authenticated user's details.
     * @param request The HttpServletRequest to get the current URI for navbar highlighting.
     * @return The name of the Thymeleaf template for the orders page.
     */
    @GetMapping("/my-orders")
    public String viewMyOrders(Model model, @AuthenticationPrincipal UserDetails userDetails,
                               HttpServletRequest request) { // <-- ADDED HttpServletRequest
        System.out.println("DEBUG: OrderController: Entering viewMyOrders method for /my-orders.");

        if (userDetails == null) {
            System.out.println("DEBUG: OrderController: viewMyOrders - UserDetails is null, redirecting to login.");
            return "redirect:/login"; // Redirect to login if user is not authenticated
        }

        Optional<User> optionalUser = getUser(userDetails);
        if (optionalUser.isEmpty()) {
            System.out.println("DEBUG: OrderController: viewMyOrders - User not found in DB for UserDetails: " + userDetails.getUsername() + ", redirecting to login.");
            return "redirect:/login"; // Redirect if user not found in DB
        }

        User user = optionalUser.get();
        // Assuming orderService.getOrdersByUser(user) fetches orders with their items and plants
        List<Order> orders = orderService.getOrdersByUser(user);

        if (orders == null) {
            orders = Collections.emptyList(); // Ensure orders list is never null for Thymeleaf
            System.out.println("DEBUG: OrderController: viewMyOrders - Orders list was null, initialized to empty list.");
        }

        // --- DEBUGGING LINES ---
        System.out.println("DEBUG: OrderController: viewMyOrders - Orders fetched for user " + user.getUsername() + ": " + orders.size() + " orders.");
        orders.forEach(order -> {
            System.out.println("  Order ID: " + order.getId() + ", Total: " + (order.getTotal() != null ? order.getTotal() : "NULL_TOTAL") + ", Status: " + order.getStatus());
            if (order.getOrderItems() != null) {
                order.getOrderItems().forEach(item -> {
                    String plantName = (item.getPlant() != null) ? item.getPlant().getName() : "NULL_PLANT";
                    BigDecimal itemPrice = (item.getPlant() != null && item.getPlant().getPrice() != null) ? item.getPlant().getPrice() : BigDecimal.ZERO;
                    // FIX: Removed null check for primitive 'int' quantity, as it cannot be null
                    System.out.println("    Item: " + plantName + ", Quantity: " + item.getQuantity() + ", Price: " + itemPrice);
                });
            } else {
                System.out.println("    Order ID " + order.getId() + " has null order items.");
            }
        });
        // --- END DEBUGGING LINES ---

        model.addAttribute("orders", orders);
        // Pass current URI to the model for active navbar link highlighting
        model.addAttribute("currentUri", request.getRequestURI());
        // Added 'user' and 'query' attributes based on previous context, ensuring header compatibility
        model.addAttribute("user", user);
        model.addAttribute("query", "");

        System.out.println("DEBUG: OrderController: Exiting viewMyOrders method, returning 'orders' view.");
        return "orders"; // <-- CHANGED THIS LINE
    }

    /**
     * Handles placing a new order.
     * @param userDetails The authenticated user's details.
     * @param address The shipping address.
     * @param delivery The delivery method.
     * @param payment The payment method.
     * @return Redirect to order confirmation page.
     */
    @PostMapping("/place-order")
    public String placeOrder(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam("address") String address,
                             @RequestParam("delivery") String delivery,
                             @RequestParam("payment") String payment) {
        System.out.println("DEBUG: OrderController: Entering placeOrder method.");

        if (userDetails == null) {
            System.out.println("DEBUG: OrderController: placeOrder - UserDetails is null, redirecting to login.");
            return "redirect:/login";
        }

        Optional<User> optionalUser = getUser(userDetails);
        if (optionalUser.isEmpty()) {
            System.out.println("DEBUG: OrderController: placeOrder - User not found during place-order for email: " + userDetails.getUsername());
            return "redirect:/login";
        }

        User user = optionalUser.get();
        Order order = orderService.placeOrderAndReturn(user, address, delivery, payment);

        if (order != null) {
            System.out.println("DEBUG: OrderController: Order placed successfully with ID: " + order.getId());
            return "redirect:/order-confirmation?orderId=" + order.getId();
        } else {
            System.err.println("ERROR: OrderController: Failed to place order for user: " + user.getUsername());
            // Potentially redirect to cart with an error message or a generic error page
            return "redirect:/cart?error=orderFailed";
        }
    }

    /**
     * Displays the order confirmation page.
     * @param userDetails The authenticated user's details.
     * @param orderId The ID of the order to confirm.
     * @param model The Model to add attributes for the view.
     * @param request The HttpServletRequest to get the current URI for navbar highlighting.
     * @return The name of the Thymeleaf template for the order confirmation page.
     */
    @GetMapping("/order-confirmation")
    public String showOrderConfirmation(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestParam("orderId") Long orderId,
                                         Model model,
                                         HttpServletRequest request) { // <-- ADDED HttpServletRequest
        System.out.println("DEBUG: OrderController: Entering showOrderConfirmation method.");
        try {
            if (userDetails == null) {
                System.out.println("DEBUG: OrderController: showOrderConfirmation - User not authenticated.");
                return "redirect:/login";
            }

            System.out.println("DEBUG: OrderController: Requesting order ID: " + orderId + " for confirmation.");

            Optional<User> optionalUser = getUser(userDetails);
            if (optionalUser.isEmpty()) {
                System.out.println("DEBUG: OrderController: showOrderConfirmation - User not found for order confirmation email: " + userDetails.getUsername());
                return "redirect:/login";
            }

            User user = optionalUser.get();
            Order order = orderService.getOrderById(orderId);

            if (order == null) {
                System.out.println("DEBUG: OrderController: showOrderConfirmation - Order not found with ID: " + orderId + ". Redirecting to orders list.");
                return "redirect:/my-orders?error=orderNotFound"; // Redirect to orders list with an error
            }

            // Ensure the order belongs to the authenticated user
            if (!order.getUser().getId().equals(user.getId())) {
                System.out.println("DEBUG: OrderController: showOrderConfirmation - Order " + orderId + " does not belong to user " + user.getId() + ". Redirecting to orders list.");
                return "redirect:/my-orders?error=unauthorizedOrder"; // Redirect for unauthorized access
            }

            // Ensure orderItems list is not null to prevent Thymeleaf errors
            if (order.getOrderItems() == null) {
                order.setOrderItems(Collections.emptyList());
                System.out.println("DEBUG: OrderController: showOrderConfirmation - Order " + orderId + " had null order items, initialized to empty list.");
            }

            model.addAttribute("order", order);
            // Add 'user', 'cartCount', 'query', and 'currentUri' for header/navbar consistency
            model.addAttribute("user", user);
            model.addAttribute("cartCount", cartService.countCartItemsByUser(user));
            model.addAttribute("query", ""); // Assuming query is used for search bar in header
            model.addAttribute("currentUri", request.getRequestURI()); // Pass current URI

            System.out.println("DEBUG: OrderController: Order " + orderId + " loaded successfully for user " + user.getId() + " for confirmation.");

            return "order-confirmation";
        } catch (Exception e) {
            System.err.println("ERROR: OrderController: An unexpected error occurred showing order confirmation for ID " + orderId + ": " + e.getMessage());
            e.printStackTrace(); // Print stack trace for detailed debugging
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "error"; // Show a generic error page
        }
    }
}