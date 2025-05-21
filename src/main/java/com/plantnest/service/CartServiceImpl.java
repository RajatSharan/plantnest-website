package com.plantnest.service;

import com.plantnest.model.CartItem;
import com.plantnest.model.Plant;
import com.plantnest.model.User;
import com.plantnest.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Override
    public void addToCart(User user, Plant plant) {
        Optional<CartItem> existingItem = cartRepository.findByUserAndPlant(user, plant);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
            cartRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setPlant(plant);
            newItem.setQuantity(1);
            cartRepository.save(newItem);
        }
    }

    @Override
    public List<CartItem> getCartItemsByUser(User user) {
        return cartRepository.findByUser(user);
    }

    @Override
    public int countCartItemsByUser(User user) {
        return cartRepository.findByUser(user).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Override
    public void clearCart(User user) {
        cartRepository.deleteByUser(user);
    }
}
