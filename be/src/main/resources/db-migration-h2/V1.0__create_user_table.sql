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