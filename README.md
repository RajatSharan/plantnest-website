# 🌿 PlantNest — Cultivating Green Dreams, Delivered to Your Door!

Welcome to **PlantNest**, your digital sanctuary where the vibrant essence of nature flourishes. 🏡✨  
We've lovingly crafted a modern e-commerce platform to bring the calming beauty of the botanical world directly into your life.

From the gentle whisper of a peace lily to the resilient embrace of an outdoor oak, discover your next green companion — all through an experience as seamless and intuitive as nature's own rhythm. 🌱🛒

---

## ✨ Features That Bloom Just For You

| 🌼 Feature | 🌿 Description |
|-----------|----------------|
| **The Infinite Arbor** | 📚 Browse a lush plant catalog with rich descriptions and visuals. |
| **Secure Sanctuary** | 🔐 User registration, login, forgot/reset password – all secured by Spring Security. |
| **Gardener’s Basket** | 🛒 Add, remove, and manage items in a smooth shopping cart experience. |
| **Graceful Checkout** | 💳 Streamlined multi-step checkout with payment options like Cash on Delivery. |
| **User Conservatory** | 👤 Manage profile, view order history, and track your green journey. |
| **Responsive Design** | 📱 Beautifully adaptive UI across mobile, tablet, and desktop devices. |

---

## 🚀 Tech Stack – The Rich Soil Beneath

<details>
<summary><strong>🧠 Backend - The Strong Trunk</strong></summary>

- 🌳 **Java 17+** – Powering the core logic  
- 🌼 **Spring Boot 3.x** – For rapid development  
- 🛡️ **Spring Security** – Protecting your user data  
- 🍃 **Thymeleaf** – Elegant server-side rendering  
- 🛠️ **Maven** – For dependency management and build automation  

</details>

<details>
<summary><strong>🎨 Frontend - The Blooming Foliage</strong></summary>

- 🧱 **HTML5 + CSS3** – Structure and style  
- ⚡ **JavaScript** – For interactive magic  
- 💎 **Bootstrap 5** – Responsive layout and modern UI  
- 🌟 **Font Awesome 6** – Rich set of beautiful icons  

</details>

<details>
<summary><strong>🌱 Database - The Deep Roots</strong></summary>

- 🐬 **MySQL** – Storing plant info and user data with integrity  

</details>

---

## 📸 Vignettes from the Garden

### 📁 Project Structure – The Organized Roots

```plaintext
plantnest-website/
│
├── .idea/                     # IntelliJ project settings (auto-generated)
├── .vscode/                   # VS Code settings (optional)
├── src/
│   └── main/
│       ├── java/com/plantnest/
│       │   ├── config/        # Security and other configuration classes
│       │   ├── controller/    # Web controllers handling user interactions
│       │   ├── dto/           # Data Transfer Objects (for data between layers)
│       │   ├── model/         # Entity classes mapping to database tables
│       │   ├── repository/    # JPA repositories for data access
│       │   ├── security/      # Custom filters, JWTs, authentication
│       │   ├── service/       # Business logic and service layer
│       │   └── PlantNestApplication.java  # Spring Boot main class
│       └── resources/
│           ├── static/        # Static assets like CSS, JS, images
│           ├── templates/     # Thymeleaf HTML templates
│           └── application.properties  # Configuration settings
│
├── target/                    # Compiled classes and build artifacts
├── .gitignore                 # Files and directories ignored by Git
├── pom.xml                    # Maven project configuration and dependencies
└── README.md                  # 🌿 You are here!

```

### 🖼️ Visuals To Add

Imagine seeing:
- 🧭 **Project structure** like sturdy branches organizing your codebase  
- 🛍️ **Checkout flow** with serene, user-friendly navigation  
- 📝 **Secure registration** inviting users to begin their journey  


---

## 🛠️ Getting Started – Sowing the Seeds

<details>
<summary><strong>🌼 Prerequisites</strong></summary>

- Java JDK 17+  
- Maven  
- IDE (IntelliJ IDEA, VS Code, Eclipse recommended)  
- MySQL  

</details>

<details>
<summary><strong>🌱 Installation Steps</strong></summary>

1. **Clone the Repository**
   \`\`\`bash
   git clone https://github.com/your-username/plantnest-website.git
   cd plantnest-website
   \`\`\`

2. **Set up the Database**  
   Create a MySQL database named \`plantnest_db\` and update your \`application.properties\` with DB credentials.

3. **Run the Application**
   \`\`\`bash
   mvn spring-boot:run
   \`\`\`

4. Visit \`http://localhost:8082\` in your browser to explore 🌿

</details>
