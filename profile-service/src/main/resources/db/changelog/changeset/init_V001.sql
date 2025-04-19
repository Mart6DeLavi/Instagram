CREATE TABLE user_profile (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(255) NOT NULL,
    avatar_url TEXT,
    number_of_posts INTEGER DEFAULT 0,
    number_of_subscribers INTEGER DEFAULT 0,
    number_of_subscriptions INTEGER DEFAULT 0,
    about_myself TEXT,
    is_public BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    is_online BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP
);