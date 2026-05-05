-- V5: Service tables: service_appointments, service_records, service_items

CREATE TABLE service_appointments (
    id              BIGSERIAL PRIMARY KEY,
    appointment_code VARCHAR(30) NOT NULL UNIQUE,
    customer_id     BIGINT NOT NULL REFERENCES customers(id),
    vehicle_id      BIGINT NOT NULL REFERENCES vehicles(id),
    employee_id     BIGINT REFERENCES employees(id),
    showroom_id     BIGINT REFERENCES showrooms(id),
    appointment_date TIMESTAMP NOT NULL,
    service_type    VARCHAR(50) NOT NULL,  -- MAINTENANCE, REPAIR, INSPECTION, WARRANTY
    description     TEXT,
    status          VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
                    -- SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
    notes           TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE service_records (
    id              BIGSERIAL PRIMARY KEY,
    appointment_id  BIGINT REFERENCES service_appointments(id),
    vehicle_id      BIGINT NOT NULL REFERENCES vehicles(id),
    employee_id     BIGINT REFERENCES employees(id),
    service_date    DATE NOT NULL,
    mileage_in      INTEGER,
    mileage_out     INTEGER,
    diagnosis       TEXT,
    work_done       TEXT,
    total_cost      NUMERIC(15,2) NOT NULL DEFAULT 0,
    labor_cost      NUMERIC(15,2) NOT NULL DEFAULT 0,
    parts_cost      NUMERIC(15,2) NOT NULL DEFAULT 0,
    next_service_date DATE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE service_items (
    id              BIGSERIAL PRIMARY KEY,
    service_record_id BIGINT NOT NULL REFERENCES service_records(id) ON DELETE CASCADE,
    item_type       VARCHAR(20) NOT NULL,  -- LABOR, PART
    name            VARCHAR(200) NOT NULL,
    quantity        NUMERIC(10,2) NOT NULL DEFAULT 1,
    unit_price      NUMERIC(15,2) NOT NULL DEFAULT 0,
    line_total      NUMERIC(15,2) NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_service_appt_code ON service_appointments(appointment_code);
CREATE INDEX idx_service_appt_customer ON service_appointments(customer_id);
CREATE INDEX idx_service_appt_vehicle ON service_appointments(vehicle_id);
CREATE INDEX idx_service_appt_date ON service_appointments(appointment_date);
CREATE INDEX idx_service_appt_status ON service_appointments(status);
CREATE INDEX idx_service_records_vehicle ON service_records(vehicle_id);
CREATE INDEX idx_service_records_appt ON service_records(appointment_id);
CREATE INDEX idx_service_items_record ON service_items(service_record_id);
