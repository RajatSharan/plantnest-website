package com.plantnest.service;

import com.plantnest.model.User;
import com.plantnest.repository.UserRepository;
import com.plantnest.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository; 

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {

        String trimmedInput = input.trim();

        Optional<User> optionalUser = userRepository.findByEmail(trimmedInput);

        if (optionalUser.isEmpty()) {
            optionalUser = userRepository.findByUsername(trimmedInput);
        }


        if (optionalUser.isEmpty()) {

        }

        User user = optionalUser.get();

        return new CustomUserDetails(user);
    }
}