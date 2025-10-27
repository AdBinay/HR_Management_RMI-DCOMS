🏢 BHEL Human Resource Management (HRM) System

This is a distributed Human Resource Management (HRM) system implemented using Java RMI (Remote Method Invocation), a Java Swing GUI, and backed by a MySQL database.

The system allows HR staff to register employees and generate reports, while employees can update their profiles, check leave balances, and apply for leave.

🛠️ Prerequisites

Before starting the application, ensure you have the following software installed and configured:

Java Development Kit (JDK): Version 8 or newer is required. Ensure the javac and java commands are accessible from your terminal (i.e., the JDK bin directory is added to your system's PATH).

MySQL Server: A running MySQL instance (e.g., local installation, XAMPP, or WAMP).

Installation Guide (Windows 11): For detailed steps on installing MySQL, refer to this guide: MySQL Installation Guide for Windows 11

MySQL JDBC Connector (Driver): The .jar file that allows Java to talk to MySQL.

Download Link: Download the JAR file for version 9.1.0 (or the latest stable version) here: MySQL Connector JAR Download

Action: Download the JAR file (e.g., mysql-connector-j-9.1.0.jar) and place it directly in your project's root folder.

Connecting MySQL to Java: For general guidance on setting up JDBC, refer to this tutorial: Connecting MySQL Database in Java using Eclipse

⚙️ Project Structure

The project contains 6 files:

File Name

Description

Role

HRMInterface.java

The Java RMI interface (the contract).

RMI

Employee.java

Serializable data object for employee records.

Data Model

LeaveApplication.java

Serializable data object for leave requests.

Data Model

HRMServer.java

RMI Server implementation with JDBC/MySQL logic.

Server

HRMClient.java

Java Swing GUI application that interacts with the server.

Client

setup.sql

SQL script to create the necessary database and tables.

Database Setup

1. Database Setup (MySQL)

You must run the SQL script to create the database schema.

Step 1.1: Start MySQL Server

Ensure your MySQL service is running (e.g., start the MySQL module in your XAMPP/WAMP control panel).

Step 1.2: Execute SQL Script

Use a tool like MySQL Workbench, phpMyAdmin, or the MySQL Command Line Client to execute the following SQL commands:

CREATE DATABASE IF NOT EXISTS hrm_db;
USE hrm_db;

CREATE TABLE IF NOT EXISTS employees (
    ic_passport VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    profile_details TEXT,
    family_details TEXT,
    leave_balance INT DEFAULT 20
);

CREATE TABLE IF NOT EXISTS leave_applications (
    app_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_ic VARCHAR(50),
    leave_date VARCHAR(20),
    reason TEXT,
    status VARCHAR(20) DEFAULT 'Pending',
    FOREIGN KEY (employee_ic) REFERENCES employees(ic_passport)
        ON DELETE CASCADE
);


Step 1.3: Update Server Credentials

Open HRMServer.java and ensure the database connection constants match your local MySQL setup. You must replace the placeholder values in the Java code with your specific credentials.

// Example from HRMServer.java
private static final String DB_URL = "jdbc:mysql://localhost:3306/hrm_db";
private static final String DB_USER = "root";     // <-- CHANGE IF NECESSARY
private static final String DB_PASS = "";         // <-- ENTER YOUR PASSWORD HERE


The values you need to configure are:

DB_URL: jdbc:mysql://localhost:3306/hrm_db (or change hrm_db if you used a different database name).

DB_USER: yourUsername (e.g., root).

DB_PASS: yourPassword (e.g., your MySQL password, or empty string "" if you don't use one locally).

2. Compilation and Execution

You will need two separate terminal windows (or two PowerShell windows) running in the project directory.

Step 2.1: Compile All Files

You must include the JDBC connector JAR in the classpath for compilation. Use the version number you downloaded (e.g., 9.1.0).

Command (Windows):

javac -cp ".;mysql-connector-j-9.1.0.jar" *.java


Command (macOS/Linux):

javac -cp ".:mysql-connector-j-9.1.0.jar" *.java


Step 2.2: Start the RMI Server (Terminal 1)

This terminal will host the RMI registry and the server application, connecting to MySQL.

Command (Windows):

java -cp ".;mysql-connector-j-9.1.0.jar" HRMServer


Expected Output:

RMI Registry started.
MySQL JDBC Driver loaded.
HRM Server is running and bound to 'HRMService'...


Step 2.3: Start the GUI Client (Terminal 2)

Open the second terminal window and run the client.

Command (All OS):

java HRMClient


The graphical user interface (GUI) should appear, allowing you to interact with the system.

🚀 How to Use the Application

HR Setup: Click "Login as HR Staff".

Go to the "Register Employee" tab and add a new employee (e.g., IC/Passport: 123, Name: Jane Doe).

Employee Login: Return to the login screen and enter the new employee's IC (123).

You can now update the profile, check the default leave balance (20), and apply for leave. All changes are immediately persisted in the MySQL database.
