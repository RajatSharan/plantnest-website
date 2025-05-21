package com.plantnest.controller;

import com.plantnest.model.Product;
import com.plantnest.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home(Model model) {
        List<Product> featuredPlants = productService.getFeaturedPlants();
        model.addAttribute("featuredPlants", featuredPlants);
        return "home";
    }
}
