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