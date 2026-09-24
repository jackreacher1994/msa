CREATE TABLE countries
(
    id        UUID PRIMARY KEY,
    code      VARCHAR(2)   NOT NULL UNIQUE,
    name      VARCHAR(100) NOT NULL,
    dial_code VARCHAR(5)   NOT NULL,
    active    BOOLEAN      NOT NULL
);
