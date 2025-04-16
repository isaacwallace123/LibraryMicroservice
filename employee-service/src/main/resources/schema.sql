DROP TABLE IF EXISTS employees;

CREATE TABLE IF NOT EXISTS employees (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,

    employeeid VARCHAR (36) NOT NULL UNIQUE,

    first_name VARCHAR (50),
    last_name VARCHAR (50),

    dob DATE,

    email VARCHAR(255),
    title VARCHAR(100),
    salary DECIMAL(10, 2)
);