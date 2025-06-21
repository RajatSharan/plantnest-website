package com.plantnest.controller;

import com.plantnest.model.User;
import com.plantnest.service.UserService;
import com.plantnest.service.CartService; // Import CartService
import jakarta.servlet.http.HttpServletRequest; // Import HttpServletRequest
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // Import Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional; // Import Optional

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService; // Autowire CartService

    /**
     * Helper method to retrieve the User entity from Spring Security's UserDetails.
     * Searches by email first, then by username if email is not found.
     * @param userDetails The UserDetails object from Spring Security context.
     * @return An Optional containing the User entity if found, otherwise empty.
     */
    private Optional<User> getUser(UserDetails userDetails) {
        if (userDetails == null) {
            System.out.println("DEBUG: getUser called with null UserDetails in UserController.");
            return Optional.empty();
        }
        String principalName = userDetails.getUsername(); // This is typically the email or username
        System.out.println("DEBUG: UserController: Attempting to find user by principal name: " + principalName);

        Optional<User> userOpt = userService.findByEmail(principalName);
        if (userOpt.isEmpty()) {
            userOpt = userService.findByUsername(principalName);
        }
        if (userOpt.isEmpty()) {
            System.out.println("DEBUG: UserController: User not found for principal name: " + principalName);
        } else {
            System.out.println("DEBUG: UserController: User found: " + userOpt.get().getUsername());
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
        System.out.println("DEBUG: UserController: addCartCount - Counting items for User: " + userDetails.getUsername() + ", Count: " + count);
        return count;
    }

    // Show profile page
    @GetMapping("/profile")
    public String showProfileForm(Model model,
                                  @AuthenticationPrincipal UserDetails userDetails, // Use @AuthenticationPrincipal
                                  HttpServletRequest request, // For currentUri
                                  @RequestParam(name = "updateSuccess", required = false) String updateSuccess) {
        System.out.println("DEBUG: UserController: Entering showProfileForm method for /profile.");

        if (userDetails == null) {
            System.out.println("DEBUG: UserController: showProfileForm - UserDetails is null, redirecting to login.");
            return "redirect:/login"; // Should be caught by SecurityConfig, but as a fallback.
        }

        Optional<User> userOptional = getUser(userDetails);

        if (userOptional.isEmpty()) {
            System.err.println("ERROR: UserController: Authenticated user '" + userDetails.getUsername() + "' not found in database during profile retrieval. Invalidating session.");
            // Changed parameter from 'error=userNotFound' to 'profileLoadError=userNotFound'
            // This is to prevent potential conflicts with 'login.html's logic that might redirect
            // to 'forgot-password' based on a generic 'error' parameter.
            return "redirect:/login?profileLoadError=userNotFound";
        }

        User freshUser = userOptional.get();
        model.addAttribute("user", freshUser);
        // cartCount is now handled by @ModelAttribute("cartCount")
        model.addAttribute("query", ""); // Assuming this is for a search bar in the header
        model.addAttribute("currentUri", request.getRequestURI()); // Pass current URI for navbar highlighting

        if ("true".equals(updateSuccess)) {
            model.addAttribute("success", "Profile updated successfully!");
        }

        System.out.println("DEBUG: UserController: Displaying profile for user: " + freshUser.getUsername());
        return "profile"; // This will resolve to src/main/resources/templates/profile.html
    }

    // Handle profile update
    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("user") User updatedUser,
                                BindingResult result,
                                @AuthenticationPrincipal UserDetails userDetails, // Use @AuthenticationPrincipal
                                HttpSession session, // Keep HttpSession for setting 'loggedInUser' if needed elsewhere
                                Model model,
                                HttpServletRequest request) { // For currentUri in case of errors
        System.out.println("DEBUG: UserController: Entering updateProfile method.");

        if (userDetails == null) {
            System.out.println("DEBUG: UserController: updateProfile - UserDetails is null, redirecting to login.");
            return "redirect:/login";
        }

        Optional<User> sessionUserOptional = getUser(userDetails);
        if (sessionUserOptional.isEmpty()) {
            System.out.println("DEBUG: UserController: updateProfile - User not found from UserDetails, redirecting to login.");
            return "redirect:/login?error=sessionUserNotFound";
        }
        User sessionUser = sessionUserOptional.get();

        // Ensure the ID of the updated user matches the ID of the authenticated user
        updatedUser.setId(sessionUser.getId());

        if (result.hasErrors()) {
            System.out.println("DEBUG: UserController: updateProfile - Validation errors found.");
            // Re-fetch the user to ensure model has the latest data before rendering form again
            model.addAttribute("user", userService.findById(sessionUser.getId()));
            model.addAttribute("currentUri", request.getRequestURI()); // Important for navbar on error page
            model.addAttribute("query", ""); // For header
            return "profile";
        }

        try {
            User currentDbUser = userService.findById(sessionUser.getId());

            if (currentDbUser != null) {
                // Check for email uniqueness if email is changed
                if (!currentDbUser.getEmail().equalsIgnoreCase(updatedUser.getEmail()) &&
                        userService.existsByEmail(updatedUser.getEmail())) {
                    model.addAttribute("emailError", "Email already registered by another user.");
                    model.addAttribute("user", currentDbUser); // Keep current user data in model
                    model.addAttribute("currentUri", request.getRequestURI());
                    model.addAttribute("query", "");
                    System.out.println("DEBUG: UserController: updateProfile - Email already exists.");
                    return "profile";
                }

                // Check for username uniqueness if username is changed
                if (!currentDbUser.getUsername().equalsIgnoreCase(updatedUser.getUsername()) &&
                        userService.existsByUsername(updatedUser.getUsername())) {
                    model.addAttribute("usernameError", "Username already taken by another user.");
                    model.addAttribute("user", currentDbUser); // Keep current user data in model
                    model.addAttribute("currentUri", request.getRequestURI());
                    model.addAttribute("query", "");
                    System.out.println("DEBUG: UserController: updateProfile - Username already taken.");
                    return "profile";
                }
            } else {
                System.err.println("ERROR: UserController: updateProfile - Current DB user not found for ID: " + sessionUser.getId());
                model.addAttribute("error", "Error: Your user data could not be retrieved. Please try again.");
                model.addAttribute("user", updatedUser); // Return the submitted user data to the form
                model.addAttribute("currentUri", request.getRequestURI());
                model.addAttribute("query", "");
                return "profile";
            }


            User savedUser = userService.saveOrUpdateUser(updatedUser);
            // Update the 'loggedInUser' in session as well if it's being used by header/navbar
            session.setAttribute("loggedInUser", savedUser); // IMPORTANT: Update session attribute as well!
            System.out.println("DEBUG: UserController: Profile updated successfully for user: " + savedUser.getUsername());

            return "redirect:/profile?updateSuccess=true";

        } catch (RuntimeException e) {
            System.err.println("ERROR: UserController: Profile update failed: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for detailed debugging
            model.addAttribute("error", "Failed to update profile. " + e.getMessage());
            // Re-fetch the user to ensure model has the latest data before rendering form again
            model.addAttribute("user", userService.findById(sessionUser.getId()));
            model.addAttribute("currentUri", request.getRequestURI());
            model.addAttribute("query", "");
            return "profile";
        }
    }
}
