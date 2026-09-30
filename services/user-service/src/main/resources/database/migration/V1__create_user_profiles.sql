CREATE TABLE user_profiles (
                               user_id UUID PRIMARY KEY,

                               first_name VARCHAR(100) NOT NULL,
                               last_name VARCHAR(100),

                               date_of_birth DATE,

                               gender VARCHAR(30),

                               bio TEXT,

                               profile_photo_url VARCHAR(500),

                               city VARCHAR(100),

                               created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);