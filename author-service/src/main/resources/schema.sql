DROP TABLE IF EXISTS authors;

CREATE TABLE IF NOT EXISTS authors(
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,

    authorid VARCHAR(50) UNIQUE,

    first_name VARCHAR(50),
    last_name VARCHAR(50),

    pseudonym VARCHAR(50)
);