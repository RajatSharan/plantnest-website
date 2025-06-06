package com.plantnest.repository;

import com.plantnest.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantRepository extends JpaRepository<Plant, Long> {

     List<Plant> findByNameContainingIgnoreCase(String keyword);
}
