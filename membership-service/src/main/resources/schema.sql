DROP TABLE IF EXISTS members;

CREATE TABLE IF NOT EXISTS members (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    memberid VARCHAR(50) UNIQUE,

    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(50),

    street VARCHAR(50),
    city VARCHAR(50),
    postal VARCHAR(50),
    province VARCHAR(50),

    phone_number VARCHAR(50),
    phone_type VARCHAR(50)
);