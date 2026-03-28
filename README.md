# 🛒 Sellio - Comprehensive E-Commerce & Advertisement Platform

**Sellio** is a robust advertisement platform designed for both individual sellers and commercial stores to list and manage their items. This project is built with a focus on modern backend architecture, secure authentication, and scalable data management practices using a Spring Boot and Gradle environment.

---

## 🚀 Tech Stack

The platform is powered by a high-performance stack:

* **Framework:** Java / Spring Boot
* **Build Tool:** Gradle
* **Database:** PostgreSQL
* **Database Migration:** Liquibase
* **Real-time Values:** Redis
* **Security:** Spring Security & JWT (Stateless authentication with Access/Refresh tokens)
* **Media Management:** Cloudinary (Cloud hosting for product images)
* **Maps Integration:** Google Maps API (For shop locations)
* **Communication:** Spring Mail (SMTP integration for notifications)

---

## ✨ Key Features

* **Multi-Role System:** Support for Individual Sellers, Stores, Admins, and a **Super Admin** for system management.
* **Schema Evolution:** Automated database migrations and versioning with **Liquibase**.
* **Advanced Media Handling:** Multi-part file support (up to 10MB) with direct Cloudinary integration.
* **Security:** Dual-token JWT system (Access & Refresh) for enhanced security and better UX.
* **Location Awareness:** Accurate store address mapping using Google Maps API.
* **Performance:** Strategic caching and optimized PostgreSQL queries with Hibernate batch processing.

---

## 🛠️ Configuration & Setup

The application uses an `application.yml` structure and relies on environment variables for security.

### Prerequisites
* JDK 21
* PostgreSQL & Redis (local or containerized)
* Cloudinary and Google Maps API keys

## 🏗️ Database Migration
This project uses **Liquibase** to manage database changes. All changelogs are located in:
`src/main/resources/db/changelog/`

When you start the application, Liquibase will automatically sync the schema with your PostgreSQL instance.