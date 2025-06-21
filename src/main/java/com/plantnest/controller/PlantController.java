package com.plantnest.controller;

import com.plantnest.model.Plant;
import com.plantnest.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/plants")
public class PlantController {

    @Autowired
    private PlantService plantService;

    @GetMapping("/shop")
    public String viewShopPage(Model model) {
        model.addAttribute("plants", plantService.getAllPlants());
        return "shop";
    }

    @GetMapping("/view/{id}")
    public String viewPlantDetails(@PathVariable Long id, Model model) {
        Optional<Plant> plant = plantService.getPlantById(id);
        if (plant.isPresent()) {
            model.addAttribute("plant", plant.get());
            return "plant-details";
        } else {
            return "redirect:/plants/shop";
        }
    }

    @GetMapping("/add")
    public String showAddPlantForm(Model model) {
        model.addAttribute("plant", new Plant());
        return "add-plant";
    }

    @PostMapping("/add")
    public String addPlant(@ModelAttribute("plant") Plant plant) {
        plantService.save(plant);
        return "redirect:/plants/shop";
    }
}