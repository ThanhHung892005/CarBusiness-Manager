-- V7: Seed data — roles, admin user, sample brands, departments, showroom

-- Roles
INSERT INTO roles (name, description) VALUES
    ('ROLE_ADMIN',    'Quản trị viên hệ thống'),
    ('ROLE_MANAGER',  'Quản lý chi nhánh'),
    ('ROLE_SALES',    'Nhân viên kinh doanh'),
    ('ROLE_CUSTOMER', 'Khách hàng');

-- Admin user (password: Admin@123 — BCrypt $2y$10$ encoded)
INSERT INTO users (username, email, password, full_name, phone, enabled) VALUES
    ('admin', 'admin@carmanagement.com',
     '$2y$10$0kJRUbaYArrOd/GFrY1BPODfkm7GEcjw7eLh3Dctl.beNSQkjKiUK',
     'System Administrator', '0900000001', TRUE);

-- Assign ADMIN role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';

-- Departments
INSERT INTO departments (code, name, description) VALUES
    ('SALES',   'Phòng Kinh Doanh',          'Quản lý bán hàng và khách hàng'),
    ('SERVICE', 'Phòng Dịch Vụ',             'Bảo dưỡng và sửa chữa xe'),
    ('ADMIN',   'Phòng Hành Chính - Nhân Sự', 'Quản lý nhân sự và hành chính'),
    ('FINANCE', 'Phòng Tài Chính - Kế Toán',  'Quản lý tài chính và kế toán');

-- Showroom mẫu
INSERT INTO showrooms (code, name, address, city, phone, email, active) VALUES
    ('HN01', 'Showroom Hà Nội - Cầu Giấy',
     '123 Trần Duy Hưng, Cầu Giấy', 'Hà Nội', '024.3333.1111', 'hanoi@carmanagement.com', TRUE),
    ('HCM01', 'Showroom TP.HCM - Quận 7',
     '456 Nguyễn Hữu Thọ, Quận 7', 'TP. Hồ Chí Minh', '028.3333.2222', 'hcm@carmanagement.com', TRUE),
    ('DN01', 'Showroom Đà Nẵng',
     '789 Nguyễn Văn Linh, Thanh Khê', 'Đà Nẵng', '0236.333.3333', 'danang@carmanagement.com', TRUE);

-- Brands mẫu
INSERT INTO brands (name, country, description, active) VALUES
    ('Toyota',    'Nhật Bản',   'Thương hiệu xe hơi hàng đầu Nhật Bản', TRUE),
    ('Honda',     'Nhật Bản',   'Thương hiệu xe hơi và xe máy Nhật Bản', TRUE),
    ('Mazda',     'Nhật Bản',   'Thương hiệu xe hơi cao cấp Nhật Bản', TRUE),
    ('BMW',       'Đức',        'Thương hiệu xe sang của Đức', TRUE),
    ('Mercedes',  'Đức',        'Thương hiệu xe luxury hàng đầu thế giới', TRUE),
    ('Hyundai',   'Hàn Quốc',  'Thương hiệu xe hơi Hàn Quốc', TRUE),
    ('Kia',       'Hàn Quốc',  'Thương hiệu xe hơi Hàn Quốc', TRUE),
    ('VinFast',   'Việt Nam',   'Thương hiệu xe hơi Việt Nam', TRUE),
    ('Ford',      'Mỹ',         'Thương hiệu xe hơi Mỹ', TRUE),
    ('Chevrolet', 'Mỹ',         'Thương hiệu xe hơi General Motors', TRUE);

-- Car models mẫu
INSERT INTO car_models (brand_id, name, year, car_type, engine, transmission, fuel_type, seats, base_price) VALUES
    ((SELECT id FROM brands WHERE name='Toyota'), 'Camry',    2024, 'Sedan', '2.5L 4-cyl', 'Automatic', 'Gasoline', 5, 1190000000),
    ((SELECT id FROM brands WHERE name='Toyota'), 'Fortuner', 2024, 'SUV',   '2.7L 4-cyl', 'Automatic', 'Gasoline', 7, 1265000000),
    ((SELECT id FROM brands WHERE name='Toyota'), 'Vios',     2024, 'Sedan', '1.5L 4-cyl', 'CVT',       'Gasoline', 5,  529000000),
    ((SELECT id FROM brands WHERE name='Honda'),  'CR-V',     2024, 'SUV',   '1.5L Turbo', 'CVT',       'Gasoline', 5,  998000000),
    ((SELECT id FROM brands WHERE name='Honda'),  'City',     2024, 'Sedan', '1.5L 4-cyl', 'CVT',       'Gasoline', 5,  529000000),
    ((SELECT id FROM brands WHERE name='Mazda'),  'CX-5',     2024, 'SUV',   '2.0L 4-cyl', 'Automatic', 'Gasoline', 5,  849000000),
    ((SELECT id FROM brands WHERE name='Mazda'),  'Mazda3',   2024, 'Sedan', '2.0L 4-cyl', 'Automatic', 'Gasoline', 5,  719000000),
    ((SELECT id FROM brands WHERE name='VinFast'),'VF8',      2024, 'SUV',   'Electric',   'Automatic', 'Electric',  7,  900000000),
    ((SELECT id FROM brands WHERE name='VinFast'),'VF9',      2024, 'SUV',   'Electric',   'Automatic', 'Electric',  7, 1400000000),
    ((SELECT id FROM brands WHERE name='BMW'),    '3 Series', 2024, 'Sedan', '2.0L Turbo', 'Automatic', 'Gasoline', 5, 2399000000);
