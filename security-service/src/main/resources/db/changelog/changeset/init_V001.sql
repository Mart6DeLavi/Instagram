CREATE TABLE jwt_backup(
    id BIGSERIAL PRIMARY KEY,
    username TEXT NOT NULL,
    token TEXT NOT NULL
);