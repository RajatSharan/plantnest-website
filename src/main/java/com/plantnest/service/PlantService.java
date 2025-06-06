package com.plantnest.service;

import com.plantnest.model.Plant;

import java.util.List;
import java.util.Optional;

public interface PlantService {

    List<Plant> getAllPlants();

    List<Plant> searchByName(String name);

    void save(Plant plant);

    Optional<Plant> getPlantById(Long id);

    void delete(Long id);

     List<Plant> searchPlants(String keyword);
}
