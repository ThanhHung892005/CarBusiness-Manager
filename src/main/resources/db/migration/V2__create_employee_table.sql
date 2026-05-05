-- V2: Employee table

CREATE TABLE employees (
    id              BIGSERIAL PRIMARY KEY,
    employee_code   VARCHAR(20) NOT NULL UNIQUE,
    user_id         BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    department_id   BIGINT REFERENCES departments(id),
    showroom_id     BIGINT REFERENCES showrooms(id),
    position        VARCHAR(100) NOT NULL,
    hire_date       DATE NOT NULL,
    salary          NUMERIC(15,2) NOT NULL DEFAULT 0,
    commission_rate NUMERIC(5,4) NOT NULL DEFAULT 0.02,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_employees_code ON employees(employee_code);
CREATE INDEX idx_employees_user_id ON employees(user_id);
CREATE INDEX idx_employees_showroom_id ON employees(showroom_id);
CREATE INDEX idx_employees_department_id ON employees(department_id);
