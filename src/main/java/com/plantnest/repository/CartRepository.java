package com.plantnest.repository;

import com.plantnest.model.CartItem;
import com.plantnest.model.Plant;
import com.plantnest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserAndPlant(User user, Plant plant);

    void deleteByUser(User user);
}
