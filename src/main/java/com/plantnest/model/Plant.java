package com.plantnest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "plant")
public class Plant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Plant name is required")
    private String name;

    @NotBlank(message = "Image filename is required")
    @Column(name = "image") // e.g., "SpiderPlant.jpg"
    private String image;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be non-negative")
    private double price;

    @Column(name = "image_url")
    private String imageUrl; // Optional: /images/filename.jpg

    private String description;

    // === Constructors ===
    public Plant() {}

    public Plant(String name, String image, double price, String imageUrl, String description) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    // === Getters and Setters ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
