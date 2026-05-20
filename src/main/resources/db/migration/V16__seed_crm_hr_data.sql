-- V16: Seed data for CRM (Leads, Customer Interactions) and HR (Attendance, Payroll)
-- Thêm employee records cho ketoan/thukho, 12 leads, 20 interactions,
-- chấm công tháng 4-5/2025, bảng lương tháng 4/2025

-- ============================================================
-- SECTION 1: EMPLOYEE RECORDS for admin / ketoan / thukho
-- ============================================================

INSERT INTO employees (employee_code, user_id, department_id, showroom_id, position, hire_date, salary, commission_rate, active) VALUES
    ('EMP000014',
     (SELECT id FROM users WHERE username = 'admin'),
     (SELECT id FROM departments WHERE code = 'ADMIN'),
     (SELECT id FROM showrooms WHERE code = 'HN01'),
     'Giam Doc Dieu Hanh', '2021-01-01', 50000000, 0.0, TRUE),

    ('EMP000015',
     (SELECT id FROM users WHERE username = 'ketoan'),
     (SELECT id FROM departments WHERE code = 'FINANCE'),
     (SELECT id FROM showrooms WHERE code = 'HN01'),
     'Ke Toan Vien Chinh', '2022-01-10', 12000000, 0.0, TRUE),

    ('EMP000016',
     (SELECT id FROM users WHERE username = 'thukho'),
     (SELECT id FROM departments WHERE code = 'ADMIN'),
     (SELECT id FROM showrooms WHERE code = 'HN01'),
     'Thu Kho Truong', '2022-03-15', 10000000, 0.0, TRUE);

-- ============================================================
-- SECTION 2: LEADS (12 leads — cover all statuses & sources)
-- ============================================================

INSERT INTO leads (full_name, phone, email, source, status, assigned_employee_id, notes) VALUES

    -- NEW (3 leads — vừa vào, chưa liên hệ)
    ('Nguyen Thi Hue', '0903111001', 'hue.nt@gmail.com', 'FACEBOOK', 'NEW',
     (SELECT id FROM employees WHERE employee_code = 'EMP000005'),
     'Quan tam Honda CR-V 2024, nhan tin qua Facebook Ad xe mau xanh'),

    ('Bui Van Kiet', '0903111002', NULL, 'WALK_IN', 'NEW',
     (SELECT id FROM employees WHERE employee_code = 'EMP000006'),
     'Vao showroom hoi gia Toyota Camry, chưa để lại thong tin email'),

    ('Pham Thi Ngoc', '0903111003', 'ngoc.pham@gmail.com', 'OTHER', 'NEW',
     NULL,
     'Goi dien hoi gia VinFast VF8, chua phan cong nhan vien'),

    -- CONTACTED (2 leads — đã liên hệ lần đầu)
    ('Tran Van Duc', '0903111004', NULL, 'REFERRAL', 'CONTACTED',
     (SELECT id FROM employees WHERE employee_code = 'EMP000007'),
     'Duoc gioi thieu boi KH000002 Nguyen Van Minh, quan tam Mazda CX-5'),

    ('Le Thi My', '0903111005', 'my.lethi@gmail.com', 'FACEBOOK', 'CONTACTED',
     (SELECT id FROM employees WHERE employee_code = 'EMP000005'),
     'Thich Hyundai Santa Fe, da goi dien lan 1, can tu van them ve tai chinh'),

    -- TEST_DRIVE (2 leads — đã lái thử, xác nhận nhu cầu cụ thể)
    ('Hoang Van Thanh', '0903111006', 'thanh.hv@bizmail.vn', 'WALK_IN', 'TEST_DRIVE',
     (SELECT id FROM employees WHERE employee_code = 'EMP000008'),
     'Co nhu cau ro rang: Kia Carnival 8 cho, mua cho gia dinh, du kien thang 6'),

    ('Nguyen Minh Quan', '0903111007', 'quan.nm@company.vn', 'REFERRAL', 'TEST_DRIVE',
     (SELECT id FROM employees WHERE employee_code = 'EMP000006'),
     'Cong ty xay dung can mua 2 xe Ford Ranger cho doi ky thuat — da qua thu tuc duyet ngan sach'),

    -- NEGOTIATING (2 leads — đã gửi báo giá, đang đàm phán)
    ('Do Thi Thanh', '0903111008', 'thanh.do@gmail.com', 'FACEBOOK', 'NEGOTIATING',
     (SELECT id FROM employees WHERE employee_code = 'EMP000009'),
     'Da gui bao gia Mercedes GLC 200, khach dang doi nguoi than tu van them'),

    ('Vu Manh Hung', '0903111009', NULL, 'WALK_IN', 'NEGOTIATING',
     (SELECT id FROM employees WHERE employee_code = 'EMP000007'),
     'Bao gia BMW X3 2024 tu tin, khach dang so sanh voi Volvo XC60'),

    -- CLOSED_WON (2 leads — đã mua xe, linked sang Customer)
    ('Nguyen Thanh Khoa', '0901234511', 'nguyen.khoa@gmail.com', 'FACEBOOK', 'CLOSED_WON',
     (SELECT id FROM employees WHERE employee_code = 'EMP000005'),
     'Da mua VinFast VF6, chuyen thanh khach hang KH000012 — hoan thanh quy 1/2025'),

    ('Le Thi Phuong', '0901234512', 'le.phuong@gmail.com', 'REFERRAL', 'CLOSED_WON',
     (SELECT id FROM employees WHERE employee_code = 'EMP000008'),
     'Da mua Honda Accord, chuyen thanh KH000013 — thanh toan bang chuyen khoan'),

    -- CLOSED_LOST (1 lead — không chốt được)
    ('Cao Van Toan', '0903111010', NULL, 'OTHER', 'CLOSED_LOST',
     (SELECT id FROM employees WHERE employee_code = 'EMP000009'),
     'Chon mua xe cu thay the, gia xe moi vuot ngan sach gia dinh');

-- Link CLOSED_WON leads to converted customers (phone match)
UPDATE leads
SET converted_customer_id = (SELECT id FROM customers WHERE customer_code = 'KH000012')
WHERE phone = '0901234511';

UPDATE leads
SET converted_customer_id = (SELECT id FROM customers WHERE customer_code = 'KH000013')
WHERE phone = '0901234512';

-- ============================================================
-- SECTION 3: CUSTOMER INTERACTIONS (20 interactions)
-- ============================================================

INSERT INTO customer_interactions (lead_id, employee_id, type, content, interaction_date) VALUES

    -- Lead "Tran Van Duc" (CONTACTED)
    ((SELECT id FROM leads WHERE phone = '0903111004'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000007'),
     'CALL', 'Goi dien gioi thieu ban than: dan anh muon mua xe SUV tam trung. Khach hoi gia Mazda CX-5 2024, hen gap truc tiep.',
     '2025-04-10 09:30:00'),

    -- Lead "Le Thi My" (CONTACTED)
    ((SELECT id FROM leads WHERE phone = '0903111005'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000005'),
     'CALL', 'Goi dien lan 1: khach quan tam Santa Fe Hybrid, hoi ve chinh sach tra gop. Da gui link brochure qua Zalo.',
     '2025-04-11 14:00:00'),
    ((SELECT id FROM leads WHERE phone = '0903111005'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000005'),
     'NOTE', 'Khach phan hoi da xem brochure, thich mau trang ngoc trai. Se goi lai vao cuoi tuan de hen lai xe thu.',
     '2025-04-14 08:30:00'),

    -- Lead "Hoang Van Thanh" (QUALIFIED)
    ((SELECT id FROM leads WHERE phone = '0903111006'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000008'),
     'MEETING', 'Gap truc tiep tai showroom HCM01. Khach mang ca gia dinh 7 nguoi den xem xe. Kia Carnival 8 cho dap ung du yeu cau. Khach can them 1 tuan de ban voi vo.',
     '2025-04-12 10:00:00'),
    ((SELECT id FROM leads WHERE phone = '0903111006'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000008'),
     'DEMO', 'Lai thu Carnival CT Premium. Khach va vo deu hai long voi noi that va khong gian. Da trinh bay bao gia chi tiet 1.619.000.000.',
     '2025-04-15 14:30:00'),
    ((SELECT id FROM leads WHERE phone = '0903111006'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000008'),
     'CALL', 'Khach goi lai: can them thoi gian sap xep tai chinh, du kien quyet dinh trong tuan toi.',
     '2025-04-18 11:00:00'),

    -- Lead "Nguyen Minh Quan" (QUALIFIED)
    ((SELECT id FROM leads WHERE phone = '0903111007'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000006'),
     'EMAIL', 'Gui email bao gia 2 xe Ford Ranger Wildtrak 4x4 AT gia uu dai doanh nghiep: 1.818.000.000/2 xe. Kem dieu khoan bao hanh mo rong.',
     '2025-04-14 09:00:00'),
    ((SELECT id FROM leads WHERE phone = '0903111007'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000006'),
     'MEETING', 'Hop voi truong phong hanh chinh cong ty. Ho muon them gam chay cao, da demo Ranger Wildtrak truong hop vung nui cong truong.',
     '2025-04-17 15:00:00'),

    -- Lead "Do Thi Thanh" (PROPOSAL)
    ((SELECT id FROM leads WHERE phone = '0903111008'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000009'),
     'CALL', 'Goi dien lan 1: khach hoi tham xe sang cho vo, ngan sach 2.5-3 ty. Tu van Mercedes GLC 200, E 300 va BMW X3.',
     '2025-04-08 10:00:00'),
    ((SELECT id FROM leads WHERE phone = '0903111008'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000009'),
     'DEMO', 'Lai thu GLC 200 tai showroom. Vo khach rat thich, nhung chong muon kiem tra them E 300. Da sap xep xem both vao cuoi tuan.',
     '2025-04-13 14:00:00'),
    ((SELECT id FROM leads WHERE phone = '0903111008'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000009'),
     'NOTE', 'Da gui bao gia GLC 200 Avantgarde: 2.549.000.000. Khach nhan ket qua hom sau. Tiep tuc follow up.',
     '2025-04-16 17:00:00'),

    -- Lead "Vu Manh Hung" (PROPOSAL)
    ((SELECT id FROM leads WHERE phone = '0903111009'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000007'),
     'MEETING', 'Gap khach lan dau tai showroom. Khach da chay Merc cu 7 nam, muon nang cap. Xem BMW X3 xDrive20i gia 2.999.000.000.',
     '2025-04-09 11:00:00'),
    ((SELECT id FROM leads WHERE phone = '0903111009'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000007'),
     'DEMO', 'Lai thu BMW X3. Khach an tuong voi cong nghe. Da gui bao gia chinh thuc kem qua tang: BHVC 1 nam + phim cach nhiet.',
     '2025-04-14 16:00:00'),
    ((SELECT id FROM leads WHERE phone = '0903111009'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000007'),
     'CALL', 'Khach cho biet dang so sanh voi Volvo XC60 gia 2.850.000.000. De nghi add-on: bao duong 3 nam de canh tranh.',
     '2025-04-20 09:00:00'),

    -- Lead "Nguyen Thanh Khoa" (CLOSED_WON)
    ((SELECT id FROM leads WHERE phone = '0901234511'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000005'),
     'CALL', 'Goi dien follow up sau khi nhan tin Facebook. Khach muon mua xe dien voi gia duoi 700 trieu. Tu van VinFast VF6.',
     '2025-03-02 09:00:00'),
    ((SELECT id FROM leads WHERE phone = '0901234511'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000005'),
     'DEMO', 'Lai thu VF6 Plus mau do. Khach rat hai long voi khong gian noi that va tang kem sac toan quoc.',
     '2025-03-08 14:00:00'),
    ((SELECT id FROM leads WHERE phone = '0901234511'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000005'),
     'NOTE', 'Khach dat coc 50 trieu, du kien giao xe trong 2 tuan. Chuyen doi lead thanh khach hang KH000012.',
     '2025-03-12 11:00:00'),

    -- Lead "Le Thi Phuong" (CLOSED_WON)
    ((SELECT id FROM leads WHERE phone = '0901234512'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000008'),
     'MEETING', 'Gap tai showroom HCM01. Khach duoc gioi thieu boi ban, quan tam Honda Accord hoac Mazda CX-8.',
     '2025-03-05 10:00:00'),
    ((SELECT id FROM leads WHERE phone = '0901234512'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000008'),
     'DEMO', 'Lai thu Honda Accord mau xam anh kim. Khach chon Accord vi cabin yen tinh, trinh bay bao gia 1.319.000.000.',
     '2025-03-10 15:00:00'),
    ((SELECT id FROM leads WHERE phone = '0901234512'),
     (SELECT id FROM employees WHERE employee_code = 'EMP000008'),
     'NOTE', 'Khach chuyen khoan du 1.319.000.000 — hoan tat. Chuyen doi lead thanh KH000013.',
     '2025-03-16 09:00:00');

-- ============================================================
-- SECTION 4: ATTENDANCES — Tháng 4/2025 (22 ngày làm)
-- ============================================================

-- Chèn toàn bộ ngày làm việc (thứ 2-6) tháng 4/2025 mặc định PRESENT
-- cho 5 nhân viên kinh doanh EMP000005-EMP000009
INSERT INTO attendances (employee_id, date, check_in, check_out, status)
SELECT
    e.id,
    d::date,
    '08:00'::time,
    '17:30'::time,
    'PRESENT'
FROM
    (SELECT id FROM employees WHERE employee_code IN
         ('EMP000005','EMP000006','EMP000007','EMP000008','EMP000009')) e,
    generate_series('2025-04-01'::date, '2025-04-30'::date, '1 day'::interval) d
WHERE EXTRACT(DOW FROM d::date) NOT IN (0, 6);

-- EMP000005: 2 ngày đi muộn (8/4, 22/4)
UPDATE attendances SET status = 'LATE', check_in = '09:10', notes = 'Di muon vi ket xe'
WHERE employee_id = (SELECT id FROM employees WHERE employee_code = 'EMP000005')
  AND date IN ('2025-04-08', '2025-04-22');

-- EMP000006: 1 ngày nửa ngày (16/4 khám bệnh), 1 ngày vắng (25/4)
UPDATE attendances SET status = 'HALF_DAY', check_out = '12:00', notes = 'Nghi buoi chieu kham suc khoe dinh ky'
WHERE employee_id = (SELECT id FROM employees WHERE employee_code = 'EMP000006')
  AND date = '2025-04-16';

UPDATE attendances SET status = 'ABSENT', check_in = NULL, check_out = NULL, notes = 'Vang mat khong phep'
WHERE employee_id = (SELECT id FROM employees WHERE employee_code = 'EMP000006')
  AND date = '2025-04-25';

-- EMP000007: 1 ngày nghỉ phép (11/4), 1 ngày đi muộn (23/4)
UPDATE attendances SET status = 'LEAVE', check_in = NULL, check_out = NULL, notes = 'Nghi phep nam'
WHERE employee_id = (SELECT id FROM employees WHERE employee_code = 'EMP000007')
  AND date = '2025-04-11';

UPDATE attendances SET status = 'LATE', check_in = '09:05', notes = 'Di muon 5 phut'
WHERE employee_id = (SELECT id FROM employees WHERE employee_code = 'EMP000007')
  AND date = '2025-04-23';

-- EMP000008: đi làm đầy đủ cả tháng (không update gì)

-- EMP000009: 2 ngày vắng (3/4, 17/4)
UPDATE attendances SET status = 'ABSENT', check_in = NULL, check_out = NULL, notes = 'Vang mat'
WHERE employee_id = (SELECT id FROM employees WHERE employee_code = 'EMP000009')
  AND date IN ('2025-04-03', '2025-04-17');

-- ============================================================
-- SECTION 5: ATTENDANCES — Tháng 5/2025 (22 ngày làm)
-- ============================================================

INSERT INTO attendances (employee_id, date, check_in, check_out, status)
SELECT
    e.id,
    d::date,
    '08:00'::time,
    '17:30'::time,
    'PRESENT'
FROM
    (SELECT id FROM employees WHERE employee_code IN
         ('EMP000005','EMP000006','EMP000007','EMP000008','EMP000009')) e,
    generate_series('2025-05-01'::date, '2025-05-31'::date, '1 day'::interval) d
WHERE EXTRACT(DOW FROM d::date) NOT IN (0, 6);

-- EMP000005: 1 ngày nghỉ phép (9/5), 1 ngày đi muộn (27/5)
UPDATE attendances SET status = 'LEAVE', check_in = NULL, check_out = NULL, notes = 'Nghi phep co viec gia dinh'
WHERE employee_id = (SELECT id FROM employees WHERE employee_code = 'EMP000005')
  AND date = '2025-05-09';

UPDATE attendances SET status = 'LATE', check_in = '09:20', notes = 'Di muon'
WHERE employee_id = (SELECT id FROM employees WHERE employee_code = 'EMP000005')
  AND date = '2025-05-27';

-- EMP000006: đi làm đầy đủ tháng 5
-- (không update)

-- EMP000007: 1 ngày nửa ngày (14/5)
UPDATE attendances SET status = 'HALF_DAY', check_out = '12:00', notes = 'Nghi buoi chieu viec rieng'
WHERE employee_id = (SELECT id FROM employees WHERE employee_code = 'EMP000007')
  AND date = '2025-05-14';

-- EMP000008: đi làm đầy đủ tháng 5
-- (không update)

-- EMP000009: 1 ngày đi muộn (6/5), 1 ngày vắng (20/5)
UPDATE attendances SET status = 'LATE', check_in = '09:30', notes = 'Xe hỏng buoi sang'
WHERE employee_id = (SELECT id FROM employees WHERE employee_code = 'EMP000009')
  AND date = '2025-05-06';

UPDATE attendances SET status = 'ABSENT', check_in = NULL, check_out = NULL, notes = 'Vang mat khong phep'
WHERE employee_id = (SELECT id FROM employees WHERE employee_code = 'EMP000009')
  AND date = '2025-05-20';

-- ============================================================
-- SECTION 6: PAYROLLS — Tháng 4/2025
-- work_days = 22 (tháng 4 có 22 ngày làm)
-- Công thức: baseSalaryEarned = salary × effectiveDays / workDays
--            netSalary = baseSalaryEarned + commission + bonuses - deductions
-- ============================================================

-- EMP000005: 22 công (2 LATE = 1.0 mỗi ngày), commission = 0, thưởng 500k → net 12.5M
-- effectiveDays = 22.0 (PRESENT×20 + LATE×2 = 22)
INSERT INTO payrolls (employee_id, month, year, base_salary, work_days, effective_days,
                      commission, bonuses, deductions, net_salary, status)
VALUES (
    (SELECT id FROM employees WHERE employee_code = 'EMP000005'),
    4, 2025,
    12000000, 22, 22.0,
    0, 500000, 0, 12500000,
    'PAID'
);

-- EMP000006: 21.0 công (HALF_DAY 1 ngày = 0.5, ABSENT 1 ngày = 0), baseSalaryEarned ≈ 14.318M
-- 15M × 21.0 / 22 = 14,318,182 → thưởng 1M, khấu trừ 200k → net ≈ 15.118M
INSERT INTO payrolls (employee_id, month, year, base_salary, work_days, effective_days,
                      commission, bonuses, deductions, net_salary, status)
VALUES (
    (SELECT id FROM employees WHERE employee_code = 'EMP000006'),
    4, 2025,
    15000000, 22, 21.0,
    0, 1000000, 200000, 15118182,
    'APPROVED'
);

-- EMP000007: 21.0 công (LEAVE 1 ngày = 0, LATE 1 ngày = 1.0)
-- 13M × 21.0 / 22 = 12,409,091 → net ≈ 12.409M
INSERT INTO payrolls (employee_id, month, year, base_salary, work_days, effective_days,
                      commission, bonuses, deductions, net_salary, status)
VALUES (
    (SELECT id FROM employees WHERE employee_code = 'EMP000007'),
    4, 2025,
    13000000, 22, 21.0,
    0, 0, 0, 12409091,
    'PAID'
);

-- EMP000008: 22.0 công (full month) + commission từ đơn hàng tháng 4 (DH202504020001 = DRAFT, không tính)
-- Nhưng có bonus KPI quý → commission thưởng 5M, thưởng chốt deal 2M
-- 18M full + 5M commission + 2M thưởng = 25M
INSERT INTO payrolls (employee_id, month, year, base_salary, work_days, effective_days,
                      commission, bonuses, deductions, net_salary, status)
VALUES (
    (SELECT id FROM employees WHERE employee_code = 'EMP000008'),
    4, 2025,
    18000000, 22, 22.0,
    5000000, 2000000, 0, 25000000,
    'PAID'
);

-- EMP000009: 20.0 công (ABSENT 2 ngày = 0)
-- 11M × 20.0 / 22 = 10,000,000 → net 10M
INSERT INTO payrolls (employee_id, month, year, base_salary, work_days, effective_days,
                      commission, bonuses, deductions, net_salary, status)
VALUES (
    (SELECT id FROM employees WHERE employee_code = 'EMP000009'),
    4, 2025,
    11000000, 22, 20.0,
    0, 0, 200000, 9800000,
    'DRAFT'
);

-- ============================================================
-- SECTION 7: PAYROLLS — Tháng 5/2025 (trạng thái DRAFT — đang tính)
-- ============================================================

-- EMP000005: 21.0 công (LEAVE 1 ngày + LATE 1 ngày = 21.0 effective)
-- 12M × 21/22 = 11,454,545
INSERT INTO payrolls (employee_id, month, year, base_salary, work_days, effective_days,
                      commission, bonuses, deductions, net_salary, status)
VALUES (
    (SELECT id FROM employees WHERE employee_code = 'EMP000005'),
    5, 2025,
    12000000, 22, 21.0,
    0, 0, 0, 11454545,
    'DRAFT'
);

-- EMP000006: 22.0 công (full month)
INSERT INTO payrolls (employee_id, month, year, base_salary, work_days, effective_days,
                      commission, bonuses, deductions, net_salary, status)
VALUES (
    (SELECT id FROM employees WHERE employee_code = 'EMP000006'),
    5, 2025,
    15000000, 22, 22.0,
    0, 0, 0, 15000000,
    'DRAFT'
);

-- EMP000007: 21.5 công (HALF_DAY 1 ngày = 0.5)
-- 13M × 21.5/22 = 12,704,545
INSERT INTO payrolls (employee_id, month, year, base_salary, work_days, effective_days,
                      commission, bonuses, deductions, net_salary, status)
VALUES (
    (SELECT id FROM employees WHERE employee_code = 'EMP000007'),
    5, 2025,
    13000000, 22, 21.5,
    0, 0, 0, 12704545,
    'DRAFT'
);

-- EMP000008: 22.0 công (full month) + bao gom commission don DH202504020001 (Carnival, DRAFT → chưa tính)
INSERT INTO payrolls (employee_id, month, year, base_salary, work_days, effective_days,
                      commission, bonuses, deductions, net_salary, status)
VALUES (
    (SELECT id FROM employees WHERE employee_code = 'EMP000008'),
    5, 2025,
    18000000, 22, 22.0,
    0, 0, 0, 18000000,
    'DRAFT'
);

-- EMP000009: 21.0 công (LATE 1 ngày + ABSENT 1 ngày = 21.0 effective)
-- 11M × 21/22 = 10,500,000
INSERT INTO payrolls (employee_id, month, year, base_salary, work_days, effective_days,
                      commission, bonuses, deductions, net_salary, status)
VALUES (
    (SELECT id FROM employees WHERE employee_code = 'EMP000009'),
    5, 2025,
    11000000, 22, 21.0,
    0, 0, 0, 10500000,
    'DRAFT'
);
