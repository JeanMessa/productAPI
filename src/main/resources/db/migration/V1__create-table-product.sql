CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE product(
    product_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    price DOUBLE PRECISION NOT NULL
)