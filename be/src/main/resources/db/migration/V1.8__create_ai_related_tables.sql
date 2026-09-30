CREATE TABLE IF NOT EXISTS api_keys (
                                        key_id            UUID          NOT NULL DEFAULT gen_random_uuid(),
    user_id           UUID          NOT NULL,
    key_type          VARCHAR(50)   NOT NULL,
    api_key_encrypted VARCHAR(2048) NOT NULL,
    key_last4         VARCHAR(4),
    is_active         BOOLEAN       NOT NULL DEFAULT TRUE,
    last_used_at      TIMESTAMPTZ,
    created_at        TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_api_keys PRIMARY KEY (key_id),
    CONSTRAINT uq_api_keys_user_type_label UNIQUE (user_id, key_type),
    CONSTRAINT fk_api_keys_user FOREIGN KEY (user_id) REFERENCES user_table (user_id),
    CONSTRAINT ck_api_keys_type CHECK (key_type IN (
                                       'GEMINI_API_KEY',
                                       'GROQ_API_KEY',
                                       'CLAUDE_API_KEY',
                                       'OPENAI_API_KEY',
                                       'MISTRAL_API_KEY',
                                       'DEEPSEEK_API_KEY',
                                       'OPENROUTER_API_KEY',
                                       'COHERE_API_KEY',
                                       'PERPLEXITY_API_KEY',
                                       'XAI_API_KEY'
                                                   ))
    );

CREATE INDEX IF NOT EXISTS idx_api_keys_user_id ON api_keys (user_id);

DROP TRIGGER IF EXISTS trg_api_keys_updated_at ON api_keys;
CREATE TRIGGER trg_api_keys_updated_at
    BEFORE UPDATE ON api_keys
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();


CREATE TABLE IF NOT EXISTS message_history (
                                               message_id        UUID          NOT NULL DEFAULT gen_random_uuid(),
    user_id           UUID          NOT NULL,
    key_id            UUID,
    provider          VARCHAR(50)   NOT NULL,
    model             VARCHAR(100),
    raw_message       TEXT          NOT NULL,
    structured_output JSONB,
    status            VARCHAR(20)   NOT NULL,
    error_message     VARCHAR(2000),
    input_tokens      INTEGER,
    output_tokens     INTEGER,
    created_at        TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_message_history PRIMARY KEY (message_id),
    CONSTRAINT fk_message_history_key FOREIGN KEY (key_id)
    REFERENCES api_keys (key_id) ON DELETE SET NULL,
    CONSTRAINT fk_message_history_user FOREIGN KEY (user_id)
    REFERENCES user_table (user_id),
    CONSTRAINT ck_message_history_status CHECK (status IN ('SUCCESS', 'FAILED'))
    );

CREATE INDEX IF NOT EXISTS idx_message_history_user_id
    ON message_history (user_id);

DROP TRIGGER IF EXISTS trg_message_history_updated_at ON message_history;
CREATE TRIGGER trg_message_history_updated_at
    BEFORE UPDATE ON message_history
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();