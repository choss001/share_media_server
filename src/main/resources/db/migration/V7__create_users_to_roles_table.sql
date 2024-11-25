CREATE TABLE user_to_sec_role (
    id bigint PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255)
);
