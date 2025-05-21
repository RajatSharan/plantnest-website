package com.plantnest.repository;

import com.plantnest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantNestUserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
