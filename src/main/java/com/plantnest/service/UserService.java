package com.plantnest.service;

import com.plantnest.model.User;
import com.plantnest.repository.UserRepository;
import com.plantnest.dto.RegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * General-purpose save method, encoding password only if it's a new user or password is provided.
     */
    @Transactional
    public User save(User user) {
        if (user.getId() == null || (user.getPassword() != null && !user.getPassword().trim().isEmpty())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    /**
     * Handles new user registration from a registration form.
     * This method has been updated to include all fields from the DTO.
     */
    @Transactional
    public User registerNewUser(RegistrationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());   // <-- ADDED
        user.setLastName(request.getLastName());     // <-- ADDED
        user.setPhoneNumber(request.getPhoneNumber()); // <-- ADDED
        user.setAddress(request.getAddress());       // <-- ADDED
        user.setRole("USER");
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Updates an existing user with new profile information.
     * Used primarily by the profile update form.
     */
    @Transactional
    public User saveOrUpdateUser(User updatedUser) {
        User existingUser = userRepository.findById(updatedUser.getId())
            .orElseThrow(() -> new RuntimeException("User with ID " + updatedUser.getId() + " not found."));

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setUsername(updatedUser.getUsername());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    /**
     * A simplified update method, used in older logic. Consider consolidating with saveOrUpdateUser.
     */
    @Transactional
    public void updateUserProfile(User updatedUser) {
        User existingUser = userRepository.findById(updatedUser.getId())
            .orElseThrow(() -> new RuntimeException("User with ID " + updatedUser.getId() + " not found."));

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        userRepository.save(existingUser);
    }
}