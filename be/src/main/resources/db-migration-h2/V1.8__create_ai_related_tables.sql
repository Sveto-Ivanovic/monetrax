
CREATE TABLE IF NOT EXISTS api_keys (
    key_id UUID NOT NULL DEFAULT RANDOM_UUID(),
    user_id UUID NOT NULL,
    key_type  VARCHAR(50)  NOT NULL,
    api_key_encrypted VARCHAR(2048)  NOT NULL,
    key_last4 VARCHAR(4),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_used_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_api_keys PRIMARY KEY (key_id),
    CONSTRAINT uq_api_keys_user_type_label UNIQUE (user_id, key_type),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES user_table(user_id),
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


CREATE TABLE IF NOT EXISTS message_history (
    message_id UUID NOT NULL DEFAULT RANDOM_UUID(),
    user_id UUID NOT NULL,
    key_id  UUID,
    provider VARCHAR(50) NOT NULL,
    model VARCHAR(100),
    raw_message VARCHAR(1000000) NOT NULL,
    structured_output JSON,
    status VARCHAR(20) NOT NULL,
    error_message VARCHAR(2000),
    input_tokens INTEGER,
    output_tokens INTEGER,
    created_at TIMESTAMP WITH TIME ZONE  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_message_history PRIMARY KEY (message_id),
    CONSTRAINT fk_message_history_key FOREIGN KEY (key_id)
    REFERENCES api_keys (key_id) ON DELETE SET NULL,
    CONSTRAINT fk_user_id_to_msg_id FOREIGN KEY (user_id) REFERENCES user_table(user_id),
    CONSTRAINT ck_message_history_status CHECK (status IN ( 'SUCCESS', 'FAILED'))
    );

CREATE INDEX IF NOT EXISTS idx_message_history_user_created
    ON message_history (user_id);
