# Ticket Service Management System V100

## Overview
A comprehensive ticket management system built with Spring Boot that allows customers to create tickets and engineers to manage them.

## Features
- **Role-based Access Control**: Customer, Engineer, Admin Engineer
- **Ticket Management**: Create, update, view, filter tickets
- **Comment System**: Add comments to tickets
- **File Attachments**: Upload and download files
- **User Management**: Admin can manage users and categories
- **Search & Filter**: Advanced filtering capabilities

## User Roles
- **Customer**: Create tickets, add comments, upload attachments, view own tickets
- **Engineer**: Manage tickets, acknowledge tickets, update status, delete tickets
- **Admin Engineer**: All engineer capabilities + user management + category management

## Default Credentials
- **Admin Engineer**: `default_engineer` / `admin123`
- **Sample Customer**: `customer1` / `pass123`
- **Sample Engineer**: `engineer1` / `pass123`

## Technology Stack
- Java 11
- Spring Boot 2.7.0
- Spring Data JPA
- MySQL Database
- Lombok
- Maven

## Setup Instructions
1. Ensure MySQL is running on localhost:3306
2. Update database credentials in `application.properties` if needed
3. Run `mvn clean install`
4. Run `mvn spring-boot:run`
5. Access the application at `http://localhost:8080`

## API Endpoints
### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `GET /api/auth/current-user` - Get current user

### Customer APIs
- `POST /api/customer/tickets` - Create ticket
- `GET /api/customer/tickets` - Get my tickets
- `POST /api/customer/tickets/{id}/comments` - Add comment
- `POST /api/customer/tickets/{id}/attachments` - Upload attachment

### Engineer APIs
- `GET /api/engineer/tickets` - Get available tickets
- `POST /api/engineer/tickets/{id}/acknowledge` - Acknowledge ticket
- `PUT /api/engineer/tickets/{id}` - Update ticket
- `DELETE /api/engineer/tickets/{id}` - Delete ticket

### Admin APIs
- `POST /api/admin/customers` - Create customer
- `POST /api/admin/engineers` - Create engineer
- `POST /api/admin/categories` - Create category

## Database Schema
The application will automatically create the required tables on startup.
