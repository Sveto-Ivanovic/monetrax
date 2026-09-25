CREATE TABLE refresh_tokens (
                                refresh_token_id     UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                                user_id              UUID NOT NULL,
                                token_hash           VARCHAR(255) NOT NULL UNIQUE,
                                issued_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                expires_at           TIMESTAMP WITH TIME ZONE NOT NULL,
                                revoked_at           TIMESTAMP WITH TIME ZONE,
                                replaced_by_token_id UUID,
                                user_agent           VARCHAR(255),
                                ip_address           VARCHAR(45),
                                CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                                CONSTRAINT fk_refresh_tokens_replaced_by FOREIGN KEY (replaced_by_token_id) REFERENCES refresh_tokens(refresh_token_id)
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens (user_id);