# 🏢 BHEL Human Resource Management (HRM) System

This is a distributed Human Resource Management (HRM) system implemented using Java RMI (Remote Method Invocation), a Java Swing GUI, and backed by a MySQL database.

The system allows HR staff to register employees and generate reports, while employees can update their profiles, check leave balances, and apply for leave.

# 🛠️ Prerequisites

Before starting the application, ensure you have the following software installed and configured:

* **Java Development Kit (JDK): Version 8 or newer is required. Ensure the javac and java commands are accessible from your terminal (i.e., the JDK bin directory is added to your system's PATH).

* **MySQL Server: A running MySQL instance (e.g., local installation, XAMPP, or WAMP).

* **Installation Guide (Windows 11): For detailed steps on installing MySQL, refer to this guide: MySQL Installation Guide for Windows 11

* **MySQL JDBC Connector (Driver): The .jar file that allows Java to talk to MySQL.

Download Link: Download the JAR file for version 9.1.0 (or the latest stable version) here: MySQL Connector JAR Download

Action: Download the JAR file (e.g., mysql-connector-j-9.1.0.jar) and place it directly in your project's root folder.

Connecting MySQL to Java: For general guidance on setting up JDBC, refer to this tutorial: Connecting MySQL Database in Java using Eclipse

⚙️ Project Structure

| **File Name**           | **Description**                                            | **Role**           |
| ----------------------- | ---------------------------------------------------------- | ------------------ |
| `HRMInterface.java`     | The Java RMI interface (the contract).                     | **RMI**            |
| `Employee.java`         | Serializable data object for employee records.             | **Data Model**     |
| `LeaveApplication.java` | Serializable data object for leave requests.               | **Data Model**     |
| `HRMServer.java`        | RMI Server implementation with JDBC/MySQL logic.           | **Server**         |
| `HRMClient.java`        | Java Swing GUI application that interacts with the server. | **Client**         |
| `setup.sql`             | SQL script to create the necessary database and tables.    | **Database Setup** |

⚙️ 1. Database Setup (MySQL)

You must run the SQL script to create the database schema.

🧭 Step 1.1: Start MySQL Server

Ensure your MySQL service is running (e.g., start the MySQL module in your XAMPP/WAMP control panel).

🧾 Step 1.2: Execute SQL Script

Use a tool like MySQL Workbench, phpMyAdmin, or the MySQL Command Line Client to execute the following commands:

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

🧩 Step 1.3: Update Server Credentials

Open HRMServer.java and ensure the database connection constants match your local MySQL setup.
Replace the placeholder values with your specific credentials.

// Example from HRMServer.java
private static final String DB_URL = "jdbc:mysql://localhost:3306/hrm_db";
private static final String DB_USER = "root";     // <-- CHANGE IF NECESSARY
private static final String DB_PASS = "";         // <-- ENTER YOUR PASSWORD HERE


Configuration Summary:

Parameter	Example Value	Description
DB_URL	jdbc:mysql://localhost:3306/hrm_db	Change hrm_db if you used a different database name.
DB_USER	root	Your MySQL username.
DB_PASS	""	Your MySQL password (or empty string if none).
💻 2. Compilation and Execution

You will need two terminal windows (or PowerShell instances) in the project directory.

🧱 Step 2.1: Compile All Files

You must include the JDBC connector .jar file in the classpath during compilation.
(Use the version number you downloaded, e.g., 9.1.0.)

💠 Windows:
javac -cp ".;mysql-connector-j-9.1.0.jar" *.java

💠 macOS / Linux:
javac -cp ".:mysql-connector-j-9.1.0.jar" *.java

🖥️ Step 2.2: Start the RMI Server (Terminal 1)

Run the RMI server and registry. It connects to MySQL automatically.

Command (Windows):
java -cp ".;mysql-connector-j-9.1.0.jar" HRMServer

Expected Output:
RMI Registry started.
MySQL JDBC Driver loaded.
HRM Server is running and bound to 'HRMService'...

🪟 Step 2.3: Start the GUI Client (Terminal 2)

Run the client in a new terminal.

Command (All OS):
java -cp . HRMClient


The GUI should appear, allowing you to interact with the HRM system.

🚀 How to Use the Application

HR Setup:
Click “Login as HR Staff” → Go to “Register Employee” tab → Add a new employee.
Example:

IC/Passport: 123
Name: Jane Doe


Employee Login:
Return to the login screen → Enter employee IC (e.g., 123).

Employee Dashboard:

Update personal profile

View leave balance (default: 20)

Apply for leave

All actions are instantly saved to the MySQL database.

🧠 Technologies Used

Java RMI (Remote Method Invocation)

Java Swing (GUI)

MySQL (Database)

JDBC Connector

Object Serialization


<img width="588" height="497" alt="Screenshot 2025-10-27 205124" src="https://github.com/user-attachments/assets/275f13eb-6792-4482-994a-eaec4a9e366d" />
<img width="589" height="493" alt="Screenshot 2025-10-27 205115" src="https://github.com/user-attachments/assets/dd602b69-0125-4d70-9d0f-ac00c3151a53" />
<img width="583" height="496" alt="Screenshot 2025-10-27 205107" src="https://github.com/user-attachments/assets/0aaf4e5b-b3de-496e-8bec-e4b239c01701" />
<img width="583" height="491" alt="Screenshot 2025-10-27 205056" src="https://github.com/user-attachments/assets/c385c7ae-d515-48ae-8a1a-9122e0d2fc52" />
<img width="582" height="492" alt="Screenshot 2025-10-27 205044" src="https://github.com/user-attachments/assets/b174eb93-4472-4d21-9c21-30c0aa75dfda" />
<img width="585" height="491" alt="Screenshot 2025-10-27 205025" src="https://github.com/user-attachments/assets/3c16b53e-abf6-4d82-902f-f062d3868790" />


🧾 License This project is provided for educational purposes.
You can modify and use it for learning Java RMI and distributed systems.
