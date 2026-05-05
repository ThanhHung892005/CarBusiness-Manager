-- V9: Thêm tài khoản mẫu cho role Sales và Customer

-- Sales user (password: Sales@123)
INSERT INTO users (username, email, password, full_name, phone, enabled) VALUES
    ('sales', 'sales@carmanagement.com',
     '$2y$10$jKqKfJBcq/HP6DdW/Zs0veWeasEj6q9/PiTVWmaUMJnQ1H/fTePXm',
     'Nhân Viên Kinh Doanh', '0900000002', TRUE);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'sales' AND r.name = 'ROLE_SALES';

-- Customer user (password: Customer@123)
INSERT INTO users (username, email, password, full_name, phone, enabled) VALUES
    ('customer', 'customer@carmanagement.com',
     '$2y$10$po1WLKuTrbg2u9LpDoA1P..CYk6hyvwR3i0oCpRqQJ8XZ2XtRKurG',
     'Nguyễn Văn Khách', '0900000003', TRUE);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'customer' AND r.name = 'ROLE_CUSTOMER';
