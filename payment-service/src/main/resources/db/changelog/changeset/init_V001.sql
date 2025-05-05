CREATE TABLE payment_transactions (
                                      id BIGSERIAL PRIMARY KEY,
                                      payment_intent_id VARCHAR(255) NOT NULL,
                                      user_id BIGINT NOT NULL,
                                      amount BIGINT NOT NULL,
                                      currency VARCHAR(10),
                                      status VARCHAR(50),
                                      failure_reason TEXT,
                                      created_at TIMESTAMP,
                                      updated_at TIMESTAMP
);
