-- V14: Module CRM — Leads và tương tác khách hàng

CREATE TABLE leads (
    id                   BIGSERIAL    PRIMARY KEY,
    full_name            VARCHAR(150) NOT NULL,
    phone                VARCHAR(20)  NOT NULL,
    email                VARCHAR(150),
    source               VARCHAR(20)  NOT NULL DEFAULT 'OTHER',
    status               VARCHAR(30)  NOT NULL DEFAULT 'NEW',
    assigned_employee_id BIGINT       REFERENCES employees(id) ON DELETE SET NULL,
    converted_customer_id BIGINT      REFERENCES customers(id) ON DELETE SET NULL,
    notes                TEXT,
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE customer_interactions (
    id               BIGSERIAL   PRIMARY KEY,
    lead_id          BIGINT      REFERENCES leads(id) ON DELETE CASCADE,
    customer_id      BIGINT      REFERENCES customers(id) ON DELETE CASCADE,
    employee_id      BIGINT      REFERENCES employees(id) ON DELETE SET NULL,
    type             VARCHAR(20) NOT NULL DEFAULT 'CALL',
    content          TEXT,
    interaction_date TIMESTAMP   NOT NULL DEFAULT NOW(),
    created_at       TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_leads_phone             ON leads(phone);
CREATE INDEX idx_leads_status            ON leads(status);
CREATE INDEX idx_leads_assigned_employee ON leads(assigned_employee_id);
CREATE INDEX idx_ci_lead                 ON customer_interactions(lead_id);
CREATE INDEX idx_ci_customer             ON customer_interactions(customer_id);
