package com.plantnest.config;

import com.plantnest.model.Activity;
import com.plantnest.model.Order;
import com.plantnest.model.Plant;
import com.plantnest.model.User;
import com.plantnest.repository.ActivityRepository;
import com.plantnest.repository.OrderRepository;
import com.plantnest.repository.PlantRepository;
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
                                      ActivityRepository activityRepo,
                                      PlantRepository plantRepo) { // Inject PlantRepository
        return args -> {
            if (userRepo.count() == 0) {
                User user = new User();
                user.setUsername("john_doe");
                user.setEmail("john@example.com");
                user.setPassword("$2a$10$6t2YnmWolbzRPG6i3dIUeegtxcU2eOcnqyaAeEj5xjHZhHKqcVozS"); // encoded "password"
                user.setFirstName("John");
                user.setLastName("Doe");
                user.setPhoneNumber("+919876543210");
                user.setAddress("123 Green Street, Anytown, State, Country");
                user.setRole("USER");

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
            } else {
                System.out.println("🌱 Database already contains user data. Skipping user, order, activity seeding.");
            }

            // Seed Plant data
            if (plantRepo.count() == 0) {
                Plant plant1 = new Plant(
                        "Aloe Vera",
                        "A resilient succulent with healing properties, perfect for skin care and first aid.",
                        new BigDecimal("18.00"),
                        "/images/AloeVera.jpg"
                );

                Plant plant2 = new Plant(
                        "Areca Palm",
                        "An elegant indoor palm, excellent for purifying air and adding a tropical touch.",
                        new BigDecimal("45.00"),
                        "/images/ArecaPalm.jpg"
                );

                Plant plant3 = new Plant(
                        "Boston Fern",
                        "A classic fern known for its lush green fronds, ideal for humid environments.",
                        new BigDecimal("22.50"),
                        "/images/BostonFern.jpg"
                );

                Plant plant4 = new Plant(
                        "Calathea Plant",
                        "Striking foliage with vibrant patterns, a beautiful addition to any indoor space.",
                        new BigDecimal("30.00"),
                        "/images/CalatheaPlant.jpg"
                );

                Plant plant5 = new Plant(
                        "Assorted Fresh Plants",
                        "A collection of various fresh plants, perfect for gifting or decorating multiple spaces.",
                        new BigDecimal("60.00"),
                        "/images/freshplants.jpg" // Note: This might be a generic image.
                );

                Plant plant6 = new Plant(
                        "Peace Lily",
                        "An elegant plant with white spathes, known for its air-purifying qualities and graceful appearance.",
                        new BigDecimal("28.00"),
                        "/images/PeaceLily.jpg"
                );

                Plant plant7 = new Plant(
                        "Pothos",
                        "A popular, easy-to-care-for trailing plant, ideal for beginners and low-light conditions.",
                        new BigDecimal("15.00"),
                        "/images/Pothos.jpg"
                );

                Plant plant8 = new Plant(
                        "Rubber Plant",
                        "A stylish indoor tree with large, glossy leaves, adding a bold statement to any room.",
                        new BigDecimal("40.00"),
                        "/images/RubberPlant.jpg"
                );

                Plant plant9 = new Plant(
                        "Small Assorted Plant",
                        "A charming small plant, perfect for desks, shelves, or as a thoughtful small gift.",
                        new BigDecimal("12.00"),
                        "/images/smallPlant.jpg" // Note: This might be a generic image.
                );

                Plant plant10 = new Plant(
                        "Spider Plant",
                        "An incredibly resilient and popular houseplant known for its 'spiderettes' or plantlets.",
                        new BigDecimal("17.50"),
                        "/images/spiderplant.jpg"
                );

                plantRepo.saveAll(List.of(
                        plant1, plant2, plant3, plant4, plant5,
                        plant6, plant7, plant8, plant9, plant10
                ));

                System.out.println("🌿 Sample plant data has been seeded.");
            } else {
                System.out.println("🌿 Database already contains plant data. Skipping plant seeding.");
            }
        };
    }
}