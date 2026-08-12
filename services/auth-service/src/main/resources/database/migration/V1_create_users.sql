CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                       email VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(20),

                       status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

                       email_verified BOOLEAN NOT NULL DEFAULT FALSE,
                       phone_verified BOOLEAN NOT NULL DEFAULT FALSE,

                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT uq_users_email UNIQUE (email),
                       CONSTRAINT uq_users_phone UNIQUE (phone_number),

                       CONSTRAINT chk_users_status
                           CHECK (status IN ('ACTIVE', 'LOCKED', 'SUSPENDED', 'DELETED'))
);