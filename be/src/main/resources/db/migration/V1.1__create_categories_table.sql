CREATE TYPE category_kind  AS ENUM ('INCOME', 'EXPENSE', 'TRANSFER', 'ADJUSTMENT');

create table categories_table (
                            category_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                            user_id uuid not null ,
                            category_type category_kind  not null ,
                            name character varying(150) not null,
                            description character varying(256),
                            created_at timestamptz not null default now(),
                            updated_at timestamptz not null default now(),
                            is_default boolean not null default false,

                            CONSTRAINT set_foreign_key
                            FOREIGN KEY (user_id)
                            REFERENCES user_table(user_id),

                            UNIQUE (user_id, name)
);