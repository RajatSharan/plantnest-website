package com.plantnest.service;

import com.plantnest.model.User;
import com.plantnest.repository.UserRepository;
import com.plantnest.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(input);
        if (optionalUser.isEmpty()) {
            optionalUser = userRepository.findByUsername(input);
        }

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email or username: " + input);
        }

        User user = optionalUser.get();
        return new CustomUserDetails(user);
    }
}
