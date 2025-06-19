package com.plantnest.service;

import com.plantnest.model.CartItem;
import com.plantnest.model.Plant;
import com.plantnest.model.User;
import com.plantnest.repository.CartItemRepository; // Assuming this repository exists and works correctly
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemRepository cartRepository;

    @Override
    @Transactional
    public void addToCart(User user, Plant plant) {
        // --- DEBUG LOG START ---
        if (user != null) {
            System.out.println("DEBUG: addToCart - User ID: " + user.getId() + ", Email: " + user.getEmail() + " | Plant: " + plant.getName());
        } else {
            System.out.println("DEBUG: addToCart - User is null!");
        }
        // --- DEBUG LOG END ---

        Optional<CartItem> existingItem = cartRepository.findByUserAndPlant(user, plant);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
            // Fix: Update subtotal when quantity changes
            item.setSubtotal(plant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            cartRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setPlant(plant);
            newItem.setQuantity(1);
            // Fix: Calculate and set subtotal for new items
            newItem.setSubtotal(plant.getPrice().multiply(BigDecimal.valueOf(newItem.getQuantity())));
            cartRepository.save(newItem);
        }
    }

    @Override
    public List<CartItem> getCartItemsByUser(User user) {
        if (user != null) {
            System.out.println("DEBUG: getCartItemsByUser - Attempting to retrieve cart for User ID: " + user.getId() + ", Email: " + user.getEmail());
        } else {
            System.out.println("DEBUG: getCartItemsByUser - User is null, cannot retrieve cart!");
            return List.of();
        }
        List<CartItem> items = cartRepository.findByUser(user);
        System.out.println("DEBUG: getCartItemsByUser - Found " + items.size() + " items for User ID: " + user.getId());
        return items;
    }

    @Override
    public int countCartItemsByUser(User user) {
        // --- DEBUG LOG START ---
        if (user != null) {
            System.out.println("DEBUG: countCartItemsByUser - Counting items for User ID: " + user.getId() + ", Email: " + user.getEmail());
        } else {
            System.out.println("DEBUG: countCartItemsByUser - User is null, count is 0!");
            return 0; // Return 0 if user is null
        }
        // --- DEBUG LOG END ---
        // This line is perfectly fine and correctly sums the quantities of all items in the user's cart.
        return cartRepository.findByUser(user).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Override
    @Transactional
    public void clearCart(User user) {
        // --- DEBUG LOG START ---
        if (user != null) {
            System.out.println("DEBUG: clearCart - Clearing cart for User ID: " + user.getId() + ", Email: " + user.getEmail());
        } else {
            System.out.println("DEBUG: clearCart - User is null, cannot clear cart!");
        }
        // --- DEBUG LOG END ---
        cartRepository.deleteByUser(user);
    }

    @Override
    @Transactional
    public void updateCartItemQuantity(Long itemId, int quantity) {
        System.out.println("DEBUG: updateCartItemQuantity - Item ID: " + itemId + ", New Quantity: " + quantity);
        Optional<CartItem> optionalItem = cartRepository.findById(itemId);
        if (optionalItem.isPresent()) {
            CartItem item = optionalItem.get();
            item.setQuantity(quantity);
            // Fix: Update subtotal when quantity changes
            // This assumes CartItem's Plant object and its price are accessible
            if (item.getPlant() != null && item.getPlant().getPrice() != null) {
                item.setSubtotal(item.getPlant().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            } else {
                System.err.println("WARNING: Plant or Plant price is null for cart item " + itemId + ". Subtotal not updated.");
                // Consider throwing an exception or handling this edge case more robustly if plant/price can be null
            }
            cartRepository.save(item);
        }
    }

    @Override
    @Transactional
    public void removeCartItem(Long itemId) {
        System.out.println("DEBUG: removeCartItem - Removing Item ID: " + itemId);
        cartRepository.deleteById(itemId);
    }

    @Override
    public List<CartItem> getCartItems(User user) {
        return getCartItemsByUser(user);
    }

    @Override
    public BigDecimal getCartTotal(User user) {
        System.out.println("DEBUG: getCartTotal - Calculating total for User ID: " + (user != null ? user.getId() : "null"));
        return getCartItems(user).stream()
                .filter(item -> item.getPlant() != null && item.getPlant().getPrice() != null)
                .map(item -> item.getPlant().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}