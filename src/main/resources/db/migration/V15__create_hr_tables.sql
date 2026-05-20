-- V15: Module HR — Chấm công và Bảng lương

CREATE TABLE attendances (
    id          BIGSERIAL    PRIMARY KEY,
    employee_id BIGINT       NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    date        DATE         NOT NULL,
    check_in    TIME,
    check_out   TIME,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PRESENT',
    notes       VARCHAR(500),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE(employee_id, date)
);

CREATE TABLE payrolls (
    id             BIGSERIAL       PRIMARY KEY,
    employee_id    BIGINT          NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    month          INT             NOT NULL,
    year           INT             NOT NULL,
    base_salary    DECIMAL(15,2)   NOT NULL DEFAULT 0,
    work_days      INT             NOT NULL DEFAULT 26,
    effective_days DECIMAL(5,1)    NOT NULL DEFAULT 0,
    commission     DECIMAL(15,2)   NOT NULL DEFAULT 0,
    bonuses        DECIMAL(15,2)   NOT NULL DEFAULT 0,
    deductions     DECIMAL(15,2)   NOT NULL DEFAULT 0,
    net_salary     DECIMAL(15,2)   NOT NULL DEFAULT 0,
    status         VARCHAR(20)     NOT NULL DEFAULT 'DRAFT',
    notes          TEXT,
    created_at     TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP       NOT NULL DEFAULT NOW(),
    UNIQUE(employee_id, month, year)
);

CREATE INDEX idx_attendances_employee ON attendances(employee_id);
CREATE INDEX idx_attendances_date     ON attendances(date);
CREATE INDEX idx_payrolls_employee    ON payrolls(employee_id);
CREATE INDEX idx_payrolls_month_year  ON payrolls(month, year);
