-- V13: Thêm tài khoản mặc định cho role KE_TOAN và THU_KHO
-- Mật khẩu: KeToan@123 và ThuKho@123 (BCrypt strength 12)

INSERT INTO users (username, email, password, full_name, phone, enabled, locked)
VALUES (
    'ketoan',
    'ketoan@carmanagement.com',
    '$2b$12$6G/yCqVBlnKNOkSVqZoJreIDYSi6jXpDXQc8Lt7D1Oh92AebikUw6',
    'Nhân Viên Kế Toán',
    '0900000010',
    TRUE,
    FALSE
);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'ketoan'
  AND r.name = 'ROLE_KE_TOAN';

INSERT INTO users (username, email, password, full_name, phone, enabled, locked)
VALUES (
    'thukho',
    'thukho@carmanagement.com',
    '$2b$12$N93y1He13uavAMpnglE5uOO0ikPU.NN3kpApt/f0rgRYPwOgWAQQq',
    'Nhân Viên Thủ Kho',
    '0900000011',
    TRUE,
    FALSE
);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'thukho'
  AND r.name = 'ROLE_THU_KHO';
