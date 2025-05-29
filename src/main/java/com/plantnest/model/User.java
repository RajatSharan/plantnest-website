package com.plantnest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects; // For Objects.hash and Objects.equals

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(unique = true, nullable = false) // Added nullable = false for username
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Column(nullable = false) // Added nullable = false for password
    private String password;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false) // Added nullable = false for email
    private String email;

    @NotBlank(message = "First name cannot be empty")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(nullable = false) // Added nullable = false
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(nullable = false) // Added nullable = false
    private String lastName;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
    @Column(nullable = false) // Added nullable = false
    private String phoneNumber;

    @NotBlank(message = "Address cannot be empty")
    @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    @Column(nullable = false) // Added nullable = false
    private String address;

    // --- ADDED: Role field for Spring Security ---
    @NotBlank(message = "Role cannot be empty")
    @Column(nullable = false)
    private String role; // e.g., "USER", "ADMIN", "PLANT_MANAGER"
    // It's common to have a default role if not specified,
    // which can be set in a constructor or registration logic.

    // Optional: Add boolean fields for account status if your UserDetails needs them
    // @Column(nullable = false)
    // private boolean enabled = true;
    // @Column(nullable = false)
    // private boolean accountNonLocked = true;
    // @Column(nullable = false)
    // private boolean credentialsNonExpired = true;
    // @Column(nullable = false)
    // private boolean accountNonExpired = true;

    // --- Relationships ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Activity> activities = new ArrayList<>();

    // Default constructor
    public User() {
    }

    // You might want a convenience constructor for creating new users
    public User(String username, String password, String email, String firstName, String lastName, String phoneNumber, String address, String role) {
        this.username = username;
        this.password = password; // Remember to hash this password before persisting!
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role; // Initialize role
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // --- ADDED: Getter and Setter for role ---
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // --- Optional: Getters and Setters for account status fields if you add them ---
    // public boolean isEnabled() { return enabled; }
    // public void setEnabled(boolean enabled) { this.enabled = enabled; }
    // public boolean isAccountNonLocked() { return accountNonLocked; }
    // public void setAccountNonLocked(boolean accountNonLocked) { this.accountNonLocked = account; }
    // public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
    // public void setCredentialsNonExpired(boolean credentialsNonExpired) { this.credentialsNonExpired = credentialsNonExpired; }
    // public boolean isAccountNonExpired() { return accountNonExpired; }
    // public void setAccountNonExpired(boolean accountNonExpired) { this.accountNonExpired = accountNonExpired; }


    // Getters and Setters for relationships
    // These return unmodifiable lists to prevent external direct modification,
    // promoting the use of add/remove helper methods for controlled changes.
    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    // Setter for collections is generally discouraged, but if needed:
    // It's safer to clear and add all rather than just assigning a new list.
    public void setOrders(List<Order> orders) {
        this.orders.clear();
        if (orders != null) {
            this.orders.addAll(orders);
        }
    }

    // Helper methods to manage orders for proper bidirectional synchronization
    public void addOrder(Order order) {
        if (order != null && !this.orders.contains(order)) {
            this.orders.add(order);
            order.setUser(this); // Crucial for maintaining bidirectional relationship
        }
    }

    public void removeOrder(Order order) {
        if (order != null && this.orders.contains(order)) {
            this.orders.remove(order);
            order.setUser(null); // Crucial for removing relationship on inverse side
        }
    }

    public List<Activity> getActivities() {
        return Collections.unmodifiableList(activities);
    }

    public void setActivities(List<Activity> activities) {
        this.activities.clear();
        if (activities != null) {
            this.activities.addAll(activities);
        }
    }

    public void addActivity(Activity activity) {
        if (activity != null && !this.activities.contains(activity)) {
            this.activities.add(activity);
            activity.setUser(this);
        }
    }

    public void removeActivity(Activity activity) {
        if (activity != null && this.activities.contains(activity)) {
            this.activities.remove(activity);
            activity.setUser(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // For entities, equality should ideally be based on the ID once it's persisted.
        // For transient entities (before saving), you might use a business key like email or username.
        // This simple implementation relies on the ID.
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        // For entities, hashCode should also be based on the ID.
        // If ID is null (new entity), you might use a constant or a natural key.
        return id != null ? Objects.hash(id) : 0; // Or a constant like 31 if id is null
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", address='" + address + '\'' +
               ", role='" + role + '\'' +
               '}';
    }
}