# 🎫 Ticket Management System

This project is a full-stack web application designed to streamline the process of managing customer support tickets. It features a robust Spring Boot backend for handling business logic and data persistence, coupled with a responsive vanilla JavaScript frontend for an intuitive user experience.

## 🚀 Features

### 🧑‍💼 User Authentication & Authorization
- Secure login system with role-based access (CUSTOMER and ENGINEER).
- Role-based access control (RBAC).

### 👨‍💻 Role-Based Functionalities
- **Customers:**
  - Create and view only their own tickets.
  - Edit their own comments.

- **Engineers:**
  - View all tickets.
  - Acknowledge, update, and delete tickets.
  - Assign engineers and update status/resolution.
  - Add internal comments.
  - Manage user accounts (customers and engineers).

### 📝 Ticket Management
- **Create Tickets**: Customers can create tickets with descriptions.
- **View Tickets**: Both roles can view tickets (with access control).
- **Acknowledge Tickets**: Engineers can assign themselves to tickets.
- **Update Tickets**: Engineers can edit all ticket details.
- **Delete Tickets**: Engineers can delete tickets.
- **Comments**: Customers and engineers can add role-based comments.

### 🤖 LLM Integration (Gemini API)
- **Ticket Summarization**: Engineers can generate a brief summary of a ticket.
- **Draft Response Generation**: Draft initial responses for ticket resolution.

## 🛠️ Technologies Used

### Backend
- **Spring Boot** (Java)
- **MySQL**
- **Spring Data JPA / Hibernate**
- **Lombok**
- **Maven**

### Frontend
- **HTML5**, **CSS3**
- **Vanilla JavaScript**
- **Lucide Icons** (for UI elements)
