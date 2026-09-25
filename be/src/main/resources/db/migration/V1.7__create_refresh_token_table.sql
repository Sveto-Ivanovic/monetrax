CREATE TABLE refresh_tokens (
                                refresh_token_id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                user_id              UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                                token_hash           VARCHAR(255) NOT NULL UNIQUE,
                                issued_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
                                expires_at           TIMESTAMPTZ NOT NULL,
                                revoked_at           TIMESTAMPTZ,
                                replaced_by_token_id UUID REFERENCES refresh_tokens(refresh_token_id),
                                user_agent           VARCHAR(255),
                                ip_address           VARCHAR(45)
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens (user_id);