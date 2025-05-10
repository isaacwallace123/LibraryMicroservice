DROP TABLE IF EXISTS book;

CREATE TABLE IF NOT EXISTS book (
    id SERIAL PRIMARY KEY,
    bookid VARCHAR(50) UNIQUE NOT NULL,
    authorid VARCHAR(50) NOT NULL,

    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100),
    publisher VARCHAR(100),
    released TIMESTAMP,
    stock INTEGER
);