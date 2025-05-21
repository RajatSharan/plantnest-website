package com.plantnest.service;

import com.plantnest.model.User;

public interface PlantNestUserService {
    User findById(Long id);
    User saveOrUpdateUser(User user);
    User findByEmail(String email);
}
