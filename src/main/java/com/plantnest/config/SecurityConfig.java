package com.plantnest.config;

import com.plantnest.security.CustomUserDetails;
import com.plantnest.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible paths (no authentication required)
                        .requestMatchers(
                                "/", "/home", "/shop", "/about", "/contact", "/search",
                                "/register", "/login", "/forgot-password", "/subscribe", "/reset-password",
                                "/css/**", "/js/**", "/images/**",
                                "/add-to-cart/**",           // AJAX endpoint for adding to cart
                                "/cart/update-quantity",     // AJAX endpoint for updating cart item quantity
                                "/cart/remove/**"            // AJAX endpoint for removing cart item
                        ).permitAll()
                        // Paths requiring authentication
                        .requestMatchers(
                                "/dashboard", "/cart", "/profile", "/place-order", "/my-orders",
                                "/cart/apply-coupon", // Apply coupon requires authenticated user
                                "/checkout",          // Checkout requires authenticated user
                                "/order-confirmation*" // Added this to ensure order confirmation page requires authentication
                        ).authenticated()
                        // Any other request not explicitly matched above also requires authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/do-login") // URL to process the login form submission
                        .successHandler(customAuthenticationSuccessHandler()) // Custom handler for successful login
                        .failureUrl("/login?error=true") // Redirect on login failure
                        .permitAll() // Allow everyone to access the login page and process URL
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL to trigger logout
                        .logoutSuccessUrl("/login?logout=true") // Redirect after successful logout
                        .invalidateHttpSession(true) // Invalidate the HTTP session
                        .deleteCookies("JSESSIONID") // Delete session cookies
                        .permitAll() // Allow everyone to logout
                )
                .rememberMe(remember -> remember
                        .userDetailsService(customUserDetailsService)
                        .rememberMeParameter("remember-me") // Name of the checkbox parameter
                        .tokenValiditySeconds(1209600) // 2 weeks validity
                )
                .sessionManagement(session -> session
                        .maximumSessions(1) // Allow only one active session per user
                        .maxSessionsPreventsLogin(false) // Old session expires if new login occurs
                )
                .csrf(csrf -> csrf
                        // Configure CSRF token repository to store token in a cookie accessible by JS
                        // withHttpOnlyFalse() makes it accessible to JavaScript, useful for AJAX
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            // Retrieve the CustomUserDetails object from the authentication principal
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            // Store the actual User entity (from your model) in the HTTP session.
            // This is crucial because your Thymeleaf templates (e.g., home.html, cart.html)
            // are designed to access properties like 'firstName' directly from the User object.
            // CustomUserDetails typically wraps your User entity and provides methods to access it.
            request.getSession().setAttribute("loggedInUser", userDetails.getUser());
            // Redirect to the dashboard with a success flag
            response.sendRedirect("/dashboard?loginSuccess=true");
        };
    }
}
