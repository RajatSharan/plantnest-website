package com.plantnest.service;

import com.plantnest.model.CartItem;
import com.plantnest.model.Plant;
import com.plantnest.model.User;

import java.util.List;

public interface CartService {
    void addToCart(User user, Plant plant);
    List<CartItem> getCartItemsByUser(User user);
    int countCartItemsByUser(User user);
    void clearCart(User user);
void updateCartItemQuantity(Long itemId, int quantity);
void removeCartItem(Long itemId);

}
