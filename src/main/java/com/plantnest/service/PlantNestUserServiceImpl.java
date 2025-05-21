package com.plantnest.service;

import com.plantnest.model.User;
import com.plantnest.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PlantNestUserServiceImpl implements PlantNestUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User saveOrUpdateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * This will encode plain-text passwords in the DB at startup.
     * It only runs once and safely skips already encoded passwords.
     */
    @PostConstruct
    public void encodeExistingPasswords() {
        userRepository.findAll().forEach(user -> {
            String password = user.getPassword();
            if (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                System.out.println("Encoded password for user: " + user.getEmail());
            }
        });
    }
}
