-- V4: Sales tables: customers, orders, order_items, invoices, payments

CREATE TABLE customers (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT UNIQUE REFERENCES users(id),
    customer_code   VARCHAR(20) NOT NULL UNIQUE,
    full_name       VARCHAR(150) NOT NULL,
    email           VARCHAR(150),
    phone           VARCHAR(20) NOT NULL,
    address         VARCHAR(500),
    city            VARCHAR(100),
    id_number       VARCHAR(20),           -- CCCD/CMND
    tax_code        VARCHAR(20),           -- Mã số thuế (doanh nghiệp)
    company_name    VARCHAR(200),
    customer_type   VARCHAR(20) NOT NULL DEFAULT 'NEW',  -- NEW, REGULAR, VIP
    is_corporate    BOOLEAN NOT NULL DEFAULT FALSE,
    loyalty_points  INTEGER NOT NULL DEFAULT 0,
    notes           TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE orders (
    id              BIGSERIAL PRIMARY KEY,
    order_code      VARCHAR(30) NOT NULL UNIQUE,
    customer_id     BIGINT NOT NULL REFERENCES customers(id),
    employee_id     BIGINT REFERENCES employees(id),
    showroom_id     BIGINT REFERENCES showrooms(id),
    status          VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
                    -- DRAFT, PENDING, CONFIRMED, DELIVERED, COMPLETED, CANCELLED
    order_date      TIMESTAMP NOT NULL DEFAULT NOW(),
    delivery_date   TIMESTAMP,
    subtotal        NUMERIC(15,2) NOT NULL DEFAULT 0,
    discount_amount NUMERIC(15,2) NOT NULL DEFAULT 0,
    discount_pct    NUMERIC(5,2) NOT NULL DEFAULT 0,
    total_amount    NUMERIC(15,2) NOT NULL DEFAULT 0,
    commission_amt  NUMERIC(15,2) NOT NULL DEFAULT 0,
    notes           TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE order_items (
    id          BIGSERIAL PRIMARY KEY,
    order_id    BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    vehicle_id  BIGINT NOT NULL REFERENCES vehicles(id),
    unit_price  NUMERIC(15,2) NOT NULL,
    discount    NUMERIC(15,2) NOT NULL DEFAULT 0,
    line_total  NUMERIC(15,2) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE invoices (
    id              BIGSERIAL PRIMARY KEY,
    invoice_code    VARCHAR(30) NOT NULL UNIQUE,
    order_id        BIGINT NOT NULL UNIQUE REFERENCES orders(id),
    issued_date     TIMESTAMP NOT NULL DEFAULT NOW(),
    due_date        TIMESTAMP,
    total_amount    NUMERIC(15,2) NOT NULL DEFAULT 0,
    paid_amount     NUMERIC(15,2) NOT NULL DEFAULT 0,
    remaining       NUMERIC(15,2) GENERATED ALWAYS AS (total_amount - paid_amount) STORED,
    status          VARCHAR(20) NOT NULL DEFAULT 'UNPAID',  -- UNPAID, PARTIAL, PAID
    pdf_path        VARCHAR(500),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE payments (
    id              BIGSERIAL PRIMARY KEY,
    invoice_id      BIGINT NOT NULL REFERENCES invoices(id),
    payment_date    TIMESTAMP NOT NULL DEFAULT NOW(),
    amount          NUMERIC(15,2) NOT NULL,
    payment_method  VARCHAR(30) NOT NULL,  -- CASH, BANK_TRANSFER, INSTALLMENT, CARD
    reference_no    VARCHAR(100),
    notes           TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_customers_code ON customers(customer_code);
CREATE INDEX idx_customers_phone ON customers(phone);
CREATE INDEX idx_customers_type ON customers(customer_type);
CREATE INDEX idx_orders_code ON orders(order_code);
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_employee_id ON orders(employee_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_order_date ON orders(order_date);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_vehicle_id ON order_items(vehicle_id);
CREATE INDEX idx_invoices_code ON invoices(invoice_code);
CREATE INDEX idx_invoices_order_id ON invoices(order_id);
CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);
