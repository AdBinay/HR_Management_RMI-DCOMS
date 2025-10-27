-- Create a new database for the project
CREATE DATABASE IF NOT EXISTS hrm_db;

-- Use the new database
USE hrm_db;

-- Create the employees table
CREATE TABLE IF NOT EXISTS employees (
    ic_passport VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    profile_details TEXT,
    family_details TEXT,
    leave_balance INT DEFAULT 20
);

-- Create the leave_applications table
CREATE TABLE IF NOT EXISTS leave_applications (
    app_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_ic VARCHAR(50),
    leave_date VARCHAR(20), -- Using VARCHAR for simplicity, DATE is also good
    reason TEXT,
    status VARCHAR(20) DEFAULT 'Pending',
    FOREIGN KEY (employee_ic) REFERENCES employees(ic_passport)
        ON DELETE CASCADE -- If an employee is deleted, remove their leave apps
);

-- Optional: Create a dedicated user for your Java app
-- (Good practice for security)
-- CREATE USER 'hrm_user'@'localhost' IDENTIFIED BY 'YourStrongPassword123!';
-- GRANT ALL PRIVILEGES ON hrm_db.* TO 'hrm_user'@'localhost';
-- FLUSH PRIVILEGES;
