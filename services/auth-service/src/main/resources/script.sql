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

CREATE TABLE auth_identities (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                 user_id UUID NOT NULL,

                                 provider VARCHAR(30) NOT NULL,

                                 provider_user_id VARCHAR(255),

                                 password_hash VARCHAR(255),

                                 created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT fk_auth_identity_user
                                     FOREIGN KEY (user_id)
                                         REFERENCES users(id)
                                         ON DELETE CASCADE,

                                 CONSTRAINT chk_auth_identity_provider
                                     CHECK (provider IN ('LOCAL', 'GOOGLE')),

                                 CONSTRAINT uq_provider_identity
                                     UNIQUE (provider, provider_user_id)
);

CREATE TABLE refresh_tokens (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                user_id UUID NOT NULL,

                                token_hash VARCHAR(255) NOT NULL,

                                expires_at TIMESTAMP WITH TIME ZONE NOT NULL,

                                revoked BOOLEAN NOT NULL DEFAULT FALSE,

                                created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_refresh_tokens_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users(id)
                                        ON DELETE CASCADE
);

CREATE TABLE login_attempts (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                user_id UUID,

                                identifier VARCHAR(255) NOT NULL,

                                provider VARCHAR(30) NOT NULL,

                                success BOOLEAN NOT NULL,

                                ip_address INET,

                                user_agent TEXT,

                                attempted_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_login_attempts_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users(id)
                                        ON DELETE SET NULL,

                                CONSTRAINT chk_login_attempts_provider
                                    CHECK (provider IN ('LOCAL', 'GOOGLE'))
);

!-- Indexes
CREATE INDEX idx_refresh_tokens_user_id
    ON refresh_tokens(user_id);

CREATE INDEX idx_refresh_tokens_expires_at
    ON refresh_tokens(expires_at);

CREATE INDEX idx_login_attempts_user_id
    ON login_attempts(user_id);

CREATE INDEX idx_login_attempts_identifier
    ON login_attempts(identifier);

CREATE INDEX idx_login_attempts_attempted_at
    ON login_attempts(attempted_at);