CREATE TABLE like_to_post (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, post_id)
);

CREATE TABLE like_to_comment (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    comment_id TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, comment_id)
);