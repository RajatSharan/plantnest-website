package com.plantnest.service;

import com.plantnest.model.User;
import com.plantnest.repository.UserRepository;
import com.plantnest.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Removed @Autowired for field injection. Constructor injection is preferred.
// @Autowired // Removed this line

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository; // Made final for constructor injection

    // Constructor injection is preferred for dependencies.
    // It makes the dependencies explicit and helps with testing.
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        // Trim the input to remove any leading/trailing whitespace
        String trimmedInput = input.trim();

        // Attempt to find by email first
        Optional<User> optionalUser = userRepository.findByEmail(trimmedInput);

        // If not found by email, try to find by username
        if (optionalUser.isEmpty()) {
            optionalUser = userRepository.findByUsername(trimmedInput);
        }

        // If still not found, throw UsernameNotFoundException
        if (optionalUser.isEmpty()) {
            // Log this for debugging if necessary, but don't expose too much info in the exception message
            // You might want to log: logger.warn("User not found for input: {}", trimmedInput);
            throw new UsernameNotFoundException("User not found with provided credentials.");
            // Or if you want to be more specific (less secure in production for login attempts):
            // throw new UsernameNotFoundException("User not found with email or username: " + trimmedInput);
        }

        // If found, return the CustomUserDetails
        User user = optionalUser.get();
        // You might want to add additional checks here if your User model has fields like 'enabled', 'accountNonLocked', etc.
        // For example:
        // if (!user.isEnabled()) {
        //     throw new DisabledException("User account is disabled.");
        // }
        // if (!user.isAccountNonLocked()) { // Assuming you have such a field in your User model
        //     throw new LockedException("User account is locked.");
        // }

        return new CustomUserDetails(user);
    }
}