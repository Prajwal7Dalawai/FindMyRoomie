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