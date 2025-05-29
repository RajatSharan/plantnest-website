package com.plantnest.service;

import com.plantnest.model.Product;
import com.plantnest.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;


    public List<Product> searchByName(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getFeaturedPlants() {
        return productRepository.findTop6ByOrderByIdDesc();
    }
}
