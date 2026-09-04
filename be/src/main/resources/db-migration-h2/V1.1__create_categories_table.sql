CREATE TABLE categories_table (
                                  category_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                                  user_id UUID NOT NULL,
                                  category_type VARCHAR(20) NOT NULL,
                                  name VARCHAR(150) NOT NULL,
                                  description VARCHAR(256),
                                  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  is_default BOOLEAN NOT NULL DEFAULT FALSE,

                                  CONSTRAINT category_type_check
                                      CHECK (category_type IN (
                                          'INCOME', 'EXPENSE', 'TRANSFER', 'ADJUSTMENT'
                                          )),

                                  CONSTRAINT set_foreign_key
                                      FOREIGN KEY (user_id)
                                          REFERENCES user_table(user_id),

                                  CONSTRAINT categories_user_name_unique
                                      UNIQUE (user_id, name)


);