package com.plantnest.config;

import com.plantnest.security.CustomUserDetails;
import com.plantnest.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Make sure this is imported
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // You might be able to remove this import now

import org.springframework.security.web.csrf.CookieCsrfTokenRepository; // Make sure this is imported

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/home", "/shop", "/about", "/contact", "/search",
                                "/register", "/login", "/subscribe",
                                "/forgot-password", "/reset-password",
                                "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico"
                        ).permitAll()
                        // Explicitly allow authenticated POST to /add-to-cart to resolve 403 issues reliably
                        .requestMatchers(HttpMethod.POST, "/add-to-cart/**").authenticated()
                        .requestMatchers("/dashboard/**", "/profile", "/profile/update", "/cart/**", "/orders/**")
                        .hasAnyAuthority("USER", "ROLE_USER") // Allow only authenticated users with specific roles/authorities
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/do-login")
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        // The 'logoutRequestMatcher' is deprecated for default '/logout' URL.
                        // Spring Security handles '/logout' by default, so this line is no longer needed.
                        // .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("uniqueAndSecretKey")
                        .userDetailsService(customUserDetailsService)
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(1209600)
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                .csrf(csrf -> csrf
                        // Explicitly configure CSRF token repository for better AJAX integration
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
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            request.getSession().setAttribute("user", userDetails.getUser());
            response.sendRedirect("/dashboard?loginSuccess=true");
        };
    }
}