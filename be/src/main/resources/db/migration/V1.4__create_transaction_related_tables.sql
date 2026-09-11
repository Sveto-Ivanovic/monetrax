CREATE TYPE adjustment_kind  AS ENUM ('DEDUCTION', 'ADDITION');

CREATE TABLE transactions (
                              transaction_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                              account_id UUID NOT NULL REFERENCES accounts(account_id) ON DELETE CASCADE,
                              name VARCHAR(150) NOT NULL,
                              description VARCHAR(500),
                              amount NUMERIC(14,2) NOT NULL,
                              amount_native NUMERIC(14,2) NOT NULL,
                              currency CHAR(3) NOT NULL,
                              category_type category_kind  not null,
                              created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE transaction_categories (
                                        transaction_id UUID NOT NULL REFERENCES transactions(transaction_id) ON DELETE CASCADE,
                                        category_id UUID NOT NULL REFERENCES categories(category_id),
                                        PRIMARY KEY (transaction_id, category_id)
);

CREATE TABLE transaction_line_items (
                                        line_item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                        transaction_id UUID NOT NULL REFERENCES transactions(transaction_id) ON DELETE CASCADE,
                                        product_name VARCHAR(150) NOT NULL,
                                        amount NUMERIC(14,2) NOT NULL
);

CREATE TABLE transaction_additional_info (
                                        transaction_info_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                         transaction_id UUID NOT NULL REFERENCES transactions(transaction_id) ON DELETE CASCADE,
                                         kind adjustment_kind NOT NULL,
                                         label VARCHAR(100) NOT NULL,
                                         amount NUMERIC(14,2) NOT NULL
);