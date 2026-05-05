-- V3: Inventory tables: brands, car_models, vehicles, vehicle_images

CREATE TABLE brands (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    country     VARCHAR(100),
    logo        VARCHAR(500),
    description VARCHAR(1000),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE car_models (
    id              BIGSERIAL PRIMARY KEY,
    brand_id        BIGINT NOT NULL REFERENCES brands(id),
    name            VARCHAR(100) NOT NULL,
    year            INTEGER NOT NULL,
    car_type        VARCHAR(50) NOT NULL,  -- Sedan, SUV, Hatchback, Pickup, Van, Coupe, Convertible
    engine          VARCHAR(100),
    transmission    VARCHAR(50),           -- Automatic, Manual, CVT
    fuel_type       VARCHAR(50),           -- Gasoline, Diesel, Electric, Hybrid
    seats           INTEGER,
    description     TEXT,
    base_price      NUMERIC(15,2) NOT NULL DEFAULT 0,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (brand_id, name, year)
);

CREATE TABLE vehicles (
    id              BIGSERIAL PRIMARY KEY,
    vin             VARCHAR(17) NOT NULL UNIQUE,
    car_model_id    BIGINT NOT NULL REFERENCES car_models(id),
    showroom_id     BIGINT REFERENCES showrooms(id),
    color           VARCHAR(50) NOT NULL,
    color_code      VARCHAR(20),
    import_price    NUMERIC(15,2) NOT NULL DEFAULT 0,
    selling_price   NUMERIC(15,2) NOT NULL DEFAULT 0,
    status          VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
                    -- AVAILABLE, RESERVED, SOLD, MAINTENANCE, TRANSFERRED
    import_date     DATE,
    sold_date       DATE,
    mileage         INTEGER DEFAULT 0,
    notes           TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE vehicle_images (
    id          BIGSERIAL PRIMARY KEY,
    vehicle_id  BIGINT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    url         VARCHAR(500) NOT NULL,
    alt_text    VARCHAR(255),
    is_primary  BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order  INTEGER DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_brands_name ON brands(name);
CREATE INDEX idx_car_models_brand_id ON car_models(brand_id);
CREATE INDEX idx_car_models_type ON car_models(car_type);
CREATE INDEX idx_vehicles_vin ON vehicles(vin);
CREATE INDEX idx_vehicles_car_model_id ON vehicles(car_model_id);
CREATE INDEX idx_vehicles_showroom_id ON vehicles(showroom_id);
CREATE INDEX idx_vehicles_status ON vehicles(status);
CREATE INDEX idx_vehicle_images_vehicle_id ON vehicle_images(vehicle_id);
