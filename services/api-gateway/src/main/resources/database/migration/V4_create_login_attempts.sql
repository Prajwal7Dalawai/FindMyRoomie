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