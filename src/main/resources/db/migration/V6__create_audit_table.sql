-- V6: Audit log table

CREATE TABLE audit_logs (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT REFERENCES users(id),
    username    VARCHAR(100),
    action      VARCHAR(50) NOT NULL,      -- CREATE, UPDATE, DELETE, LOGIN, LOGOUT, EXPORT
    entity_type VARCHAR(100),
    entity_id   VARCHAR(50),
    description TEXT,
    ip_address  VARCHAR(45),
    user_agent  VARCHAR(500),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at DESC);
