-- V11: Refactor roles (đổi tên + thêm mới) và xóa bảng service không còn dùng

-- 1. Xóa dữ liệu service trước (do foreign key constraints)
DELETE FROM service_items;
DELETE FROM service_records;
DELETE FROM service_appointments;

-- 2. Xóa bảng service
DROP TABLE IF EXISTS service_items;
DROP TABLE IF EXISTS service_records;
DROP TABLE IF EXISTS service_appointments;

-- 3. Xóa user_roles của ROLE_CUSTOMER và ROLE_MANAGER (trước khi xóa role)
DELETE FROM user_roles
WHERE role_id IN (SELECT id FROM roles WHERE name IN ('ROLE_CUSTOMER', 'ROLE_MANAGER'));

-- 4. Gán ROLE_GIAM_DOC cho users đang có ROLE_MANAGER (trước khi đổi tên ROLE_ADMIN)
--    Bước này chạy trước khi UPDATE để tránh mất user manager
INSERT INTO user_roles (user_id, role_id)
SELECT ur.user_id, (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
FROM user_roles ur
INNER JOIN roles r ON ur.role_id = r.id AND r.name = 'ROLE_MANAGER'
WHERE NOT EXISTS (
    SELECT 1 FROM user_roles existing
    WHERE existing.user_id = ur.user_id
      AND existing.role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
);

-- 5. Đổi tên role ADMIN → GIAM_DOC, SALES → NV_KINH_DOANH
UPDATE roles SET name = 'ROLE_GIAM_DOC',        description = 'Giám đốc'             WHERE name = 'ROLE_ADMIN';
UPDATE roles SET name = 'ROLE_NV_KINH_DOANH',   description = 'Nhân viên kinh doanh'  WHERE name = 'ROLE_SALES';

-- 6. Xóa role MANAGER và CUSTOMER (user_roles đã xóa ở bước 3)
DELETE FROM roles WHERE name IN ('ROLE_MANAGER', 'ROLE_CUSTOMER');

-- 7. Thêm 2 role mới
INSERT INTO roles (name, description, created_at, updated_at)
VALUES
  ('ROLE_KE_TOAN',  'Kế toán',  NOW(), NOW()),
  ('ROLE_THU_KHO',  'Thủ kho',  NOW(), NOW());
