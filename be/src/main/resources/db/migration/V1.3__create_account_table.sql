CREATE TABLE accounts (
                          account_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                          name VARCHAR(100) NOT NULL,
                          description VARCHAR(255),
                          current_balance NUMERIC(14,2) NOT NULL DEFAULT 0,
                          currency CHAR(3) NOT NULL,
                          institution_name VARCHAR(100),
                          account_number_masked VARCHAR(4) default 'XXXX',
                          is_active BOOLEAN NOT NULL DEFAULT TRUE,
                          is_archived BOOLEAN NOT NULL DEFAULT FALSE,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);