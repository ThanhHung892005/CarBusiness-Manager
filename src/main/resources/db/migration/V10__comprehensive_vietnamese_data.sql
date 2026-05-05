-- ============================================================
-- V10: DỮ LIỆU MẪU TOÀN DIỆN — PHÙ HỢP THỰC TẾ VIỆT NAM
-- Bao gồm: Showrooms bổ sung, Users (Manager/Sales/Kỹ thuật/Khách hàng),
--          Employees, Customers (cá nhân + doanh nghiệp, NEW/REGULAR/VIP),
--          Car Models (tất cả loại xe), Vehicles (tất cả trạng thái),
--          Orders (tất cả trạng thái), Invoices, Payments (tất cả phương thức),
--          Service Appointments (tất cả trạng thái), Service Records & Items
-- ============================================================

-- ============================================================
-- SECTION 1: SHOWROOM BỔ SUNG
-- ============================================================
INSERT INTO showrooms (code, name, address, city, phone, email, active) VALUES
    ('HN02',  'Showroom Hà Nội - Long Biên',
     '45 Nguyễn Văn Cừ, Phường Ngọc Lâm, Long Biên', 'Hà Nội',
     '024.6666.1122', 'hanoi2@carmanagement.com', TRUE),
    ('HCM02', 'Showroom Bình Dương',
     '88 Đại Lộ Bình Dương, Phường Thuận Giao, Thuận An', 'Bình Dương',
     '0274.6666.2233', 'binhduong@carmanagement.com', TRUE),
    ('CT01',  'Showroom Cần Thơ',
     '234 Trần Phú, Phường Cái Khế, Ninh Kiều', 'Cần Thơ',
     '0292.666.3344', 'cantho@carmanagement.com', TRUE);

-- ============================================================
-- SECTION 2: USERS BỔ SUNG
-- Mật khẩu: Manager@123, Sales@123, Customer@123
-- ============================================================

-- Quản lý chi nhánh (ROLE_MANAGER) — password: Manager@123
INSERT INTO users (username, email, password, full_name, phone, enabled) VALUES
    ('manager_hn',  'manager.hn@carmanagement.com',
     '$2b$10$vbLZ6o9TufrvgYOiRit8UOidfvY2FOhmMP7foq.pNpYBgnuK0gIqO',
     'Trần Văn Minh', '0912345601', TRUE),
    ('manager_hcm', 'manager.hcm@carmanagement.com',
     '$2b$10$vbLZ6o9TufrvgYOiRit8UOidfvY2FOhmMP7foq.pNpYBgnuK0gIqO',
     'Lê Thị Lan', '0912345602', TRUE),
    ('manager_dn',  'manager.dn@carmanagement.com',
     '$2b$10$vbLZ6o9TufrvgYOiRit8UOidfvY2FOhmMP7foq.pNpYBgnuK0gIqO',
     'Phạm Văn Đức', '0912345603', TRUE);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username IN ('manager_hn','manager_hcm','manager_dn')
  AND r.name = 'ROLE_MANAGER';

-- Nhân viên kinh doanh (ROLE_SALES) — password: Sales@123
INSERT INTO users (username, email, password, full_name, phone, enabled) VALUES
    ('sales_nguyen', 'sales.nguyen@carmanagement.com',
     '$2b$10$aKWQTJtbtsi3N3i7/QL8JO77/bAKrUnroTQ8xPUWnyAMPA9VMLHT2',
     'Nguyễn Văn An', '0912345604', TRUE),
    ('sales_tran',   'sales.tran@carmanagement.com',
     '$2b$10$aKWQTJtbtsi3N3i7/QL8JO77/bAKrUnroTQ8xPUWnyAMPA9VMLHT2',
     'Trần Thị Bích', '0912345605', TRUE),
    ('sales_le',     'sales.le@carmanagement.com',
     '$2b$10$aKWQTJtbtsi3N3i7/QL8JO77/bAKrUnroTQ8xPUWnyAMPA9VMLHT2',
     'Lê Văn Cường', '0912345606', TRUE),
    ('sales_pham',   'sales.pham@carmanagement.com',
     '$2b$10$aKWQTJtbtsi3N3i7/QL8JO77/bAKrUnroTQ8xPUWnyAMPA9VMLHT2',
     'Phạm Thị Dung', '0912345607', TRUE),
    ('sales_hoang',  'sales.hoang@carmanagement.com',
     '$2b$10$aKWQTJtbtsi3N3i7/QL8JO77/bAKrUnroTQ8xPUWnyAMPA9VMLHT2',
     'Hoàng Văn Em', '0912345608', TRUE);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username IN ('sales_nguyen','sales_tran','sales_le','sales_pham','sales_hoang')
  AND r.name = 'ROLE_SALES';

-- Kỹ thuật viên (dùng nội bộ, không cần đăng nhập UI) — password: Sales@123
INSERT INTO users (username, email, password, full_name, phone, enabled) VALUES
    ('tech_hung', 'tech.hung@carmanagement.com',
     '$2b$10$aKWQTJtbtsi3N3i7/QL8JO77/bAKrUnroTQ8xPUWnyAMPA9VMLHT2',
     'Nguyễn Văn Hùng', '0912345609', TRUE),
    ('tech_thu',  'tech.thu@carmanagement.com',
     '$2b$10$aKWQTJtbtsi3N3i7/QL8JO77/bAKrUnroTQ8xPUWnyAMPA9VMLHT2',
     'Trần Thị Thu', '0912345610', TRUE),
    ('tech_son',  'tech.son@carmanagement.com',
     '$2b$10$aKWQTJtbtsi3N3i7/QL8JO77/bAKrUnroTQ8xPUWnyAMPA9VMLHT2',
     'Lê Văn Sơn', '0912345611', TRUE);

-- Khách hàng cá nhân (ROLE_CUSTOMER) — password: Customer@123
INSERT INTO users (username, email, password, full_name, phone, enabled) VALUES
    ('kh_minh',  'nguyen.minh@gmail.com',   '$2b$10$nTFjep.STH3O3nbEt6F1QuD6pGi42lHRmR3CTbp1vbHEqQE5hnrSu', 'Nguyễn Văn Minh',   '0901234501', TRUE),
    ('kh_thu',   'le.thu@gmail.com',         '$2b$10$nTFjep.STH3O3nbEt6F1QuD6pGi42lHRmR3CTbp1vbHEqQE5hnrSu', 'Lê Thị Thu',        '0901234502', TRUE),
    ('kh_duc',   'pham.duc@gmail.com',       '$2b$10$nTFjep.STH3O3nbEt6F1QuD6pGi42lHRmR3CTbp1vbHEqQE5hnrSu', 'Phạm Văn Đức',      '0901234503', TRUE),
    ('kh_lan',   'tran.lan@gmail.com',       '$2b$10$nTFjep.STH3O3nbEt6F1QuD6pGi42lHRmR3CTbp1vbHEqQE5hnrSu', 'Trần Thị Lan',      '0901234504', TRUE),
    ('kh_hung',  'hoang.hung@gmail.com',     '$2b$10$nTFjep.STH3O3nbEt6F1QuD6pGi42lHRmR3CTbp1vbHEqQE5hnrSu', 'Hoàng Văn Hùng',    '0901234505', TRUE),
    ('kh_hoa',   'vu.hoa@gmail.com',         '$2b$10$nTFjep.STH3O3nbEt6F1QuD6pGi42lHRmR3CTbp1vbHEqQE5hnrSu', 'Vũ Thị Hoa',        '0901234506', TRUE),
    ('kh_tuan',  'do.tuan@gmail.com',        '$2b$10$nTFjep.STH3O3nbEt6F1QuD6pGi42lHRmR3CTbp1vbHEqQE5hnrSu', 'Đỗ Văn Tuấn',       '0901234507', TRUE),
    ('kh_mai',   'nguyen.mai@gmail.com',     '$2b$10$nTFjep.STH3O3nbEt6F1QuD6pGi42lHRmR3CTbp1vbHEqQE5hnrSu', 'Nguyễn Thị Mai',    '0901234508', TRUE),
    ('kh_long',  'vu.long@gmail.com',        '$2b$10$nTFjep.STH3O3nbEt6F1QuD6pGi42lHRmR3CTbp1vbHEqQE5hnrSu', 'Vũ Hoàng Long',     '0901234509', TRUE),
    ('kh_linh',  'tran.linh@gmail.com',      '$2b$10$nTFjep.STH3O3nbEt6F1QuD6pGi42lHRmR3CTbp1vbHEqQE5hnrSu', 'Trần Thị Linh',     '0901234510', TRUE),
    ('kh_khoa',  'nguyen.khoa@gmail.com',    '$2b$10$nTFjep.STH3O3nbEt6F1QuD6pGi42lHRmR3CTbp1vbHEqQE5hnrSu', 'Nguyễn Thanh Khoa', '0901234511', TRUE),
    ('kh_phuong','le.phuong@gmail.com',      '$2b$10$nTFjep.STH3O3nbEt6F1QuD6pGi42lHRmR3CTbp1vbHEqQE5hnrSu', 'Lê Thị Phương',     '0901234512', TRUE);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username IN ('kh_minh','kh_thu','kh_duc','kh_lan','kh_hung',
                     'kh_hoa','kh_tuan','kh_mai','kh_long','kh_linh','kh_khoa','kh_phuong')
  AND r.name = 'ROLE_CUSTOMER';

-- ============================================================
-- SECTION 3: EMPLOYEES
-- ============================================================

-- Nhân viên cho user 'sales' hiện có (V9)
INSERT INTO employees (employee_code, user_id, department_id, showroom_id, position, hire_date, salary, commission_rate, active) VALUES
    ('EMP000001',
     (SELECT id FROM users WHERE username='sales'),
     (SELECT id FROM departments WHERE code='SALES'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Nhân Viên Kinh Doanh', '2023-01-15', 12000000, 0.02, TRUE);

-- Quản lý chi nhánh
INSERT INTO employees (employee_code, user_id, department_id, showroom_id, position, hire_date, salary, commission_rate, active) VALUES
    ('EMP000002',
     (SELECT id FROM users WHERE username='manager_hn'),
     (SELECT id FROM departments WHERE code='ADMIN'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Giám Đốc Chi Nhánh', '2022-03-01', 35000000, 0.005, TRUE),
    ('EMP000003',
     (SELECT id FROM users WHERE username='manager_hcm'),
     (SELECT id FROM departments WHERE code='ADMIN'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Giám Đốc Chi Nhánh', '2022-06-01', 35000000, 0.005, TRUE),
    ('EMP000004',
     (SELECT id FROM users WHERE username='manager_dn'),
     (SELECT id FROM departments WHERE code='ADMIN'),
     (SELECT id FROM showrooms WHERE code='DN01'),
     'Giám Đốc Chi Nhánh', '2023-01-10', 32000000, 0.005, TRUE);

-- Nhân viên kinh doanh
INSERT INTO employees (employee_code, user_id, department_id, showroom_id, position, hire_date, salary, commission_rate, active) VALUES
    ('EMP000005',
     (SELECT id FROM users WHERE username='sales_nguyen'),
     (SELECT id FROM departments WHERE code='SALES'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Nhân Viên Kinh Doanh', '2023-03-01', 12000000, 0.02, TRUE),
    ('EMP000006',
     (SELECT id FROM users WHERE username='sales_tran'),
     (SELECT id FROM departments WHERE code='SALES'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Chuyên Viên Kinh Doanh', '2022-09-15', 15000000, 0.025, TRUE),
    ('EMP000007',
     (SELECT id FROM users WHERE username='sales_le'),
     (SELECT id FROM departments WHERE code='SALES'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Nhân Viên Kinh Doanh', '2023-04-01', 13000000, 0.02, TRUE),
    ('EMP000008',
     (SELECT id FROM users WHERE username='sales_pham'),
     (SELECT id FROM departments WHERE code='SALES'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Chuyên Viên Kinh Doanh Cao Cấp', '2021-08-01', 18000000, 0.03, TRUE),
    ('EMP000009',
     (SELECT id FROM users WHERE username='sales_hoang'),
     (SELECT id FROM departments WHERE code='SALES'),
     (SELECT id FROM showrooms WHERE code='DN01'),
     'Nhân Viên Kinh Doanh', '2023-08-01', 11000000, 0.02, TRUE);

-- Kỹ thuật viên
INSERT INTO employees (employee_code, user_id, department_id, showroom_id, position, hire_date, salary, commission_rate, active) VALUES
    ('EMP000010',
     (SELECT id FROM users WHERE username='tech_hung'),
     (SELECT id FROM departments WHERE code='SERVICE'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Kỹ Thuật Viên Chính', '2022-05-01', 16000000, 0.0, TRUE),
    ('EMP000011',
     (SELECT id FROM users WHERE username='tech_thu'),
     (SELECT id FROM departments WHERE code='SERVICE'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Kỹ Thuật Viên', '2022-08-15', 14000000, 0.0, TRUE),
    ('EMP000012',
     (SELECT id FROM users WHERE username='tech_son'),
     (SELECT id FROM departments WHERE code='SERVICE'),
     (SELECT id FROM showrooms WHERE code='DN01'),
     'Kỹ Thuật Viên Cấp Cao', '2021-11-01', 18000000, 0.0, TRUE);

-- ============================================================
-- SECTION 4: CUSTOMERS
-- ============================================================

-- Link user 'customer' (V9) vào bảng customers
INSERT INTO customers (user_id, customer_code, full_name, email, phone, address, city, id_number, customer_type, loyalty_points)
VALUES (
    (SELECT id FROM users WHERE username='customer'),
    'KH000001', 'Nguyễn Văn Khách', 'customer@carmanagement.com', '0900000003',
    '12 Nguyễn Huệ, Phường Bến Nghé, Quận 1', 'TP. Hồ Chí Minh',
    '087654321001', 'NEW', 0
);

-- Khách hàng cá nhân — liên kết user account
INSERT INTO customers (user_id, customer_code, full_name, email, phone, address, city, id_number, customer_type, loyalty_points) VALUES
    ((SELECT id FROM users WHERE username='kh_minh'),
     'KH000002', 'Nguyễn Văn Minh', 'nguyen.minh@gmail.com', '0901234501',
     '45 Lê Đại Hành, Phường Bùi Thị Xuân, Hai Bà Trưng', 'Hà Nội',
     '001234567890', 'VIP', 5200),
    ((SELECT id FROM users WHERE username='kh_thu'),
     'KH000003', 'Lê Thị Thu', 'le.thu@gmail.com', '0901234502',
     '78 Đinh Tiên Hoàng, Phường 3, Bình Thạnh', 'TP. Hồ Chí Minh',
     '079234567891', 'REGULAR', 1265),
    ((SELECT id FROM users WHERE username='kh_duc'),
     'KH000004', 'Phạm Văn Đức', 'pham.duc@gmail.com', '0901234503',
     '23 Phan Đình Phùng, Phường Hải Châu 1, Hải Châu', 'Đà Nẵng',
     '048234567892', 'REGULAR', 529),
    ((SELECT id FROM users WHERE username='kh_lan'),
     'KH000005', 'Trần Thị Lan', 'tran.lan@gmail.com', '0901234504',
     '90 Nguyễn Lương Bằng, Phường Hoà Khánh Nam, Liên Chiểu', 'Đà Nẵng',
     '048334567893', 'REGULAR', 998),
    ((SELECT id FROM users WHERE username='kh_hung'),
     'KH000006', 'Hoàng Văn Hùng', 'hoang.hung@gmail.com', '0901234505',
     '56 Lý Thường Kiệt, Phường Phan Chu Trinh, Hoàn Kiếm', 'Hà Nội',
     '001534567894', 'REGULAR', 1265),
    ((SELECT id FROM users WHERE username='kh_hoa'),
     'KH000007', 'Vũ Thị Hoa', 'vu.hoa@gmail.com', '0901234506',
     '134 Nguyễn Thị Minh Khai, Phường 6, Quận 3', 'TP. Hồ Chí Minh',
     '079634567895', 'NEW', 0),
    ((SELECT id FROM users WHERE username='kh_tuan'),
     'KH000008', 'Đỗ Văn Tuấn', 'do.tuan@gmail.com', '0901234507',
     '200 Trần Phú, Phường Hải Châu 2, Hải Châu', 'Đà Nẵng',
     '048734567896', 'REGULAR', 719),
    ((SELECT id FROM users WHERE username='kh_mai'),
     'KH000009', 'Nguyễn Thị Mai', 'nguyen.mai@gmail.com', '0901234508',
     '67 Điện Biên Phủ, Phường 15, Bình Thạnh', 'TP. Hồ Chí Minh',
     '079834567897', 'REGULAR', 900),
    ((SELECT id FROM users WHERE username='kh_long'),
     'KH000010', 'Vũ Hoàng Long', 'vu.long@gmail.com', '0901234509',
     '88 Tôn Đức Thắng, Phường Bến Nghé, Quận 1', 'TP. Hồ Chí Minh',
     '079934567898', 'VIP', 7500),
    ((SELECT id FROM users WHERE username='kh_linh'),
     'KH000011', 'Trần Thị Linh', 'tran.linh@gmail.com', '0901234510',
     '145 Nguyễn Văn Trỗi, Phường 11, Phú Nhuận', 'TP. Hồ Chí Minh',
     '079034567899', 'NEW', 0),
    ((SELECT id FROM users WHERE username='kh_khoa'),
     'KH000012', 'Nguyễn Thanh Khoa', 'nguyen.khoa@gmail.com', '0901234511',
     '12 Trần Hưng Đạo, Phường Hưng Thành, Ninh Kiều', 'Cần Thơ',
     '092234567800', 'NEW', 0),
    ((SELECT id FROM users WHERE username='kh_phuong'),
     'KH000013', 'Lê Thị Phương', 'le.phuong@gmail.com', '0901234512',
     '34 Lê Thánh Tôn, Phường Bến Nghé, Quận 1', 'TP. Hồ Chí Minh',
     '079134567801', 'REGULAR', 450);

-- Khách hàng doanh nghiệp (không có user account)
INSERT INTO customers (customer_code, full_name, email, phone, address, city, tax_code, company_name, customer_type, is_corporate, loyalty_points) VALUES
    ('KH000014', 'Công Ty TNHH ABC Logistics',
     'purchasing@abclogistics.vn', '024.6789.0123',
     '45 Trường Chinh, Phường Phương Mai, Đống Đa', 'Hà Nội',
     '0109876543', 'Công Ty TNHH ABC Logistics',
     'VIP', TRUE, 12500),
    ('KH000015', 'Tập Đoàn XYZ Petroleum',
     'admin@xyzpetro.vn', '028.6789.0456',
     '12 Nguyễn Đình Chiểu, Phường Đa Kao, Quận 1', 'TP. Hồ Chí Minh',
     '0312345678', 'Tập Đoàn XYZ Petroleum',
     'VIP', TRUE, 28900),
    ('KH000016', 'Công Ty CP Xây Dựng Nam Phát',
     'info@namphat.vn', '0236.999.1234',
     '56 Hùng Vương, Phường Hải Châu 1, Hải Châu', 'Đà Nẵng',
     '0401234567', 'Công Ty CP Xây Dựng Nam Phát',
     'REGULAR', TRUE, 1200),
    ('KH000017', 'Công Ty TNHH Vận Tải Phương Nam',
     'cskh@phuongnam.vn', '0292.456.7890',
     '78 Nguyễn Thị Thập, Phường Cái Khế, Ninh Kiều', 'Cần Thơ',
     '1801234567', 'Công Ty TNHH Vận Tải Phương Nam',
     'REGULAR', TRUE, 3600);

-- ============================================================
-- SECTION 5: CAR MODELS BỔ SUNG (tất cả loại xe)
-- ============================================================

-- Toyota bổ sung: Hatchback, MPV, Pickup, SUV luxury
INSERT INTO car_models (brand_id, name, year, car_type, engine, transmission, fuel_type, seats, base_price, active) VALUES
    ((SELECT id FROM brands WHERE name='Toyota'), 'Yaris Cross', 2024, 'SUV',     '1.5L Hybrid',            'CVT',       'Hybrid',   5,  838000000, TRUE),
    ((SELECT id FROM brands WHERE name='Toyota'), 'Innova Cross', 2024, 'MPV',    '2.0L Hybrid',            'CVT',       'Hybrid',   7,  990000000, TRUE),
    ((SELECT id FROM brands WHERE name='Toyota'), 'Hilux',       2024, 'PICKUP',  '2.8L 4-cyl Diesel',      'Automatic', 'Diesel',   5,  918000000, TRUE),
    ((SELECT id FROM brands WHERE name='Toyota'), 'Land Cruiser', 2024, 'SUV',    '3.5L V6 Twin Turbo',     'Automatic', 'Gasoline', 7, 5500000000, TRUE),
    ((SELECT id FROM brands WHERE name='Toyota'), 'GR Supra',    2024, 'COUPE',   '3.0L Inline-6 Turbo',    'Automatic', 'Gasoline', 2, 4999000000, TRUE);

-- Honda bổ sung
INSERT INTO car_models (brand_id, name, year, car_type, engine, transmission, fuel_type, seats, base_price, active) VALUES
    ((SELECT id FROM brands WHERE name='Honda'), 'Jazz',    2024, 'HATCHBACK', '1.5L i-MMD Hybrid',  'CVT',       'Hybrid',   5,  544000000, TRUE),
    ((SELECT id FROM brands WHERE name='Honda'), 'Accord',  2024, 'SEDAN',     '1.5L Turbo',          'CVT',       'Gasoline', 5, 1319000000, TRUE),
    ((SELECT id FROM brands WHERE name='Honda'), 'HR-V',    2024, 'SUV',       '1.5L 4-cyl',          'CVT',       'Gasoline', 5,  699000000, TRUE),
    ((SELECT id FROM brands WHERE name='Honda'), 'Odyssey', 2024, 'MPV',       '2.4L i-VTEC',         'Automatic', 'Gasoline', 8, 1650000000, TRUE);

-- Mazda bổ sung
INSERT INTO car_models (brand_id, name, year, car_type, engine, transmission, fuel_type, seats, base_price, active) VALUES
    ((SELECT id FROM brands WHERE name='Mazda'), 'Mazda2',  2024, 'HATCHBACK', '1.5L SkyActiv-G', 'Automatic', 'Gasoline', 5,  479000000, TRUE),
    ((SELECT id FROM brands WHERE name='Mazda'), 'CX-3',    2024, 'SUV',       '2.0L SkyActiv-G', 'Automatic', 'Gasoline', 5,  619000000, TRUE),
    ((SELECT id FROM brands WHERE name='Mazda'), 'CX-8',    2024, 'SUV',       '2.5L SkyActiv-G', 'Automatic', 'Gasoline', 7, 1199000000, TRUE),
    ((SELECT id FROM brands WHERE name='Mazda'), 'CX-60',   2024, 'SUV',       '3.3L Diesel',     'Automatic', 'Diesel',   5, 1549000000, TRUE);

-- BMW bổ sung
INSERT INTO car_models (brand_id, name, year, car_type, engine, transmission, fuel_type, seats, base_price, active) VALUES
    ((SELECT id FROM brands WHERE name='BMW'), '5 Series',    2024, 'SEDAN',   '2.0L TwinPower Turbo', 'Automatic', 'Gasoline', 5, 3499000000, TRUE),
    ((SELECT id FROM brands WHERE name='BMW'), 'X3',          2024, 'SUV',     '2.0L TwinPower Turbo', 'Automatic', 'Gasoline', 5, 2999000000, TRUE),
    ((SELECT id FROM brands WHERE name='BMW'), 'X5',          2024, 'SUV',     '3.0L TwinPower Turbo', 'Automatic', 'Gasoline', 5, 5219000000, TRUE),
    ((SELECT id FROM brands WHERE name='BMW'), 'M4 Coupe',    2024, 'COUPE',   '3.0L TwinPower Turbo', 'Automatic', 'Gasoline', 4, 6999000000, TRUE);

-- Mercedes-Benz bổ sung
INSERT INTO car_models (brand_id, name, year, car_type, engine, transmission, fuel_type, seats, base_price, active) VALUES
    ((SELECT id FROM brands WHERE name='Mercedes'), 'C 200',     2024, 'SEDAN', '1.5L Turbo EQ Boost',   'Automatic', 'Gasoline', 5, 1889000000, TRUE),
    ((SELECT id FROM brands WHERE name='Mercedes'), 'E 300',     2024, 'SEDAN', '2.0L Turbo',             'Automatic', 'Gasoline', 5, 3069000000, TRUE),
    ((SELECT id FROM brands WHERE name='Mercedes'), 'GLC 200',   2024, 'SUV',   '1.5L Turbo EQ Boost',   'Automatic', 'Gasoline', 5, 2549000000, TRUE),
    ((SELECT id FROM brands WHERE name='Mercedes'), 'GLE 450',   2024, 'SUV',   '3.0L Turbo EQ Boost',   'Automatic', 'Gasoline', 5, 5349000000, TRUE),
    ((SELECT id FROM brands WHERE name='Mercedes'), 'S 450',     2024, 'SEDAN', '3.0L Turbo',             'Automatic', 'Gasoline', 5, 8139000000, TRUE);

-- Hyundai bổ sung
INSERT INTO car_models (brand_id, name, year, car_type, engine, transmission, fuel_type, seats, base_price, active) VALUES
    ((SELECT id FROM brands WHERE name='Hyundai'), 'Tucson',     2024, 'SUV',      '2.0L 4-cyl',          'Automatic', 'Gasoline', 5,  825000000, TRUE),
    ((SELECT id FROM brands WHERE name='Hyundai'), 'Elantra',    2024, 'SEDAN',    '2.0L 4-cyl',          'Automatic', 'Gasoline', 5,  699000000, TRUE),
    ((SELECT id FROM brands WHERE name='Hyundai'), 'Santa Fe',   2024, 'SUV',      '2.5L Turbo',          'Automatic', 'Gasoline', 7, 1445000000, TRUE),
    ((SELECT id FROM brands WHERE name='Hyundai'), 'Stargazer',  2024, 'MPV',      '1.5L 4-cyl',          'Automatic', 'Gasoline', 7,  625000000, TRUE),
    ((SELECT id FROM brands WHERE name='Hyundai'), 'IONIQ 6',    2024, 'SEDAN',    'Electric 239kW (AWD)','Automatic', 'Electric', 5, 1599000000, TRUE);

-- Kia bổ sung
INSERT INTO car_models (brand_id, name, year, car_type, engine, transmission, fuel_type, seats, base_price, active) VALUES
    ((SELECT id FROM brands WHERE name='Kia'), 'Sportage', 2024, 'SUV',    '1.6L Turbo',           'Automatic', 'Gasoline', 5,  869000000, TRUE),
    ((SELECT id FROM brands WHERE name='Kia'), 'Carnival', 2024, 'VAN',    '2.2L CRDi Diesel',     'Automatic', 'Diesel',   8, 1619000000, TRUE),
    ((SELECT id FROM brands WHERE name='Kia'), 'K5',       2024, 'SEDAN',  '2.0L 4-cyl',           'Automatic', 'Gasoline', 5,  869000000, TRUE),
    ((SELECT id FROM brands WHERE name='Kia'), 'Seltos',   2024, 'SUV',    '1.4L Turbo',           'Automatic', 'Gasoline', 5,  699000000, TRUE),
    ((SELECT id FROM brands WHERE name='Kia'), 'EV6',      2024, 'SUV',    'Electric 320kW (AWD)', 'Automatic', 'Electric', 5, 1699000000, TRUE);

-- VinFast bổ sung
INSERT INTO car_models (brand_id, name, year, car_type, engine, transmission, fuel_type, seats, base_price, active) VALUES
    ((SELECT id FROM brands WHERE name='VinFast'), 'VF3',      2024, 'HATCHBACK', 'Electric 42kW',        'Automatic', 'Electric', 5,  235000000, TRUE),
    ((SELECT id FROM brands WHERE name='VinFast'), 'VF5 Plus', 2024, 'HATCHBACK', 'Electric 130kW',       'Automatic', 'Electric', 5,  458000000, TRUE),
    ((SELECT id FROM brands WHERE name='VinFast'), 'VF6',      2024, 'SUV',       'Electric 150kW',       'Automatic', 'Electric', 5,  675000000, TRUE),
    ((SELECT id FROM brands WHERE name='VinFast'), 'VF7',      2024, 'SUV',       'Electric 260kW (AWD)', 'Automatic', 'Electric', 5,  850000000, TRUE);

-- Ford bổ sung
INSERT INTO car_models (brand_id, name, year, car_type, engine, transmission, fuel_type, seats, base_price, active) VALUES
    ((SELECT id FROM brands WHERE name='Ford'), 'Ranger',    2024, 'PICKUP', '2.0L Bi-Turbo Diesel', 'Automatic', 'Diesel',   5,  909000000, TRUE),
    ((SELECT id FROM brands WHERE name='Ford'), 'Everest',   2024, 'SUV',    '2.0L Turbo Diesel',    'Automatic', 'Diesel',   7, 1349000000, TRUE),
    ((SELECT id FROM brands WHERE name='Ford'), 'Territory', 2024, 'SUV',    '1.5L EcoBoost',        'Automatic', 'Gasoline', 5,  822000000, TRUE),
    ((SELECT id FROM brands WHERE name='Ford'), 'Transit',   2024, 'VAN',    '2.0L EcoBlue Diesel',  'Manual',    'Diesel',   16,1050000000, TRUE),
    ((SELECT id FROM brands WHERE name='Ford'), 'Mustang',   2024, 'COUPE',  '5.0L V8',              'Automatic', 'Gasoline', 4, 4400000000, TRUE);

-- Chevrolet bổ sung
INSERT INTO car_models (brand_id, name, year, car_type, engine, transmission, fuel_type, seats, base_price, active) VALUES
    ((SELECT id FROM brands WHERE name='Chevrolet'), 'Colorado',    2024, 'PICKUP', '2.8L Duramax Diesel', 'Automatic', 'Diesel',   5,  778000000, TRUE),
    ((SELECT id FROM brands WHERE name='Chevrolet'), 'Trailblazer', 2024, 'SUV',    '1.35L Turbo',         'Automatic', 'Gasoline', 7,  939000000, TRUE),
    ((SELECT id FROM brands WHERE name='Chevrolet'), 'Camaro',      2024, 'COUPE',  '6.2L V8',             'Automatic', 'Gasoline', 4, 4500000000, TRUE);

-- ============================================================
-- SECTION 6: VEHICLES (42 xe — đủ tất cả trạng thái)
-- VIN format: XX_BRAND(2)_YEAR(4)_SEQ(9) = 17 ký tự
-- ============================================================

-- ---- SOLD vehicles: cho COMPLETED orders ----
INSERT INTO vehicles (vin, car_model_id, showroom_id, color, color_code, import_price, selling_price, status, import_date, sold_date) VALUES
    ('VNTY202400000001',
     (SELECT id FROM car_models WHERE name='Camry'    AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Đen ánh kim', '#1C1C1C', 1060000000, 1190000000, 'SOLD', '2023-11-15', '2024-01-25'),
    ('VNTY202400000002',
     (SELECT id FROM car_models WHERE name='Fortuner' AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Xám titan', '#808080', 1120000000, 1265000000, 'SOLD', '2023-12-01', '2024-02-28'),
    ('VNTY202400000003',
     (SELECT id FROM car_models WHERE name='Vios'     AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Đỏ đô', '#8B0000', 460000000, 529000000, 'SOLD', '2024-01-10', '2024-03-15'),
    ('VNHO202400000001',
     (SELECT id FROM car_models WHERE name='City'     AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Trắng ngọc trai', '#F8F8FF', 460000000, 529000000, 'SOLD', '2024-01-20', '2024-03-28'),
    ('VNHO202400000002',
     (SELECT id FROM car_models WHERE name='CR-V'     AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Xanh ngọc', '#006994', 880000000, 998000000, 'SOLD', '2024-02-05', '2024-04-20'),
    ('VNME202400000001',
     (SELECT id FROM car_models WHERE name='C 200'    AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Trắng ngọc trai', '#F5F5F5', 1680000000, 1889000000, 'SOLD', '2024-03-01', '2024-05-30'),
    ('VNMZ202400000001',
     (SELECT id FROM car_models WHERE name='Mazda3'   AND year=2024),
     (SELECT id FROM showrooms WHERE code='DN01'),
     'Bạc', '#C0C0C0', 640000000, 719000000, 'SOLD', '2024-02-20', '2024-06-15'),
    ('VNVF202400000001',
     (SELECT id FROM car_models WHERE name='VF8'      AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Xanh dương đậm', '#003399', 800000000, 900000000, 'SOLD', '2024-03-15', '2024-07-20'),
    ('VNHY202400000001',
     (SELECT id FROM car_models WHERE name='Elantra'  AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Trắng', '#FFFFFF', 620000000, 699000000, 'SOLD', '2024-04-01', '2024-08-15'),
    ('VNHO202400000003',
     (SELECT id FROM car_models WHERE name='HR-V'     AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Đỏ', '#CC0000', 620000000, 699000000, 'SOLD', '2024-04-10', '2024-09-10'),
    ('VNTY202400000004',
     (SELECT id FROM car_models WHERE name='Yaris Cross' AND year=2024),
     (SELECT id FROM showrooms WHERE code='DN01'),
     'Vàng ánh kim', '#FFD700', 740000000, 838000000, 'SOLD', '2024-05-01', '2024-10-22');

-- ---- SOLD vehicles: cho DELIVERED orders ----
INSERT INTO vehicles (vin, car_model_id, showroom_id, color, color_code, import_price, selling_price, status, import_date, sold_date) VALUES
    ('VNMZ202400000002',
     (SELECT id FROM car_models WHERE name='CX-5'      AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Xanh navy', '#1B3A6B', 750000000, 849000000, 'SOLD', '2024-05-10', '2024-11-10'),
    ('VNBM202400000001',
     (SELECT id FROM car_models WHERE name='3 Series'  AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Đen', '#000000', 2100000000, 2399000000, 'SOLD', '2024-06-01', '2024-12-05');

-- ---- RESERVED vehicles: cho CONFIRMED / PENDING / DRAFT orders ----
INSERT INTO vehicles (vin, car_model_id, showroom_id, color, color_code, import_price, selling_price, status, import_date) VALUES
    ('VNHY202400000002',
     (SELECT id FROM car_models WHERE name='Tucson'    AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Trắng', '#FFFFFF', 730000000, 825000000, 'RESERVED', '2024-08-01'),
    ('VNVF202400000002',
     (SELECT id FROM car_models WHERE name='VF9'       AND year=2024),
     (SELECT id FROM showrooms WHERE code='DN01'),
     'Trắng ngọc trai', '#F5F5F5', 1230000000, 1400000000, 'RESERVED', '2024-09-01'),
    ('VNFO202400000001',
     (SELECT id FROM car_models WHERE name='Ranger'    AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Trắng bóng', '#FAFAFA', 800000000, 909000000, 'RESERVED', '2024-09-15'),
    ('VNKI202400000001',
     (SELECT id FROM car_models WHERE name='Sportage'  AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Xám titan', '#808080', 770000000, 869000000, 'RESERVED', '2024-10-01'),
    ('VNTY202400000005',
     (SELECT id FROM car_models WHERE name='Fortuner'  AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Trắng', '#FFFFFF', 1120000000, 1265000000, 'RESERVED', '2024-10-15'),
    ('VNKI202400000002',
     (SELECT id FROM car_models WHERE name='Carnival'  AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Đen', '#000000', 1430000000, 1619000000, 'RESERVED', '2024-11-01');

-- ---- AVAILABLE vehicles: đã huỷ đơn → trả lại kho ----
INSERT INTO vehicles (vin, car_model_id, showroom_id, color, color_code, import_price, selling_price, status, import_date) VALUES
    ('VNVF202400000003',
     (SELECT id FROM car_models WHERE name='VF6'       AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Xanh mint', '#98FF98', 590000000, 675000000, 'AVAILABLE', '2024-07-01'),
    ('VNFO202400000002',
     (SELECT id FROM car_models WHERE name='Territory' AND year=2024),
     (SELECT id FROM showrooms WHERE code='DN01'),
     'Bạc', '#C0C0C0', 730000000, 822000000, 'AVAILABLE', '2024-07-15');

-- ---- AVAILABLE vehicles: chưa có đơn ----
INSERT INTO vehicles (vin, car_model_id, showroom_id, color, color_code, import_price, selling_price, status, import_date) VALUES
    ('VNTY202400000006',
     (SELECT id FROM car_models WHERE name='Camry'       AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Trắng ngọc trai', '#F8F8FF', 1060000000, 1190000000, 'AVAILABLE', '2024-08-01'),
    ('VNTY202400000007',
     (SELECT id FROM car_models WHERE name='Innova Cross' AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Bạc', '#C0C0C0', 870000000, 990000000, 'AVAILABLE', '2024-08-10'),
    ('VNTY202400000008',
     (SELECT id FROM car_models WHERE name='Land Cruiser' AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Đen', '#000000', 4900000000, 5500000000, 'AVAILABLE', '2024-09-01'),
    ('VNTY202400000009',
     (SELECT id FROM car_models WHERE name='Hilux'        AND year=2024),
     (SELECT id FROM showrooms WHERE code='DN01'),
     'Trắng', '#FFFFFF', 810000000, 918000000, 'AVAILABLE', '2024-09-05'),
    ('VNHO202400000004',
     (SELECT id FROM car_models WHERE name='Jazz'         AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Xanh mint', '#98FF98', 480000000, 544000000, 'AVAILABLE', '2024-09-20'),
    ('VNHO202400000005',
     (SELECT id FROM car_models WHERE name='Accord'       AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Xám ánh kim', '#A8A9AD', 1160000000, 1319000000, 'AVAILABLE', '2024-10-01'),
    ('VNMZ202400000003',
     (SELECT id FROM car_models WHERE name='CX-3'         AND year=2024),
     (SELECT id FROM showrooms WHERE code='DN01'),
     'Đỏ pha lê', '#DC143C', 540000000, 619000000, 'AVAILABLE', '2024-10-05'),
    ('VNMZ202400000004',
     (SELECT id FROM car_models WHERE name='CX-8'         AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Trắng ngọc trai', '#F5F5F5', 1060000000, 1199000000, 'AVAILABLE', '2024-10-10'),
    ('VNBM202400000002',
     (SELECT id FROM car_models WHERE name='5 Series'     AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Trắng ngọc trai', '#F5F5F5', 3100000000, 3499000000, 'AVAILABLE', '2024-10-15'),
    ('VNME202400000002',
     (SELECT id FROM car_models WHERE name='GLC 200'      AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Bạc', '#C0C0C0', 2250000000, 2549000000, 'AVAILABLE', '2024-10-20'),
    ('VNHY202400000003',
     (SELECT id FROM car_models WHERE name='Santa Fe'     AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Đen', '#000000', 1270000000, 1445000000, 'AVAILABLE', '2024-11-01'),
    ('VNKI202400000003',
     (SELECT id FROM car_models WHERE name='Seltos'       AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Trắng', '#FFFFFF', 615000000, 699000000, 'AVAILABLE', '2024-11-05'),
    ('VNKI202400000004',
     (SELECT id FROM car_models WHERE name='K5'           AND year=2024),
     (SELECT id FROM showrooms WHERE code='DN01'),
     'Xám titan', '#808080', 770000000, 869000000, 'AVAILABLE', '2024-11-10'),
    ('VNVF202400000004',
     (SELECT id FROM car_models WHERE name='VF5 Plus'     AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Xanh mint', '#98FF98', 400000000, 458000000, 'AVAILABLE', '2024-11-15'),
    ('VNVF202400000005',
     (SELECT id FROM car_models WHERE name='VF7'          AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Đen', '#000000', 750000000, 850000000, 'AVAILABLE', '2024-11-20'),
    ('VNFO202400000003',
     (SELECT id FROM car_models WHERE name='Everest'      AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Trắng', '#FFFFFF', 1190000000, 1349000000, 'AVAILABLE', '2024-12-01'),
    ('VNCH202400000001',
     (SELECT id FROM car_models WHERE name='Colorado'     AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Trắng bóng', '#FAFAFA', 690000000, 778000000, 'AVAILABLE', '2024-12-05'),
    ('VNCH202400000002',
     (SELECT id FROM car_models WHERE name='Trailblazer'  AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Đen', '#000000', 830000000, 939000000, 'AVAILABLE', '2024-12-10'),
    ('VNMZ202400000005',
     (SELECT id FROM car_models WHERE name='Mazda2'       AND year=2024),
     (SELECT id FROM showrooms WHERE code='DN01'),
     'Vàng ánh kim', '#FFD700', 420000000, 479000000, 'AVAILABLE', '2024-12-15'),
    ('VNVF202400000006',
     (SELECT id FROM car_models WHERE name='VF6'          AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN02'),
     'Đỏ', '#CC0000', 590000000, 675000000, 'AVAILABLE', '2025-01-05'),
    ('VNHY202400000004',
     (SELECT id FROM car_models WHERE name='Stargazer'    AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Bạc', '#C0C0C0', 550000000, 625000000, 'AVAILABLE', '2025-01-10'),
    ('VNME202400000003',
     (SELECT id FROM car_models WHERE name='E 300'        AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Đen', '#000000', 2720000000, 3069000000, 'AVAILABLE', '2025-01-15');

-- ---- MAINTENANCE vehicles ----
INSERT INTO vehicles (vin, car_model_id, showroom_id, color, color_code, import_price, selling_price, status, import_date, mileage, notes) VALUES
    ('VNTY202300000001',
     (SELECT id FROM car_models WHERE name='Vios'     AND year=2024),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'Trắng', '#FFFFFF', 450000000, 499000000, 'MAINTENANCE', '2023-05-10', 45000,
     'Đang bảo dưỡng định kỳ 45.000km — thay dầu, lọc gió, kiểm tra phanh'),
    ('VNHO202300000001',
     (SELECT id FROM car_models WHERE name='City'     AND year=2024),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'Bạc', '#C0C0C0', 450000000, 499000000, 'MAINTENANCE', '2023-06-20', 38500,
     'Sửa chữa hệ thống điều hoà — dự kiến hoàn thành 3 ngày');

-- ============================================================
-- SECTION 7: ORDERS (20 đơn — tất cả trạng thái)
-- 11 COMPLETED (dữ liệu biểu đồ doanh thu tháng 1-11/2024)
-- 2 DELIVERED, 2 CONFIRMED, 2 PENDING, 2 DRAFT, 2 CANCELLED
-- ============================================================

-- ---- COMPLETED orders (đã hoàn tất + thanh toán đủ) ----
INSERT INTO orders (order_code, customer_id, employee_id, showroom_id, status,
                    order_date, delivery_date, subtotal, discount_amount, discount_pct,
                    total_amount, commission_amt, notes) VALUES
    ('DH202401150001',
     (SELECT id FROM customers WHERE customer_code='KH000002'),
     (SELECT id FROM employees WHERE employee_code='EMP000005'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'COMPLETED', '2024-01-15 09:30:00', '2024-01-25 14:00:00',
     1190000000, 0, 0, 1190000000, 23800000,
     'Khách hàng VIP — thanh toán tiền mặt toàn bộ'),

    ('DH202402200001',
     (SELECT id FROM customers WHERE customer_code='KH000003'),
     (SELECT id FROM employees WHERE employee_code='EMP000007'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'COMPLETED', '2024-02-20 10:00:00', '2024-02-28 16:00:00',
     1265000000, 20000000, 1.58, 1245000000, 31125000,
     'Giảm 20tr cho khách hàng thân thiết'),

    ('DH202403100001',
     (SELECT id FROM customers WHERE customer_code='KH000004'),
     (SELECT id FROM employees WHERE employee_code='EMP000009'),
     (SELECT id FROM showrooms WHERE code='DN01'),
     'COMPLETED', '2024-03-10 11:00:00', '2024-03-15 15:00:00',
     529000000, 0, 0, 529000000, 10580000,
     'Khách hàng lần đầu mua xe'),

    ('DH202403250001',
     (SELECT id FROM customers WHERE customer_code='KH000014'),
     (SELECT id FROM employees WHERE employee_code='EMP000007'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'COMPLETED', '2024-03-25 14:00:00', '2024-03-28 10:00:00',
     529000000, 10000000, 1.89, 519000000, 12975000,
     'Mua cho đội xe công ty — giảm giá theo hợp đồng'),

    ('DH202404150001',
     (SELECT id FROM customers WHERE customer_code='KH000005'),
     (SELECT id FROM employees WHERE employee_code='EMP000005'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'COMPLETED', '2024-04-15 09:00:00', '2024-04-20 14:00:00',
     998000000, 0, 0, 998000000, 19960000,
     NULL),

    ('DH202405200001',
     (SELECT id FROM customers WHERE customer_code='KH000010'),
     (SELECT id FROM employees WHERE employee_code='EMP000008'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'COMPLETED', '2024-05-20 10:30:00', '2024-05-30 11:00:00',
     1889000000, 50000000, 2.65, 1839000000, 55170000,
     'Khách VIP — giảm 50tr, bao gồm gói bảo hiểm xe cao cấp'),

    ('DH202406100001',
     (SELECT id FROM customers WHERE customer_code='KH000008'),
     (SELECT id FROM employees WHERE employee_code='EMP000009'),
     (SELECT id FROM showrooms WHERE code='DN01'),
     'COMPLETED', '2024-06-10 11:30:00', '2024-06-15 16:00:00',
     719000000, 0, 0, 719000000, 14380000,
     NULL),

    ('DH202407150001',
     (SELECT id FROM customers WHERE customer_code='KH000009'),
     (SELECT id FROM employees WHERE employee_code='EMP000005'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'COMPLETED', '2024-07-15 09:00:00', '2024-07-20 14:00:00',
     900000000, 30000000, 3.33, 870000000, 26100000,
     'Xe điện VinFast — tặng gói sạc tại nhà'),

    ('DH202408100001',
     (SELECT id FROM customers WHERE customer_code='KH000006'),
     (SELECT id FROM employees WHERE employee_code='EMP000007'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'COMPLETED', '2024-08-10 10:00:00', '2024-08-15 15:00:00',
     699000000, 0, 0, 699000000, 13980000,
     NULL),

    ('DH202409050001',
     (SELECT id FROM customers WHERE customer_code='KH000007'),
     (SELECT id FROM employees WHERE employee_code='EMP000008'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'COMPLETED', '2024-09-05 09:30:00', '2024-09-10 14:00:00',
     699000000, 0, 0, 699000000, 20970000,
     NULL),

    ('DH202410200001',
     (SELECT id FROM customers WHERE customer_code='KH000015'),
     (SELECT id FROM employees WHERE employee_code='EMP000006'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'COMPLETED', '2024-10-20 10:00:00', '2024-10-22 16:00:00',
     838000000, 38000000, 4.54, 800000000, 20000000,
     'Tập đoàn mua xe cho ban lãnh đạo — giảm đặc biệt');

-- ---- DELIVERED orders (đã giao xe, chưa thanh toán đủ) ----
INSERT INTO orders (order_code, customer_id, employee_id, showroom_id, status,
                    order_date, delivery_date, subtotal, discount_amount, discount_pct,
                    total_amount, commission_amt, notes) VALUES
    ('DH202411050001',
     (SELECT id FROM customers WHERE customer_code='KH000006'),
     (SELECT id FROM employees WHERE employee_code='EMP000005'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'DELIVERED', '2024-11-05 09:00:00', '2024-11-10 14:00:00',
     849000000, 0, 0, 849000000, 16980000,
     'Khách đặt cọc 500tr, thanh toán phần còn lại sau'),

    ('DH202412010001',
     (SELECT id FROM customers WHERE customer_code='KH000010'),
     (SELECT id FROM employees WHERE employee_code='EMP000008'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'DELIVERED', '2024-12-01 10:00:00', '2024-12-05 16:00:00',
     2399000000, 0, 0, 2399000000, 71970000,
     'BMW 3 Series — khách VIP, chờ chuyển khoản từ ngân hàng');

-- ---- CONFIRMED orders (đã xác nhận, chưa giao) ----
INSERT INTO orders (order_code, customer_id, employee_id, showroom_id, status,
                    order_date, subtotal, discount_amount, discount_pct,
                    total_amount, commission_amt, notes) VALUES
    ('DH202501150001',
     (SELECT id FROM customers WHERE customer_code='KH000011'),
     (SELECT id FROM employees WHERE employee_code='EMP000005'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'CONFIRMED', '2025-01-15 10:00:00',
     825000000, 0, 0, 825000000, 16500000,
     'Dự kiến giao xe tuần tới'),

    ('DH202502010001',
     (SELECT id FROM customers WHERE customer_code='KH000016'),
     (SELECT id FROM employees WHERE employee_code='EMP000009'),
     (SELECT id FROM showrooms WHERE code='DN01'),
     'CONFIRMED', '2025-02-01 09:00:00',
     1400000000, 70000000, 5.0, 1330000000, 33250000,
     'Doanh nghiệp — hợp đồng đã ký, giảm 5% theo chính sách B2B');

-- ---- PENDING orders (chờ xử lý) ----
INSERT INTO orders (order_code, customer_id, employee_id, showroom_id, status,
                    order_date, subtotal, discount_amount, discount_pct,
                    total_amount, commission_amt, notes) VALUES
    ('DH202503050001',
     (SELECT id FROM customers WHERE customer_code='KH000012'),
     (SELECT id FROM employees WHERE employee_code='EMP000005'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'PENDING', '2025-03-05 11:00:00',
     909000000, 0, 0, 909000000, 18180000,
     'Đang chờ khách hàng xác nhận phương thức thanh toán'),

    ('DH202503100001',
     (SELECT id FROM customers WHERE customer_code='KH000013'),
     (SELECT id FROM employees WHERE employee_code='EMP000007'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'PENDING', '2025-03-10 14:00:00',
     869000000, 0, 0, 869000000, 17380000,
     'Khách đang cân nhắc màu xe — chờ phản hồi');

-- ---- DRAFT orders (nháp, chưa xác nhận) ----
INSERT INTO orders (order_code, customer_id, employee_id, showroom_id, status,
                    order_date, subtotal, discount_amount, discount_pct,
                    total_amount, commission_amt, notes) VALUES
    ('DH202504010001',
     (SELECT id FROM customers WHERE customer_code='KH000001'),
     (SELECT id FROM employees WHERE employee_code='EMP000006'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     'DRAFT', '2025-04-01 09:30:00',
     1265000000, 0, 0, 1265000000, 31625000,
     'Đơn nháp — đang tư vấn khách lần đầu'),

    ('DH202504020001',
     (SELECT id FROM customers WHERE customer_code='KH000017'),
     (SELECT id FROM employees WHERE employee_code='EMP000008'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'DRAFT', '2025-04-02 10:00:00',
     1619000000, 0, 0, 1619000000, 48570000,
     'Công ty vận tải — đang đàm phán giá và số lượng');

-- ---- CANCELLED orders ----
INSERT INTO orders (order_code, customer_id, employee_id, showroom_id, status,
                    order_date, subtotal, discount_amount, discount_pct,
                    total_amount, commission_amt, notes) VALUES
    ('DH202409300001',
     (SELECT id FROM customers WHERE customer_code='KH000007'),
     (SELECT id FROM employees WHERE employee_code='EMP000007'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     'CANCELLED', '2024-09-30 09:00:00',
     675000000, 0, 0, 675000000, 13500000,
     'Khách huỷ do thay đổi nhu cầu — chọn xe khác'),

    ('DH202410150001',
     (SELECT id FROM customers WHERE customer_code='KH000003'),
     (SELECT id FROM employees WHERE employee_code='EMP000009'),
     (SELECT id FROM showrooms WHERE code='DN01'),
     'CANCELLED', '2024-10-15 11:00:00',
     822000000, 0, 0, 822000000, 16440000,
     'Huỷ do khách không đủ điều kiện vay ngân hàng');

-- ============================================================
-- SECTION 8: ORDER ITEMS
-- ============================================================

INSERT INTO order_items (order_id, vehicle_id, unit_price, discount, line_total) VALUES
    -- COMPLETED orders
    ((SELECT id FROM orders WHERE order_code='DH202401150001'),
     (SELECT id FROM vehicles WHERE vin='VNTY202400000001'),
     1190000000, 0, 1190000000),
    ((SELECT id FROM orders WHERE order_code='DH202402200001'),
     (SELECT id FROM vehicles WHERE vin='VNTY202400000002'),
     1265000000, 20000000, 1245000000),
    ((SELECT id FROM orders WHERE order_code='DH202403100001'),
     (SELECT id FROM vehicles WHERE vin='VNTY202400000003'),
     529000000, 0, 529000000),
    ((SELECT id FROM orders WHERE order_code='DH202403250001'),
     (SELECT id FROM vehicles WHERE vin='VNHO202400000001'),
     529000000, 10000000, 519000000),
    ((SELECT id FROM orders WHERE order_code='DH202404150001'),
     (SELECT id FROM vehicles WHERE vin='VNHO202400000002'),
     998000000, 0, 998000000),
    ((SELECT id FROM orders WHERE order_code='DH202405200001'),
     (SELECT id FROM vehicles WHERE vin='VNME202400000001'),
     1889000000, 50000000, 1839000000),
    ((SELECT id FROM orders WHERE order_code='DH202406100001'),
     (SELECT id FROM vehicles WHERE vin='VNMZ202400000001'),
     719000000, 0, 719000000),
    ((SELECT id FROM orders WHERE order_code='DH202407150001'),
     (SELECT id FROM vehicles WHERE vin='VNVF202400000001'),
     900000000, 30000000, 870000000),
    ((SELECT id FROM orders WHERE order_code='DH202408100001'),
     (SELECT id FROM vehicles WHERE vin='VNHY202400000001'),
     699000000, 0, 699000000),
    ((SELECT id FROM orders WHERE order_code='DH202409050001'),
     (SELECT id FROM vehicles WHERE vin='VNHO202400000003'),
     699000000, 0, 699000000),
    ((SELECT id FROM orders WHERE order_code='DH202410200001'),
     (SELECT id FROM vehicles WHERE vin='VNTY202400000004'),
     838000000, 38000000, 800000000),
    -- DELIVERED orders
    ((SELECT id FROM orders WHERE order_code='DH202411050001'),
     (SELECT id FROM vehicles WHERE vin='VNMZ202400000002'),
     849000000, 0, 849000000),
    ((SELECT id FROM orders WHERE order_code='DH202412010001'),
     (SELECT id FROM vehicles WHERE vin='VNBM202400000001'),
     2399000000, 0, 2399000000),
    -- CONFIRMED orders
    ((SELECT id FROM orders WHERE order_code='DH202501150001'),
     (SELECT id FROM vehicles WHERE vin='VNHY202400000002'),
     825000000, 0, 825000000),
    ((SELECT id FROM orders WHERE order_code='DH202502010001'),
     (SELECT id FROM vehicles WHERE vin='VNVF202400000002'),
     1400000000, 70000000, 1330000000),
    -- PENDING orders
    ((SELECT id FROM orders WHERE order_code='DH202503050001'),
     (SELECT id FROM vehicles WHERE vin='VNFO202400000001'),
     909000000, 0, 909000000),
    ((SELECT id FROM orders WHERE order_code='DH202503100001'),
     (SELECT id FROM vehicles WHERE vin='VNKI202400000001'),
     869000000, 0, 869000000),
    -- DRAFT orders
    ((SELECT id FROM orders WHERE order_code='DH202504010001'),
     (SELECT id FROM vehicles WHERE vin='VNTY202400000005'),
     1265000000, 0, 1265000000),
    ((SELECT id FROM orders WHERE order_code='DH202504020001'),
     (SELECT id FROM vehicles WHERE vin='VNKI202400000002'),
     1619000000, 0, 1619000000),
    -- CANCELLED orders
    ((SELECT id FROM orders WHERE order_code='DH202409300001'),
     (SELECT id FROM vehicles WHERE vin='VNVF202400000003'),
     675000000, 0, 675000000),
    ((SELECT id FROM orders WHERE order_code='DH202410150001'),
     (SELECT id FROM vehicles WHERE vin='VNFO202400000002'),
     822000000, 0, 822000000);

-- ============================================================
-- SECTION 9: INVOICES
-- (chỉ tạo cho DELIVERED và COMPLETED orders)
-- ============================================================

INSERT INTO invoices (invoice_code, order_id, issued_date, due_date, total_amount, paid_amount, status) VALUES
    -- COMPLETED → PAID
    ('INV202401150001', (SELECT id FROM orders WHERE order_code='DH202401150001'),
     '2024-01-25 14:30:00', '2024-02-25 00:00:00', 1190000000, 1190000000, 'PAID'),
    ('INV202402200001', (SELECT id FROM orders WHERE order_code='DH202402200001'),
     '2024-02-28 16:30:00', '2024-03-28 00:00:00', 1245000000, 1245000000, 'PAID'),
    ('INV202403100001', (SELECT id FROM orders WHERE order_code='DH202403100001'),
     '2024-03-15 15:30:00', '2024-04-15 00:00:00',  529000000,  529000000, 'PAID'),
    ('INV202403250001', (SELECT id FROM orders WHERE order_code='DH202403250001'),
     '2024-03-28 10:30:00', '2024-04-28 00:00:00',  519000000,  519000000, 'PAID'),
    ('INV202404150001', (SELECT id FROM orders WHERE order_code='DH202404150001'),
     '2024-04-20 14:30:00', '2024-05-20 00:00:00',  998000000,  998000000, 'PAID'),
    ('INV202405200001', (SELECT id FROM orders WHERE order_code='DH202405200001'),
     '2024-05-30 11:30:00', '2024-06-30 00:00:00', 1839000000, 1839000000, 'PAID'),
    ('INV202406100001', (SELECT id FROM orders WHERE order_code='DH202406100001'),
     '2024-06-15 16:30:00', '2024-07-15 00:00:00',  719000000,  719000000, 'PAID'),
    ('INV202407150001', (SELECT id FROM orders WHERE order_code='DH202407150001'),
     '2024-07-20 14:30:00', '2024-08-20 00:00:00',  870000000,  870000000, 'PAID'),
    ('INV202408100001', (SELECT id FROM orders WHERE order_code='DH202408100001'),
     '2024-08-15 15:30:00', '2024-09-15 00:00:00',  699000000,  699000000, 'PAID'),
    ('INV202409050001', (SELECT id FROM orders WHERE order_code='DH202409050001'),
     '2024-09-10 14:30:00', '2024-10-10 00:00:00',  699000000,  699000000, 'PAID'),
    ('INV202410200001', (SELECT id FROM orders WHERE order_code='DH202410200001'),
     '2024-10-22 16:30:00', '2024-11-22 00:00:00',  800000000,  800000000, 'PAID'),
    -- DELIVERED → PARTIAL (đặt cọc 500tr)
    ('INV202411050001', (SELECT id FROM orders WHERE order_code='DH202411050001'),
     '2024-11-10 14:30:00', '2024-12-10 00:00:00',  849000000,  500000000, 'PARTIAL'),
    -- DELIVERED → UNPAID
    ('INV202412010001', (SELECT id FROM orders WHERE order_code='DH202412010001'),
     '2024-12-05 16:30:00', '2025-01-05 00:00:00', 2399000000,         0, 'UNPAID');

-- ============================================================
-- SECTION 10: PAYMENTS (tất cả phương thức: CASH, BANK_TRANSFER, INSTALLMENT, CARD)
-- ============================================================

INSERT INTO payments (invoice_id, amount, payment_method, reference_no, notes) VALUES
    -- INV202401150001 — CASH toàn bộ
    ((SELECT id FROM invoices WHERE invoice_code='INV202401150001'),
     1190000000, 'CASH', NULL, 'Thanh toán tiền mặt tại showroom'),

    -- INV202402200001 — BANK_TRANSFER
    ((SELECT id FROM invoices WHERE invoice_code='INV202402200001'),
     1245000000, 'BANK_TRANSFER', 'FT240228001234',
     'Chuyển khoản Vietcombank — đã xác nhận'),

    -- INV202403100001 — INSTALLMENT (trả góp 2 lần)
    ((SELECT id FROM invoices WHERE invoice_code='INV202403100001'),
     300000000, 'INSTALLMENT', 'INST240315001',
     'Đợt 1: trả trước 300tr — vay ngân hàng BIDV'),
    ((SELECT id FROM invoices WHERE invoice_code='INV202403100001'),
     229000000, 'BANK_TRANSFER', 'FT240401001235',
     'Đợt 2: ngân hàng giải ngân 229tr'),

    -- INV202403250001 — CARD + CASH
    ((SELECT id FROM invoices WHERE invoice_code='INV202403250001'),
     200000000, 'CARD', 'CARD240328001', 'Quẹt thẻ Visa Platinum'),
    ((SELECT id FROM invoices WHERE invoice_code='INV202403250001'),
     319000000, 'CASH', NULL, 'Thanh toán tiền mặt phần còn lại'),

    -- INV202404150001 — BANK_TRANSFER
    ((SELECT id FROM invoices WHERE invoice_code='INV202404150001'),
     998000000, 'BANK_TRANSFER', 'FT240420001236',
     'Chuyển khoản Techcombank'),

    -- INV202405200001 — INSTALLMENT (VIP, 3 đợt)
    ((SELECT id FROM invoices WHERE invoice_code='INV202405200001'),
     700000000, 'BANK_TRANSFER', 'FT240530001237',
     'Đợt 1: chuyển khoản 700tr qua MB Bank'),
    ((SELECT id FROM invoices WHERE invoice_code='INV202405200001'),
     700000000, 'BANK_TRANSFER', 'FT240615001238',
     'Đợt 2: chuyển khoản 700tr'),
    ((SELECT id FROM invoices WHERE invoice_code='INV202405200001'),
     439000000, 'BANK_TRANSFER', 'FT240701001239',
     'Đợt 3: thanh toán phần còn lại 439tr'),

    -- INV202406100001 — CASH
    ((SELECT id FROM invoices WHERE invoice_code='INV202406100001'),
     719000000, 'CASH', NULL, 'Tiền mặt đầy đủ'),

    -- INV202407150001 — BANK_TRANSFER + CARD
    ((SELECT id FROM invoices WHERE invoice_code='INV202407150001'),
     500000000, 'BANK_TRANSFER', 'FT240720001240', 'Chuyển khoản VPBank'),
    ((SELECT id FROM invoices WHERE invoice_code='INV202407150001'),
     370000000, 'CARD', 'CARD240721001', 'Thanh toán thẻ JCB'),

    -- INV202408100001 — INSTALLMENT
    ((SELECT id FROM invoices WHERE invoice_code='INV202408100001'),
     699000000, 'INSTALLMENT', 'INST240815001',
     'Trả góp qua HD Saison — 24 tháng'),

    -- INV202409050001 — BANK_TRANSFER
    ((SELECT id FROM invoices WHERE invoice_code='INV202409050001'),
     699000000, 'BANK_TRANSFER', 'FT240910001241', 'Chuyển khoản ACB'),

    -- INV202410200001 — BANK_TRANSFER (tập đoàn)
    ((SELECT id FROM invoices WHERE invoice_code='INV202410200001'),
     800000000, 'BANK_TRANSFER', 'FT241022001242',
     'Chuyển khoản từ tài khoản công ty XYZ Petroleum'),

    -- INV202411050001 — PARTIAL: đặt cọc 500tr
    ((SELECT id FROM invoices WHERE invoice_code='INV202411050001'),
     500000000, 'BANK_TRANSFER', 'FT241110001243',
     'Đặt cọc 500tr — còn lại 349tr thanh toán khi nhận xe giấy tờ');

-- ============================================================
-- SECTION 11: SERVICE APPOINTMENTS (15 lịch hẹn — đủ trạng thái)
-- ============================================================

INSERT INTO service_appointments (appointment_code, customer_id, vehicle_id, employee_id, showroom_id,
                                   appointment_date, service_type, description, status, notes) VALUES

    -- COMPLETED appointments (5)
    ('SA202401001',
     (SELECT id FROM customers WHERE customer_code='KH000002'),
     (SELECT id FROM vehicles WHERE vin='VNTY202400000001'),
     (SELECT id FROM employees WHERE employee_code='EMP000010'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     '2024-02-15 08:00:00', 'MAINTENANCE',
     'Bảo dưỡng định kỳ 5.000km — thay dầu nhớt, kiểm tra tổng thể',
     'COMPLETED', 'Hoàn thành đúng giờ, không phát sinh thêm'),

    ('SA202403001',
     (SELECT id FROM customers WHERE customer_code='KH000003'),
     (SELECT id FROM vehicles WHERE vin='VNTY202400000002'),
     (SELECT id FROM employees WHERE employee_code='EMP000011'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     '2024-04-10 09:00:00', 'INSPECTION',
     'Kiểm tra trước khi đăng kiểm — đăng kiểm lần đầu',
     'COMPLETED', 'Xe đạt chuẩn, cấp giấy đăng kiểm'),

    ('SA202405001',
     (SELECT id FROM customers WHERE customer_code='KH000004'),
     (SELECT id FROM vehicles WHERE vin='VNTY202400000003'),
     (SELECT id FROM employees WHERE employee_code='EMP000012'),
     (SELECT id FROM showrooms WHERE code='DN01'),
     '2024-06-20 08:30:00', 'REPAIR',
     'Sửa chữa hệ thống điều hoà — không mát, nghi ngờ thiếu gas',
     'COMPLETED', 'Đã nạp thêm gas R134a và vệ sinh dàn lạnh'),

    ('SA202408001',
     (SELECT id FROM customers WHERE customer_code='KH000009'),
     (SELECT id FROM vehicles WHERE vin='VNVF202400000001'),
     (SELECT id FROM employees WHERE employee_code='EMP000010'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     '2024-09-05 14:00:00', 'WARRANTY',
     'Cập nhật phần mềm OTA + kiểm tra pin — trong thời hạn bảo hành',
     'COMPLETED', 'Cập nhật firmware v3.2.1 thành công'),

    ('SA202410001',
     (SELECT id FROM customers WHERE customer_code='KH000005'),
     (SELECT id FROM vehicles WHERE vin='VNHO202400000002'),
     (SELECT id FROM employees WHERE employee_code='EMP000011'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     '2024-10-15 10:00:00', 'MAINTENANCE',
     'Bảo dưỡng định kỳ 10.000km — thay dầu, lọc dầu, lọc gió, kiểm tra phanh',
     'COMPLETED', 'Phát hiện phanh sau mòn — đã thay má phanh'),

    -- IN_PROGRESS appointments (3)
    ('SA202503001',
     (SELECT id FROM customers WHERE customer_code='KH000006'),
     (SELECT id FROM vehicles WHERE vin='VNMZ202400000002'),
     (SELECT id FROM employees WHERE employee_code='EMP000010'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     '2025-04-27 08:00:00', 'REPAIR',
     'Tiếng kêu lạ từ hộp số — nghi hỏng vòng bi',
     'IN_PROGRESS', 'Đang tháo kiểm tra — dự kiến 2 ngày'),

    ('SA202503002',
     (SELECT id FROM customers WHERE customer_code='KH000008'),
     (SELECT id FROM vehicles WHERE vin='VNMZ202400000001'),
     (SELECT id FROM employees WHERE employee_code='EMP000012'),
     (SELECT id FROM showrooms WHERE code='DN01'),
     '2025-04-27 09:00:00', 'MAINTENANCE',
     'Bảo dưỡng định kỳ 20.000km',
     'IN_PROGRESS', 'Đang thay nhớt và kiểm tra gầm'),

    ('SA202503003',
     (SELECT id FROM customers WHERE customer_code='KH000014'),
     (SELECT id FROM vehicles WHERE vin='VNHO202400000003'),
     (SELECT id FROM employees WHERE employee_code='EMP000011'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     '2025-04-27 10:00:00', 'INSPECTION',
     'Đăng kiểm định kỳ 2 năm',
     'IN_PROGRESS', 'Đang kiểm tra khí thải và ánh sáng'),

    -- CONFIRMED appointments (3)
    ('SA202504001',
     (SELECT id FROM customers WHERE customer_code='KH000010'),
     (SELECT id FROM vehicles WHERE vin='VNME202400000001'),
     (SELECT id FROM employees WHERE employee_code='EMP000011'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     '2025-05-05 08:00:00', 'MAINTENANCE',
     'Bảo dưỡng Mercedes C200 — 5.000km đầu theo gói dịch vụ',
     'CONFIRMED', 'Lịch đã xác nhận qua điện thoại'),

    ('SA202504002',
     (SELECT id FROM customers WHERE customer_code='KH000015'),
     (SELECT id FROM vehicles WHERE vin='VNTY202400000004'),
     (SELECT id FROM employees WHERE employee_code='EMP000010'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     '2025-05-08 09:00:00', 'WARRANTY',
     'Kiểm tra lỗi cảm biến lùi xe — phản hồi chậm',
     'CONFIRMED', 'Đã gửi thông báo xác nhận qua email'),

    ('SA202504003',
     (SELECT id FROM customers WHERE customer_code='KH000012'),
     (SELECT id FROM vehicles WHERE vin='VNVF202400000004'),
     (SELECT id FROM employees WHERE employee_code='EMP000012'),
     (SELECT id FROM showrooms WHERE code='DN01'),
     '2025-05-10 14:00:00', 'MAINTENANCE',
     'Bảo dưỡng VF5 Plus — kiểm tra pin và hệ thống sạc',
     'CONFIRMED', NULL),

    -- SCHEDULED appointments (chưa xác nhận) (2)
    ('SA202504004',
     (SELECT id FROM customers WHERE customer_code='KH000011'),
     (SELECT id FROM vehicles WHERE vin='VNHY202400000002'),
     NULL,
     (SELECT id FROM showrooms WHERE code='HN01'),
     '2025-05-15 10:00:00', 'MAINTENANCE',
     'Bảo dưỡng Hyundai Tucson — 1.000km đầu',
     'SCHEDULED', 'Chờ phân công kỹ thuật viên'),

    ('SA202504005',
     (SELECT id FROM customers WHERE customer_code='KH000013'),
     (SELECT id FROM vehicles WHERE vin='VNVF202400000002'),
     NULL,
     (SELECT id FROM showrooms WHERE code='DN01'),
     '2025-05-20 08:30:00', 'MAINTENANCE',
     'Bảo dưỡng VF9 — lần đầu sau 3 tháng sử dụng',
     'SCHEDULED', NULL),

    -- CANCELLED appointments (2)
    ('SA202402001',
     (SELECT id FROM customers WHERE customer_code='KH000007'),
     (SELECT id FROM vehicles WHERE vin='VNHO202400000003'),
     (SELECT id FROM employees WHERE employee_code='EMP000011'),
     (SELECT id FROM showrooms WHERE code='HCM01'),
     '2024-03-20 08:00:00', 'INSPECTION',
     'Đăng kiểm lần đầu',
     'CANCELLED', 'Khách huỷ do bận công việc đột xuất'),

    ('SA202407001',
     (SELECT id FROM customers WHERE customer_code='KH000009'),
     (SELECT id FROM vehicles WHERE vin='VNVF202400000001'),
     (SELECT id FROM employees WHERE employee_code='EMP000010'),
     (SELECT id FROM showrooms WHERE code='HN01'),
     '2024-08-01 14:00:00', 'REPAIR',
     'Lỗi màn hình giải trí — treo máy khi kết nối điện thoại',
     'CANCELLED', 'VinFast triệu hồi — xe được sửa miễn phí theo chiến dịch bảo hành');

-- ============================================================
-- SECTION 12: SERVICE RECORDS + SERVICE ITEMS
-- (cho 5 COMPLETED appointments)
-- ============================================================

-- Record 1: Bảo dưỡng Camry 5.000km (SA202401001)
INSERT INTO service_records (appointment_id, vehicle_id, employee_id, service_date,
                              mileage_in, mileage_out, diagnosis, work_done,
                              total_cost, labor_cost, parts_cost, next_service_date)
VALUES (
    (SELECT id FROM service_appointments WHERE appointment_code='SA202401001'),
    (SELECT id FROM vehicles WHERE vin='VNTY202400000001'),
    (SELECT id FROM employees WHERE employee_code='EMP000010'),
    '2024-02-15', 5100, 5105,
    'Xe hoạt động bình thường, đến mức bảo dưỡng định kỳ 5.000km đầu tiên',
    'Thay dầu động cơ 0W-20 Toyota Genuine, thay lọc dầu, kiểm tra áp suất lốp, kiểm tra phanh, vệ sinh lọc gió khoang cabin',
    1850000, 500000, 1350000, '2024-08-15'
);

INSERT INTO service_items (service_record_id, item_type, name, quantity, unit_price, line_total)
SELECT (SELECT id FROM service_records WHERE vehicle_id=(SELECT id FROM vehicles WHERE vin='VNTY202400000001') AND service_date='2024-02-15'),
       v.item_type, v.name, v.quantity, v.unit_price, v.line_total
FROM (VALUES
    ('LABOR'::text, 'Công bảo dưỡng định kỳ 5.000km'::text,        1::numeric(10,2), 500000::numeric(15,2),  500000::numeric(15,2)),
    ('PART'::text,  'Dầu động cơ Toyota Genuine 0W-20 (4L)'::text,  1::numeric(10,2), 850000::numeric(15,2),  850000::numeric(15,2)),
    ('PART'::text,  'Lọc dầu Toyota Genuine'::text,                  1::numeric(10,2), 350000::numeric(15,2),  350000::numeric(15,2)),
    ('PART'::text,  'Lọc gió khoang cabin'::text,                    1::numeric(10,2), 150000::numeric(15,2),  150000::numeric(15,2))
) AS v(item_type, name, quantity, unit_price, line_total);

-- Record 2: Kiểm tra đăng kiểm Fortuner (SA202403001)
INSERT INTO service_records (appointment_id, vehicle_id, employee_id, service_date,
                              mileage_in, mileage_out, diagnosis, work_done,
                              total_cost, labor_cost, parts_cost, next_service_date)
VALUES (
    (SELECT id FROM service_appointments WHERE appointment_code='SA202403001'),
    (SELECT id FROM vehicles WHERE vin='VNTY202400000002'),
    (SELECT id FROM employees WHERE employee_code='EMP000011'),
    '2024-04-10', 8200, 8210,
    'Xe đạt tất cả tiêu chuẩn kỹ thuật, đèn chiếu sáng đầy đủ, phanh hoạt động tốt',
    'Kiểm tra hệ thống chiếu sáng, phanh, lái, còi, khí thải, bổ sung nước rửa kính',
    350000, 350000, 0, '2026-04-10'
);

INSERT INTO service_items (service_record_id, item_type, name, quantity, unit_price, line_total)
SELECT (SELECT id FROM service_records WHERE vehicle_id=(SELECT id FROM vehicles WHERE vin='VNTY202400000002') AND service_date='2024-04-10'),
       v.item_type, v.name, v.quantity, v.unit_price, v.line_total
FROM (VALUES
    ('LABOR'::text, 'Phí kiểm tra kỹ thuật trước đăng kiểm'::text, 1::numeric(10,2), 250000::numeric(15,2), 250000::numeric(15,2)),
    ('LABOR'::text, 'Kiểm tra khí thải'::text,                      1::numeric(10,2), 100000::numeric(15,2), 100000::numeric(15,2))
) AS v(item_type, name, quantity, unit_price, line_total);

-- Record 3: Sửa điều hoà Vios (SA202405001)
INSERT INTO service_records (appointment_id, vehicle_id, employee_id, service_date,
                              mileage_in, mileage_out, diagnosis, work_done,
                              total_cost, labor_cost, parts_cost, next_service_date)
VALUES (
    (SELECT id FROM service_appointments WHERE appointment_code='SA202405001'),
    (SELECT id FROM vehicles WHERE vin='VNTY202400000003'),
    (SELECT id FROM employees WHERE employee_code='EMP000012'),
    '2024-06-20', 18500, 18502,
    'Hệ thống lạnh thiếu gas R134a, dàn lạnh bị bám bẩn làm giảm hiệu suất làm lạnh 40%',
    'Nạp gas R134a 400g, vệ sinh dàn lạnh bằng dung dịch chuyên dụng, kiểm tra áp suất hệ thống, đo nhiệt độ cửa gió',
    2750000, 800000, 1950000, NULL
);

INSERT INTO service_items (service_record_id, item_type, name, quantity, unit_price, line_total)
SELECT (SELECT id FROM service_records WHERE vehicle_id=(SELECT id FROM vehicles WHERE vin='VNTY202400000003') AND service_date='2024-06-20'),
       v.item_type, v.name, v.quantity, v.unit_price, v.line_total
FROM (VALUES
    ('LABOR'::text, 'Công nạp gas và vệ sinh điều hoà'::text,    1::numeric(10,2), 800000::numeric(15,2),  800000::numeric(15,2)),
    ('PART'::text,  'Gas lạnh R134a (400g)'::text,                1::numeric(10,2), 450000::numeric(15,2),  450000::numeric(15,2)),
    ('PART'::text,  'Dung dịch vệ sinh dàn lạnh'::text,          1::numeric(10,2), 200000::numeric(15,2),  200000::numeric(15,2)),
    ('PART'::text,  'Phụ kiện đầu nối van nạp gas'::text,        2::numeric(10,2), 150000::numeric(15,2),  300000::numeric(15,2)),
    ('PART'::text,  'Lọc cabin (thay kết hợp)'::text,            1::numeric(10,2), 250000::numeric(15,2),  250000::numeric(15,2)),
    ('LABOR'::text, 'Phí kiểm tra tổng thể hệ thống lạnh'::text, 1::numeric(10,2), 500000::numeric(15,2),  500000::numeric(15,2)),
    ('PART'::text,  'Vật tư hao hụt'::text,                      1::numeric(10,2), 250000::numeric(15,2),  250000::numeric(15,2))
) AS v(item_type, name, quantity, unit_price, line_total);

-- Record 4: Cập nhật OTA VinFast VF8 (SA202408001)
INSERT INTO service_records (appointment_id, vehicle_id, employee_id, service_date,
                              mileage_in, mileage_out, diagnosis, work_done,
                              total_cost, labor_cost, parts_cost, next_service_date)
VALUES (
    (SELECT id FROM service_appointments WHERE appointment_code='SA202408001'),
    (SELECT id FROM vehicles WHERE vin='VNVF202400000001'),
    (SELECT id FROM employees WHERE employee_code='EMP000010'),
    '2024-09-05', 12800, 12800,
    'Pin hoạt động bình thường SOH 98%, firmware cần cập nhật lên v3.2.1 để tối ưu hành trình',
    'Cập nhật firmware OTA v3.2.1 (tối ưu quản lý pin, cải thiện phạm vi lái 5%), kiểm tra kết nối sạc AC/DC, cân bằng cell pin',
    0, 0, 0, '2025-03-05'
);

INSERT INTO service_items (service_record_id, item_type, name, quantity, unit_price, line_total)
SELECT (SELECT id FROM service_records WHERE vehicle_id=(SELECT id FROM vehicles WHERE vin='VNVF202400000001') AND service_date='2024-09-05'),
       v.item_type, v.name, v.quantity, v.unit_price, v.line_total
FROM (VALUES
    ('LABOR'::text, 'Dịch vụ bảo hành — cập nhật OTA miễn phí'::text, 1::numeric(10,2), 0::numeric(15,2), 0::numeric(15,2))
) AS v(item_type, name, quantity, unit_price, line_total);

-- Record 5: Bảo dưỡng Honda CR-V 10.000km (SA202410001) — phát sinh thay phanh
INSERT INTO service_records (appointment_id, vehicle_id, employee_id, service_date,
                              mileage_in, mileage_out, diagnosis, work_done,
                              total_cost, labor_cost, parts_cost, next_service_date)
VALUES (
    (SELECT id FROM service_appointments WHERE appointment_code='SA202410001'),
    (SELECT id FROM vehicles WHERE vin='VNHO202400000002'),
    (SELECT id FROM employees WHERE employee_code='EMP000011'),
    '2024-10-15', 10200, 10210,
    'Má phanh sau còn 2mm (ngưỡng tối thiểu 3mm) — cần thay ngay. Các hệ thống khác bình thường.',
    'Thay dầu động cơ 0W-20 Honda Genuine 4L, thay lọc dầu, thay má phanh sau 2 bánh, kiểm tra và bơm lốp chuẩn áp suất, vệ sinh hệ thống phanh',
    4650000, 1200000, 3450000, '2025-04-15'
);

INSERT INTO service_items (service_record_id, item_type, name, quantity, unit_price, line_total)
SELECT (SELECT id FROM service_records WHERE vehicle_id=(SELECT id FROM vehicles WHERE vin='VNHO202400000002') AND service_date='2024-10-15'),
       v.item_type, v.name, v.quantity, v.unit_price, v.line_total
FROM (VALUES
    ('LABOR'::text, 'Công bảo dưỡng định kỳ 10.000km'::text,           1::numeric(10,2),  700000::numeric(15,2),  700000::numeric(15,2)),
    ('LABOR'::text, 'Công thay má phanh sau (2 bánh)'::text,            1::numeric(10,2),  500000::numeric(15,2),  500000::numeric(15,2)),
    ('PART'::text,  'Dầu động cơ Honda Genuine 0W-20 (4L)'::text,       1::numeric(10,2),  950000::numeric(15,2),  950000::numeric(15,2)),
    ('PART'::text,  'Lọc dầu Honda Genuine'::text,                       1::numeric(10,2),  320000::numeric(15,2),  320000::numeric(15,2)),
    ('PART'::text,  'Má phanh sau Honda CR-V chính hãng (bộ 2)'::text,  1::numeric(10,2), 1850000::numeric(15,2), 1850000::numeric(15,2)),
    ('PART'::text,  'Dầu vệ sinh phanh'::text,                           1::numeric(10,2),  180000::numeric(15,2),  180000::numeric(15,2)),
    ('PART'::text,  'Vật tư hao hụt'::text,                              1::numeric(10,2),  150000::numeric(15,2),  150000::numeric(15,2))
) AS v(item_type, name, quantity, unit_price, line_total);
