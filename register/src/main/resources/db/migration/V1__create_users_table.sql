-- SQL Migration Script for creating users table
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    age INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
