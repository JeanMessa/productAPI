CREATE TABLE users(
    user_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(250) NOT NULL,
    password VARCHAR(250) NOT NULL
)