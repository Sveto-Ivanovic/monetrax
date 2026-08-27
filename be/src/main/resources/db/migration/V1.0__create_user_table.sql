create table user_table (
                                                 user_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                                                 user_email varchar(254) not null unique
                                                 created_at timestamptz not null default now(),
                                                 updated_at timestamptz not null default now()
                                                 additional_info jsonb null,
                                                 name character varying(150) null,
                                                 surname character varying(150) null,
                                                 user_name character varying(150) null,
                                                 user_classification character varying(100) null,
                                                 date_of_birth date null,
                                                 password_hash character varying(256) not null,
                                                 user_permissions jsonb null,
                                                 salt_string character varying(64) null
);
