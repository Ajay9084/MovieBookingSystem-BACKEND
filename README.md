# Movie Booking System - Backend

This is the backend for the Movie Booking System built using Spring Boot.

## Tech Stack
- Java
- Spring Boot
- Spring Security
- JWT Authentication
- PostgreSQL (Supabase)
- Docker

## Features
- User Authentication
- Movie Management
- Booking System
- Role-based authorization

## Run Locally

Clone the repository:

git clone https://github.com/Ajay9084/MovieBookingSystem-BACKEND.git

Build the project:

mvn clean package

Run the Docker container:

docker build -t movie-app .
docker run -p 8080:8080 movie-app
