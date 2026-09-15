
CREATE TABLE transactions (
                              transaction_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                              user_id UUID NOT NULL REFERENCES user_table(user_id) ON DELETE CASCADE,
                              account_id UUID NOT NULL REFERENCES accounts(account_id) ON DELETE CASCADE,
                              name VARCHAR(150) NOT NULL,
                              description VARCHAR(500),
                              amount NUMERIC(14,2) NOT NULL,
                              amount_native NUMERIC(14,2) NOT NULL,
                              currency VARCHAR(3) NOT NULL,
                              conversion_factor DECIMAL(18,8),
                              category_type VARCHAR(50) NOT NULL CHECK (category_type IN ('INCOME', 'EXPENSE', 'TRANSFER_FROM', 'ADJUSTMENT_PLUS', 'TRANSFER_TO', 'ADJUSTMENT_MINUS')),
                              created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transaction_categories (
                                        transaction_id UUID NOT NULL REFERENCES transactions(transaction_id) ON DELETE CASCADE,
                                        category_id UUID NOT NULL REFERENCES categories_table(category_id),
                                        PRIMARY KEY (transaction_id, category_id)
);

CREATE TABLE transaction_line_items (
                                        line_item_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                                        transaction_id UUID NOT NULL REFERENCES transactions(transaction_id) ON DELETE CASCADE,
                                        product_name VARCHAR(150) NOT NULL,
                                        amount NUMERIC(14,2) NOT NULL
);

CREATE TABLE transaction_additional_info(
                                        transaction_info_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                                         transaction_id UUID NOT NULL REFERENCES transactions(transaction_id) ON DELETE CASCADE,
                                         kind VARCHAR(50) NOT NULL CHECK (kind IN ('DEDUCTION', 'ADDITION')),
                                         label VARCHAR(100) NOT NULL,
                                         amount NUMERIC(14,2) NOT NULL
);