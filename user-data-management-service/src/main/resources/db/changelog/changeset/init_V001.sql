CREATE TABLE  users (
                                    id BIGSERIAL PRIMARY KEY,
                                    first_name VARCHAR(255) NOT NULL,
                                    last_name VARCHAR(255) NOT NULL,
                                    username VARCHAR(255) NOT NULL,
                                    email VARCHAR(255) NOT NULL,
                                    password VARCHAR(255) NOT NULL,
                                    number_of_posts INTEGER NOT NULL,
                                    number_of_subscribers INTEGER NOT NULL,
                                    number_of_subscriptions INTEGER NOT NULL,
                                    about_myself TEXT,
                                    sex VARCHAR(50) NOT NULL,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    CONSTRAINT user_unique UNIQUE (username, email)
);