CREATE TABLE accounts (
                          account_id  UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                          user_id UUID NOT NULL,
                          CONSTRAINT fk_accounts_user
                              FOREIGN KEY (user_id)
                                  REFERENCES user_table(user_id)
                                  ON DELETE CASCADE,

                          name VARCHAR(100) NOT NULL,
                          description VARCHAR(255),
                          current_balance DECIMAL(14,2) NOT NULL DEFAULT 0.00,
                          currency VARCHAR(3) NOT NULL,
                          institution_name VARCHAR(100),
                          account_number_masked VARCHAR(4) DEFAULT 'XXXX',
                          is_active BOOLEAN NOT NULL DEFAULT TRUE,
                          is_archived BOOLEAN NOT NULL DEFAULT FALSE,
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);