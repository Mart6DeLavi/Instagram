CREATE TABLE calls (
                       id BIGSERIAL PRIMARY KEY,
                       caller_id BIGINT NOT NULL,
                       callee_id BIGINT NOT NULL,
                       type VARCHAR(10) NOT NULL CHECK (type IN ('AUDIO', 'VIDEO')),
                       status VARCHAR(10) NOT NULL CHECK (status IN ('CREATED', 'RINGING', 'ACCEPTED', 'REJECTED', 'ENDED')),
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       accepted_at TIMESTAMP,
                       ended_at TIMESTAMP
);
