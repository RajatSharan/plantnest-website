package com.plantnest.service;

import com.plantnest.model.*;
import com.plantnest.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    /**
     * Places a new order for the given user.
     */
    public void placeOrder(User user, String address, String delivery, String payment) {
        List<CartItem> cartItems = cartService.getCartItemsByUser(user);
        double totalAmount = cartItems.stream()
            .filter(item -> item.getPlant() != null)
            .mapToDouble(item -> item.getPlant().getPrice() * item.getQuantity())
            .sum();

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(address);
        order.setDeliveryOption(delivery);
        order.setPaymentMethod(payment);
        order.setTotal(BigDecimal.valueOf(totalAmount));
        order.setStatus("PLACED");
        order.setCreatedAt(LocalDateTime.now());

        // Convert cart items to order items
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem item = new OrderItem();
            item.setPlant(cartItem.getPlant());
            item.setQuantity(cartItem.getQuantity());
            item.setOrder(order); // associate with this order
            return item;
        }).toList();

        order.setOrderItems(orderItems);

        // Save order (cascades orderItems)
        orderRepository.save(order);

        // Clear user's cart
        cartService.clearCart(user);
    }

    /**
     * Returns all orders for the given user (most recent first).
     */
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }
}