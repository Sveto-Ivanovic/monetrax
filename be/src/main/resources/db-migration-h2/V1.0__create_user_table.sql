CREATE TABLE user_table (
                            user_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
                            user_email VARCHAR(254) NOT NULL UNIQUE,
                            created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            additional_info JSON NULL,
                            name VARCHAR(150) NULL,
                            surname VARCHAR(150) NULL,
                            user_name VARCHAR(150) NULL,
                            role VARCHAR(150) NULL,
                            date_of_birth DATE NULL,
                            password_hash VARCHAR(256) NOT NULL,
                            has_finished_onboarding BOOLEAN NOT NULL DEFAULT FALSE,
                            has_verified_email BOOLEAN NOT NULL DEFAULT FALSE,
                            last_logged_in TIMESTAMP WITH TIME ZONE NULL
);

INSERT INTO user_table (
    user_id,
    user_email,
    created_at,
    updated_at,
    additional_info,
    name,
    surname,
    user_name,
    role,
    date_of_birth,
    password_hash,
    has_finished_onboarding,
    has_verified_email,
    last_logged_in
)
VALUES (
           '7f3a9c21-6d84-4b17-a5e2-91c0f8d73b46',
           'sveto@gmail.com',
           '2025-06-29T20:47:00+02:00',
           '2026-08-15T20:47:00+02:00',
           '{"is_student":"yes"}',
           'Svetozar',
           'Ivanovic',
           'SveIvan',
           'admin',
           '1998-12-06',
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
           FALSE,
           FALSE,
           NULL
       );