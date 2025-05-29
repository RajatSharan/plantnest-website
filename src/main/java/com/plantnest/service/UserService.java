package com.plantnest.service;

import com.plantnest.model.User;
import com.plantnest.repository.UserRepository;
import com.plantnest.dto.RegistrationRequest; // Import the DTO
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

    @Transactional
    public User save(User user) {
        // This method is for general user saving/updating.
        // For new user registration, use registerNewUser to ensure password encoding.
        if (user.getId() == null || (user.getPassword() != null && !user.getPassword().trim().isEmpty())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    // New method for registering a user from the DTO
    @Transactional
    public User registerNewUser(RegistrationRequest request) {
        // You might want to add more robust checks here before creating the user
        // (e.g., if user already exists, throw a specific exception)
        User user = new User();
        user.setUsername(request.getUsername()); // Assuming username is for Full Name
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encode password before saving
        user.setRole("USER"); // Assign a default role, if applicable to your User model

        // If your User model has firstName, lastName, phoneNumber, address fields,
        // you would map them from the RegistrationRequest DTO here.
        // user.setFirstName(request.getFirstName());
        // user.setLastName(request.getLastName());
        // user.setPhoneNumber(request.getPhoneNumber());
        // user.setAddress(request.getAddress());

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

    @Transactional
    public User saveOrUpdateUser(User updatedUser) {
        Optional<User> existingUserOptional = userRepository.findById(updatedUser.getId());

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();

            // Update fields from updatedUser
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            existingUser.setAddress(updatedUser.getAddress());
            existingUser.setUsername(updatedUser.getUsername());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            return userRepository.save(existingUser);
        } else {
            throw new RuntimeException("User with ID " + updatedUser.getId() + " not found for update.");
        }
    }
}