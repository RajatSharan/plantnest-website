package com.plantnest.service;

import com.plantnest.model.CartItem;
import com.plantnest.model.Order;
import com.plantnest.model.OrderItem;
import com.plantnest.model.User;
import com.plantnest.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository; //

    @Autowired
    private CartService cartService; //

    // ✅ Place order and return saved order
    public Order placeOrderAndReturn(User user, String address, String delivery, String payment) {
        List<CartItem> cartItems = cartService.getCartItemsByUser(user); //

        // ✅ Prevent empty orders
        if (cartItems.isEmpty()) { //
            throw new IllegalStateException("Cannot place an order with an empty cart."); //
        }

        // ✅ Calculate total
        BigDecimal totalAmount = cartItems.stream()
                .filter(item -> item.getPlant() != null)
                .map(item -> item.getPlant().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))) //
                .reduce(BigDecimal.ZERO, BigDecimal::add); //

        // ✅ Create order
        Order order = new Order(); //
        order.setUser(user); //
        order.setShippingAddress(address); //
        order.setDeliveryOption(delivery); //
        order.setPaymentMethod(payment); //
        order.setTotal(totalAmount); //
        order.setStatus("PLACED"); //
        order.setCreatedAt(LocalDateTime.now()); //

        // ✅ Create order items
        List<OrderItem> orderItems = cartItems.stream()
                .filter(item -> item.getPlant() != null)
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem(); //
                    orderItem.setPlant(cartItem.getPlant()); //
                    orderItem.setQuantity(cartItem.getQuantity()); //
                    orderItem.setOrder(order); // back-reference
                    return orderItem;
                })
                .collect(Collectors.toList()); //

        order.setOrderItems(orderItems); //

        // ✅ Save order and return
        Order savedOrder = orderRepository.save(order); //

        // ✅ Clear cart
        cartService.clearCart(user); //

        // ✅ Optional: Debug log
        System.out.println("Placed order ID: " + savedOrder.getId() + " for user: " + user.getEmail()); //

        return savedOrder; //
    }

    // ✅ Get all orders for a user
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user); //
    }

    // ✅ Get a specific order by ID
    public Order getOrderById(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId); //
        if (optionalOrder.isPresent()) { //
            Order order = optionalOrder.get(); //
            if (order.getOrderItems() == null) { //
                order.setOrderItems(List.of()); 
            }
            return order; //
        }
        return null; //
    }
}