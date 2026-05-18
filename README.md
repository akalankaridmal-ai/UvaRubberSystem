# 🌿 Uva Rubber Management System (ERP)

A comprehensive, desktop-based Enterprise Resource Planning (ERP) solution designed for regional rubber collection centers in Sri Lanka. This application digitizes and automates manual raw latex weight collection, automates complex local pricing/tax structures, and handles dynamic financial reporting for commercial bank systems.

## 🚀 Key Features

- **Secure Access Control:** Role-based authentication framework backed by a relational user registry.
- **Dynamic Daily Collection Entry:** Automated computation of **Dry Rubber Content (DRC %)** and precise **Dry Weight (KG)** metrics derived dynamically from Metrolac readings.
- **Live Operation Dashboard:** Bottom-panel real-time operational status reporting displaying cumulative metrics (Liters & Dry KG) for the active calendar date.
- **Financial Cycle Management:** 15-day batch settlement engine with localized **"Gross-Up" pricing** configurations, automated **Stamp Duty** deduction calculations, and manual exceptional-rate overrides per supplier.
- **Sri Lankan Banking Integration:** Automated formatting targeting commercial ledger compliance (3-digit spacing e.g., `150 750.00`) with dynamic bulk filtering and printing configurations for **Bank of Ceylon (BOC)** and **People's Bank** credit letters.

## 🛠️ Tech Stack & Architecture

- **Language & GUI Framework:** Java SE 8+ utilizing Java Swing & Abstract Window Toolkit (AWT)
- **Database Connectivity:** Java Database Connectivity (JDBC) API
- **Data Persistence:** MySQL Relational Database Management System (RDBMS)
- **Design Pattern:** Architecture segmented into a robust Layered Pattern:
  - `com.uvarubber.view` (User Interface Frames)
  - `com.uvarubber.service` (Domain Logic & Mathematical Engines)
  - `com.uvarubber.dao` (Data Access Objects executing parameterized SQL)
  - `com.uvarubber.model` (Plain Old Java Objects mapping corporate entities)

---

## 📅 Database Schema

The core relational architecture maps out high-throughput daily transactions against rigid operational supplier assets:

```sql
CREATE DATABASE uva_rubber_system;
USE uva_rubber_system;

-- 1. Suppliers Registry Table
CREATE TABLE suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(255) NOT NULL,
    nic_no VARCHAR(20),
    bank_name VARCHAR(100), -- E.g., 'BOC', 'PPLS', 'RDB'
    account_no VARCHAR(50),
    branch_name VARCHAR(100),
    bank_code INT,
    route_name VARCHAR(100)
);

-- 2. Daily Transactions Ledger
CREATE TABLE daily_collections (
    collection_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id INT,
    collection_date DATE NOT NULL,
    liters DOUBLE NOT NULL,
    metrolac_reading INT NOT NULL,
    drc_percentage DOUBLE,
    dry_kg DOUBLE,
    payment_status ENUM('Pending', 'Paid') DEFAULT 'Pending',
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE CASCADE
);

-- 3. Security & Access Credentials Registry
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'Admin'
);
