package com.plantnest.model;

import jakarta.persistence.*;

@Entity
@Table(name = "plant") 
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private double price;

    private String image;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(length = 1000)
    private String description;

    // --- Constructors ---

    public Product() {
    }

    public Product(String name, double price, String image, String imageUrl, String description) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
