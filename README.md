# 🚗 Car Business Manager

Hệ thống quản lý nội bộ cho đại lý bán xe mới — xây dựng bằng **Spring Boot 3.2.5 + PostgreSQL 15 + Thymeleaf**.

Dành cho doanh nghiệp 6–15 người với 4 vai trò nghiệp vụ: Giám đốc, NV Kinh doanh, Kế toán, Thủ kho.
Không có cổng tự phục vụ cho khách hàng — mọi tài khoản do Giám đốc tạo và cấp phép.

---

## ✨ Tính năng chính

| Module | URL | Vai trò | Mô tả |
|--------|-----|---------|-------|
| **Dashboard** | `/admin/dashboard` | Giám đốc | KPI tổng hợp, doanh thu 12 tháng, đơn mới |
| **Kho xe** | `/inventory/vehicles` | Giám đốc + Thủ kho + NV KD (xem) | Nhập xe VIN, trạng thái, hình ảnh |
| **CRM / Leads** | `/crm/leads` | Giám đốc + NV KD | Quản lý leads, timeline tương tác, chuyển đổi |
| **Khách hàng** | `/sales/customers` | Giám đốc + NV KD | Hồ sơ, điểm tích lũy, lịch sử mua |
| **Đơn hàng** | `/sales/orders` | Giám đốc + NV KD | Tạo đơn, theo dõi trạng thái, giao xe |
| **Báo giá PDF** | `/sales/quotes` | Giám đốc + NV KD | Xuất PDF on-the-fly, không lưu DB |
| **Hóa đơn / Thanh toán** | `/ke-toan/invoices` | Giám đốc + Kế toán | Ghi nhận thanh toán, xuất PDF hóa đơn |
| **Báo cáo** | `/manager/reports` | Giám đốc + Kế toán | Doanh thu theo tháng, xuất Excel |
| **Nhân viên** | `/admin/employees` | Giám đốc | Tạo/sửa nhân viên + tài khoản trong 1 form |
| **Chấm công** | `/admin/hr/attendance` | Giám đốc | Chấm công ngày, trạng thái đi làm |
| **Bảng lương** | `/admin/hr/payroll` | Giám đốc | Tính lương tự động = công × lương + hoa hồng |
| **KPI nhân viên** | `/admin/kpi` | Giám đốc | Xếp hạng doanh số, hoa hồng theo tháng/năm |
| **Audit Log** | `/admin/audit-logs` | Giám đốc | Lịch sử thao tác toàn hệ thống |
| **Quản trị** | `/admin/brands`, `/admin/models`, `/admin/showrooms` | Giám đốc | Danh mục hãng xe, dòng xe, chi nhánh |

---

## 🛠️ Công nghệ

| Layer | Công nghệ | Phiên bản |
|-------|-----------|-----------|
| Language | Java | 21 (compile JDK 24) |
| Framework | Spring Boot | 3.2.5 |
| Security | Spring Security | 6.x |
| ORM | Spring Data JPA / Hibernate | 6.x |
| Template | Thymeleaf + thymeleaf-extras-springsecurity6 | theo Boot |
| Database | PostgreSQL | 15.17 (Docker) |
| Migration | Flyway | 9.22.3 |
| Build | Maven | 3.9+ |
| Lombok | Project Lombok | 1.18.38 |
| PDF | iText | 8.0.3 |
| Excel | Apache POI | 5.2.5 |
| CSS/JS | Bootstrap 5.3.2, jQuery 3.7.1, Chart.js 4.4.2 | CDN |
| Icons | Font Awesome 6.5.1 | CDN |
| Tests | JUnit 5, Mockito, TestContainers | theo Boot |
| Coverage | JaCoCo | 0.8.11 |

---

## 🚀 Hướng dẫn chạy

### Yêu cầu
- **JDK 24** (Lombok 1.18.38 chưa hỗ trợ JDK 25)
- **Maven 3.9+**
- **Docker Desktop** (chạy PostgreSQL)

### Khởi động nhanh

```bash
# 1. Clone repo
git clone <repo-url>
cd "CarBusiness Manager"

# 2. Khởi động PostgreSQL (lần đầu)
docker-compose up -d

# 3. Chạy ứng dụng
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home \
  mvn spring-boot:run

# 4. Mở trình duyệt
open http://localhost:9090/login
```

> Flyway tự động chạy 17 migration (V1–V17) khi khởi động lần đầu.

### Ports

| Service | Port | URL |
|---------|------|-----|
| Spring Boot App | **9090** | http://localhost:9090 |
| PostgreSQL | **5434** | `localhost:5434/car_management` |
| pgAdmin | **8888** | http://localhost:8888 |

### Tài khoản mặc định

| Username | Password | Vai trò | Dashboard |
|----------|----------|---------|-----------|
| `admin` | `Admin@123` | Giám Đốc | `/admin/dashboard` |
| `sales` | `Sales@123` | NV Kinh Doanh | `/sales/dashboard` |
| `ketoan` | `KeToan@123` | Kế Toán | `/ke-toan/dashboard` |
| `thukho` | `ThuKho@123` | Thủ Kho | `/inventory/dashboard` |

---

## 🗄️ Thiết kế cơ sở dữ liệu (20 bảng)

### ERD tổng quan

```
PHÂN QUYỀN
  users ──M:M── roles
    │
    └──1:1── employees ──M:1── departments
                    │
                    └──M:1── showrooms

DANH MỤC XE
  brands ──1:M── car_models ──1:M── vehicles ──1:M── vehicle_images

LUỒNG BÁN HÀNG
  customers ──1:M── orders ──1:1── invoices ──1:M── payments
                       │
                    order_items
                       │
                    vehicles (FK)

CRM
  leads ──1:M── customer_interactions ──M:1── employees
    │
    └──M:1── employees (assigned)
    └──M:1── customers (converted)

NHÂN SỰ
  employees ──1:M── attendances
  employees ──1:M── payrolls

HỆ THỐNG
  audit_logs (ghi nhận mọi thao tác quan trọng)
```

### Chi tiết 20 bảng

| # | Bảng | Mô tả | Cột chính |
|---|------|-------|-----------|
| 1 | `users` | Tài khoản hệ thống | id, username, password (BCrypt), enabled, locked |
| 2 | `roles` | Vai trò | id, name (ROLE_GIAM_DOC / NV_KINH_DOANH / KE_TOAN / THU_KHO) |
| 3 | `user_roles` | Phân quyền M:M | user_id, role_id |
| 4 | `departments` | Phòng ban | id, code, name |
| 5 | `showrooms` | Chi nhánh | id, code, name, address, city |
| 6 | `employees` | Nhân viên | id, employee_code, user_id (1:1), salary, commission_rate |
| 7 | `brands` | Hãng xe | id, name, country |
| 8 | `car_models` | Dòng xe | id, brand_id, name, year, car_type, base_price |
| 9 | `vehicles` | Xe cá thể | id, vin (UNIQUE), car_model_id, color, selling_price, status |
| 10 | `vehicle_images` | Hình ảnh xe | id, vehicle_id, image_url |
| 11 | `customers` | Khách hàng | id, customer_code, full_name, phone, customer_type, loyalty_points |
| 12 | `orders` | Đơn hàng | id, order_code, customer_id, vehicle_id, employee_id, status, total_amount |
| 13 | `order_items` | Chi tiết đơn | id, order_id, vehicle_id, quantity, unit_price |
| 14 | `invoices` | Hóa đơn | id, invoice_code, order_id, total_amount, paid_amount, status |
| 15 | `payments` | Thanh toán | id, invoice_id, amount, payment_method, payment_date |
| 16 | `audit_logs` | Nhật ký hệ thống | id, username, action, entity_type, entity_id, created_at |
| 17 | `leads` | Leads tiềm năng | id, full_name, phone, source, status, assigned_employee_id, converted_customer_id |
| 18 | `customer_interactions` | Lịch sử tương tác | id, lead_id, employee_id, type, content, interaction_date |
| 19 | `attendances` | Chấm công | id, employee_id, date (UNIQUE/employee), check_in, check_out, status |
| 20 | `payrolls` | Bảng lương | id, employee_id, month, year (UNIQUE/employee+month+year), effective_days, net_salary, status |

### Enums quan trọng

```
VehicleStatus  : AVAILABLE | RESERVED | SOLD | MAINTENANCE
OrderStatus    : DRAFT | CONFIRMED | DELIVERED | COMPLETED | CANCELLED
InvoiceStatus  : UNPAID | PARTIAL | PAID
PaymentMethod  : CASH | BANK_TRANSFER | INSTALLMENT

LeadSource     : FACEBOOK | WALK_IN | REFERRAL | OTHER
LeadStatus     : NEW | CONTACTED | TEST_DRIVE | NEGOTIATING | CLOSED_WON | CLOSED_LOST
InteractionType: CALL | EMAIL | MEETING | DEMO | NOTE | OTHER

AttendanceStatus: PRESENT | LATE | HALF_DAY | ABSENT | LEAVE
PayrollStatus   : DRAFT | APPROVED | PAID

CustomerType   : NEW | REGULAR | VIP | CORPORATE
CarType        : SEDAN | SUV | MPV | PICKUP | HATCHBACK | COUPE | CONVERTIBLE | VAN
```

---

## 📐 Kiến trúc

```
src/main/java/com/carmanagement/
├── config/          # SecurityConfig, WebMvcConfig
├── controller/      # 16 controllers (MVC + JSON cho chart data)
├── service/         # 14 service interfaces + impls
├── repository/      # 14 Spring Data JPA repositories
├── entity/          # 16 JPA entities (@Entity, Lombok @Builder)
├── dto/
│   ├── request/     # Form DTOs (create/update requests)
│   └── response/    # Response DTOs (dashboard stats, KPI)
├── enums/           # 12 enums với displayName tiếng Việt
├── exception/       # GlobalExceptionHandler, custom exceptions
└── security/        # CustomUserDetailsService, CustomAuthSuccessHandler
```

### Quyết định thiết kế quan trọng

- **`FetchType.LAZY` toàn bộ + `open-in-view=false`** — tránh N+1, load explicit bằng `JOIN FETCH`
- **Sentinel date thay vì null** — Hibernate 6 bind null → bytea với PostgreSQL; dùng năm 2000/2099
- **`countQuery` riêng khi có `JOIN FETCH`** — Spring Data tự sinh countQuery sai với DISTINCT/JOIN FETCH
- **Tích hợp User + Employee** — Giám đốc tạo nhân viên + tài khoản trong 1 form `@Transactional`
- **Báo giá không lưu DB** — `ResponseEntity<byte[]>` trả PDF on-the-fly từ POST
- **Audit log + Email `@Async`** — không block HTTP response, transaction độc lập `REQUIRES_NEW`
- **Lombok 1.18.38 bắt buộc** — phiên bản thấp hơn gây `TypeTag::UNKNOWN` với JDK 21+

---

## 🧪 Kiểm thử

```bash
# Chạy unit tests (87 tests, không cần Docker)
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home mvn test

# Với Docker: integration tests cũng chạy (TestContainers PostgreSQL 15)
# Không có Docker: integration tests tự động bỏ qua (disabledWithoutDocker = true)
```

**Phạm vi:**
- `OrderServiceImplTest` (20), `VehicleServiceImplTest` (12), `CustomerServiceImplTest` (13)
- `HrServiceImplTest` (14), `CrmServiceImplTest` (16)
- `ExcelExportServiceTest` (7), `PdfExportServiceTest` (5)
- `OrderFlowIntegrationTest` (5) — TestContainers, skip nếu không có Docker

---

## 🔧 Xử lý sự cố

```bash
# Port 9090 bị chiếm
lsof -ti:9090 | xargs kill -9

# Flyway checksum mismatch (khi sửa file migration đã chạy)
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home \
  mvn flyway:repair \
  -Dflyway.url=jdbc:postgresql://localhost:5434/car_management \
  -Dflyway.user=caruser -Dflyway.password=carpassword123

# Xem log ứng dụng trực tiếp
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home \
  mvn spring-boot:run 2>&1 | tee /tmp/app.log

# pgAdmin
# Truy cập http://localhost:8888
# Email: admin@carmanagement.com / Password: admin123
# Server: host=postgres, port=5432, user=caruser, pass=carpassword123
```

---

## 📂 Flyway Migrations

| Version | File | Nội dung |
|---------|------|----------|
| V1–V6 | schema gốc | users, roles, departments, showrooms, employees, vehicles, orders, audit_logs |
| V7–V10 | seed data | roles, admin, departments, showrooms, brands, 13 nhân viên, 17 khách hàng, xe, đơn |
| V11 | persistent_logins | Spring Security remember-me |
| V12 | refactor roles | 4 role nghiệp vụ mới, xóa service tables |
| V13 | ketoan + thukho | Tài khoản kế toán và thủ kho |
| V14 | crm tables | Schema: leads, customer_interactions |
| V15 | hr tables | Schema: attendances, payrolls |
| V16 | seed crm+hr | 12 leads, 20 tương tác, 220 chấm công, 10 bảng lương |
| V17 | fix lead statuses | Fix enum mismatch QUALIFIED→TEST_DRIVE, PROPOSAL→NEGOTIATING |

---

*Hệ thống nội bộ — không có cổng đăng ký tự phục vụ cho khách hàng.*
