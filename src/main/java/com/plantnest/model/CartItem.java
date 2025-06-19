package com.plantnest.model;

import jakarta.persistence.*;

import java.math.BigDecimal; // Import BigDecimal for currency values

@Entity
@Table(name = "cart_item") // It's good practice to explicitly define table name if it differs from class name
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Use LAZY fetching to avoid N+1 issues unless always needed
    @JoinColumn(name = "plant_id", nullable = false) // Make sure plant_id cannot be null
    private Plant plant;

    @ManyToOne(fetch = FetchType.LAZY) // Use LAZY fetching
    @JoinColumn(name = "user_id", nullable = false) // Make sure user_id cannot be null
    private User user;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2) // Define precision and scale for BigDecimal
    private BigDecimal subtotal; // New field to store the calculated subtotal for the item

    // --- Constructors (Optional, but good practice) ---
    public CartItem() {
    }

    public CartItem(Plant plant, User user, int quantity) {
        this.plant = plant;
        this.user = user;
        this.quantity = quantity;
        // Subtotal will be calculated and set in the service layer
    }


    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", plant=" + (plant != null ? plant.getName() : "null") + // Avoid fetching full plant in toString
                ", user=" + (user != null ? user.getEmail() : "null") +   // Avoid fetching full user in toString
                ", quantity=" + quantity +
                ", subtotal=" + subtotal +
                '}';
    }
}