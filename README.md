# BimorShop Marketplace

**BimorShop** is a full-stack marketplace application that allows users to buy and sell products. It features a dynamic catalog, user authentication, product management for sellers, and a favorites system. The application provides a REST API for seamless interaction between the frontend and backend.

## Key Features

### Marketplace & Products
* **Dynamic Catalog:** View products with images, prices, and categories.
* **Smart Search:** Search products by name (case-insensitive).
* **Product Details:** Modal view with detailed information, including seller contacts (Phone, Email).
* **Seller Badge:** Identify who is selling the item directly in the product card.

### User Management
* **Authentication:** Secure Registration and Login system.
* **Profile Management:** View personal info and manage listed products.
* **Role System:** Every user can be both a buyer and a seller.

### Personalization
* **Favorites:** Add/Remove products to/from your wishlist.
* **My Products:** Manage (Edit/Delete) your own listings easily.

### REST API
* **OpenAPI/Swagger:** Fully documented API endpoints.
* **CRUD Operations:** Full support for creating, reading, updating, and deleting accounts, products, and orders.

---

### APPLICATION IMAGES

## Registration window

<img width="513" height="579" alt="image" src="https://github.com/user-attachments/assets/4d321350-d433-4bf0-aa79-fae901db0870" />


## Tech Stack

### Backend (Spring Boot)
* **Language:** Java 17+
* **Framework:** Spring Boot 
* **Database:** PostgreSQL
* **ORM:** Hibernate / Spring Data JPA
* **Documentation:** SpringDoc OpenAPI (Swagger)
* **Build System:** Maven

### Frontend (Vanilla JS)
* **Core:** HTML5, CSS3, JavaScript (ES6+)
* **Styling:** Custom CSS with Responsive Grid
* **Icons:** FontAwesome 6.4.0
* **Architecture:** Component-based rendering (DOM manipulation), Fetch API for HTTP requests.

---

## Installation and Launch

### Prerequisites
* Java 17 or newer
* Maven
* PostgreSQL
* A modern Web Browser (Chrome, Firefox, Edge)

### Backend Setup

1.  **Navigate to the backend directory:**
    ```bash
    cd backend
    ```

2.  **Configure Database:**
    Ensure you have a PostgreSQL database created (e.g., `bimorshop_db`). Update `application.properties` if necessary.

3.  **Build the project:**
    ```bash
    mvn clean install
    ```

4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    *The server will start on port `8080`.*

### Frontend Setup

Since the frontend is built with **Vanilla JS**, you don't need `npm` or `node_modules`.

1.  **Navigate to the frontend directory:**
    ```bash
    cd frontend
    ```

2.  **Launch the Interface:**
    * **Option A (Simple):** Open `index.html` directly in your browser.
    * **Option B (Recommended):** Use a live server (like VS Code "Live Server" extension) to serve files on port `3000` (or configure CORS in backend accordingly).

---

## API Documentation

After launching the backend application, the interactive Swagger documentation is available at:
**http://localhost:8080/swagger-ui.html**

---

## Quick Start

1.  **Start PostgreSQL** database service.
2.  **Run Backend:** Execute `mvn spring-boot:run` in the backend folder.
3.  **Open Frontend:** Launch `index.html`.
4.  **Register:** Create a new account via the "Войти" -> "Регистрация" button.
5.  **Sell:** Go to Profile -> "Добавить" to list your first item (e.g., "iPhone 15", 3000 BYN).
6.  **Search:** Use the search bar on the home page to find your item.

---

## Configuration

### Backend Configuration
Edit `src/main/resources/application.properties` to match your environment:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bimorshop_db
spring.datasource.username=postgres
spring.datasource.password=your_password
server.port=8080
spring.jpa.hibernate.ddl-auto=update
