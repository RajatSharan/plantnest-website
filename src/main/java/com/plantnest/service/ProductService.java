package com.plantnest.service;

import com.plantnest.model.Product;
import com.plantnest.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for handling product-related operations
 * like search, listing, featured selection, and individual retrieval.
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Search for products by name (case-insensitive).
     *
     * @param query the search keyword
     * @return list of matching products
     */
    public List<Product> searchByName(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    /**
     * Get all available products.
     *
     * @return list of all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Get a product by ID (optional use in cart or product details).
     *
     * @param id product ID
     * @return the product, or null if not found
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    /**
     * Get the most recent 6 products for homepage featured section.
     *
     * @return list of top 6 products
     */
    public List<Product> getFeaturedPlants() {
        return productRepository.findTop6ByOrderByIdDesc();
    }
}
