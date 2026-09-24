CREATE TABLE customers
(
    id            UUID PRIMARY KEY,
    email         VARCHAR(320) NOT NULL UNIQUE,
    full_name     VARCHAR(100) NOT NULL,
    phone_number  VARCHAR(16)  NOT NULL,
    country_code  VARCHAR(2)   NOT NULL,
    status        VARCHAR(16)  NOT NULL,
    registered_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    version       BIGINT       NOT NULL
);
