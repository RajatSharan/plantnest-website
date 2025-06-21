
package com.plantnest.service;

import com.plantnest.model.Plant;
import com.plantnest.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlantServiceImpl implements PlantService {

    @Autowired
    private PlantRepository plantRepository;

    @Override
    public List<Plant> getAllPlants() {

        return plantRepository.findAll();
    }

    @Override
    public List<Plant> searchByName(String name) {
        return plantRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Plant> searchPlants(String keyword) {
        // Use the new repository method to search by name OR description
        return plantRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public void save(Plant plant) {
        plantRepository.save(plant);
    }

    @Override
    public Optional<Plant> getPlantById(Long id) {
        return plantRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        plantRepository.deleteById(id);
    }
}