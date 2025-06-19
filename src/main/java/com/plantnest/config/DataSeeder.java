package com.plantnest.config;

import com.plantnest.model.Activity;
import com.plantnest.model.Order;
import com.plantnest.model.User;
import com.plantnest.repository.ActivityRepository;
import com.plantnest.repository.OrderRepository;
import com.plantnest.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedData(UserRepository userRepo,
                                      OrderRepository orderRepo,
                                      ActivityRepository activityRepo) {
        return args -> {
            if (userRepo.count() == 0) {
                User user = new User();
                user.setUsername("john_doe");
                user.setEmail("john@example.com");
                user.setPassword("$2a$10$6t2YnmWolbzRPG6i3dIUeegtxcU2eOcnqyaAeEj5xjHZhHKqcVozS"); // encoded "password"
                user.setFirstName("John");
                user.setLastName("Doe");

                userRepo.save(user);

                Order order1 = new Order();
                order1.setUser(user);
                order1.setStatus("Delivered");
                order1.setTotal(new BigDecimal("45.00"));
                order1.setCreatedAt(LocalDateTime.now().minusDays(2));

                Order order2 = new Order();
                order2.setUser(user);
                order2.setStatus("Shipped");
                order2.setTotal(new BigDecimal("29.99"));
                order2.setCreatedAt(LocalDateTime.now().minusDays(1));

                orderRepo.saveAll(List.of(order1, order2));

                Activity activity1 = new Activity();
                activity1.setUser(user);
                activity1.setDescription("Bought Aloe Vera");
                activity1.setAmount(new BigDecimal("18.00"));
                activity1.setTimestamp(LocalDateTime.now().minusHours(4));

                Activity activity2 = new Activity();
                activity2.setUser(user);
                activity2.setDescription("Viewed Calathea Plant");
                activity2.setAmount(new BigDecimal("22.00"));
                activity2.setTimestamp(LocalDateTime.now().minusHours(2));

                activityRepo.saveAll(List.of(activity1, activity2));

                System.out.println("🌱 Sample user, orders, and activities have been seeded.");
            }
        };
    }
}
