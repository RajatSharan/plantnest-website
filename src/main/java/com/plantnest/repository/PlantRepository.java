package com.plantnest.repository;

import com.plantnest.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Add this import

import java.util.List;

@Repository
public interface PlantRepository extends JpaRepository<Plant, Long> {

     List<Plant> findByNameContainingIgnoreCase(String keyword);


     List<Plant> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}