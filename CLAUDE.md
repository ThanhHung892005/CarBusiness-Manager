# 🚗 HỆ THỐNG QUẢN LÝ DOANH NGHIỆP Ô TÔ (CAR MANAGEMENT SYSTEM)

## 📋 TỔNG QUAN DỰ ÁN

### Thông tin cơ bản
- **Tên dự án**: Car Management System
- **Công nghệ**: Spring Boot 3.2.5 + PostgreSQL 15 + Thymeleaf
- **Mục tiêu**: Xây dựng hệ thống quản lý toàn diện cho doanh nghiệp kinh doanh ô tô
- **Trạng thái hiện tại**: Phase 3 + Phase 3.5 hoàn thành — Đăng ký tự phục vụ (Customer) + Quản lý User (Admin) đã live

### Mô tả
Hệ thống quản lý doanh nghiệp ô tô là ứng dụng web toàn diện hỗ trợ quản lý kho xe, bán hàng, khách hàng, dịch vụ sau bán hàng, nhân sự và báo cáo thống kê. Hệ thống có 4 vai trò người dùng: Admin, Manager, Sales, Customer với phân quyền rõ ràng.

---

## 🚦 TRẠNG THÁI TRIỂN KHAI (cập nhật 2026-04-28)

### ✅ ĐÃ HOÀN THÀNH (Phase 1 + Phase 2 + Phase 3 + Phase 3.5)

#### Hạ tầng & Cấu hình
- ✅ `pom.xml` — Spring Boot 3.2.5, Java 21, Lombok **1.18.38**, MapStruct 1.6.3, Flyway, POI, iText
- ✅ `docker-compose.yml` — PostgreSQL 15 (port **5434**) + pgAdmin (port **8888**)
- ✅ `application.properties` — datasource, JPA, Flyway, Security, Mail, Upload, Pagination
- ✅ `application-dev.properties` — override cho môi trường local (không commit)
- ✅ `.gitignore` — loại trừ credentials, target/, uploads/

#### Database (Flyway migrations — 19 bảng + 10 migrations)
- ✅ `V1` — users, roles, user_roles, departments, showrooms
- ✅ `V2` — employees
- ✅ `V3` — brands, car_models, vehicles, vehicle_images
- ✅ `V4` — customers, orders, order_items, invoices, payments
- ✅ `V5` — service_appointments, service_records, service_items
- ✅ `V6` — audit_logs
- ✅ `V7` — seed data: 4 roles, admin user, 4 departments, 3 showrooms, 10 brands, 10 car models
- ✅ `V8` — fix car_type enum case (Sedan → SEDAN, SUV giữ nguyên)
- ✅ `V9` — tài khoản mẫu Sales (sales/Sales@123) và Customer (customer/Customer@123)
- ✅ `V10` — dữ liệu Việt Nam thực tế: 26 users, 12 employees, 17 customers, 54 car models, 45 vehicles, 21 orders, 13 invoices, 17 payments, 15 service appointments, 5 service records, 21 service items

#### Backend Java — Layers đầy đủ
- ✅ **Enums** (9): OrderStatus, VehicleStatus, AppointmentStatus, CustomerType, PaymentMethod, InvoiceStatus, ServiceType, CarType, AuditAction
- ✅ **Entities** (14 classes): BaseEntity, User, Role, Department, Showroom, Employee, Brand, CarModel, Vehicle, VehicleImage, Customer, Order, OrderItem, Invoice, Payment, ServiceAppointment, ServiceRecord, ServiceItem, AuditLog
- ✅ **Repositories** (13): với JPQL search queries, pagination, custom finders — tất cả đã fix lỗi null-param bytea (CAST AS string cho LIKE, sentinel date cho range); `UserRepository.search()` thêm DISTINCT + empty-string sentinel + countQuery riêng [Phase 3.5]
- ✅ **Services** (11 interfaces + 11 implementations + 2 utility services): Brand, Vehicle, Customer, Order, CarModel, Showroom, Employee, ServiceAppointment, AuditLog, Email, PdfExportService, ExcelExportService, **UserRegistrationService** (Phase 3.5), **UserService** (Phase 3.5)
- ✅ **Controllers** (15): AuthController, HomeController, DashboardController, BrandController, VehicleController, CustomerController, OrderController, CarModelController, ShowroomController, EmployeeController, ServiceAppointmentController, ReportController, AuditLogController, **RegistrationController** (Phase 3.5), **UserController** (Phase 3.5)
- ✅ **DTOs**: VehicleCreateRequest, VehicleSearchRequest, OrderCreateRequest, CustomerCreateRequest, PaymentCreateRequest, DashboardStatsResponse, AppointmentCreateRequest, **UserRegistrationRequest** (Phase 3.5), **UserCreateRequest** (Phase 3.5)
- ✅ **Exception handling**: ResourceNotFoundException, BusinessException, GlobalExceptionHandler (`@ControllerAdvice`)
- ✅ **Utilities**: FileUtil (upload ảnh), CodeGeneratorUtil

#### Spring Security
- ✅ BCrypt password encoder (strength 12)
- ✅ Form login custom (`/login`), logout (`/logout`)
- ✅ Redirect sau login theo role: Admin→`/admin/dashboard`, Manager→`/manager/dashboard`, Sales→`/sales/dashboard`, Customer→`/customer/dashboard`
- ✅ CSRF protection bật
- ✅ Remember-me (7 ngày, lưu DB)
- ✅ Session timeout 30 phút
- ✅ `@PreAuthorize` trên từng controller method
- ✅ Public routes: `/`, `/home`, `/login`, `/register`, `/error` — không cần đăng nhập

#### Frontend — Thymeleaf + Bootstrap 5.3 (32 templates)
- ✅ **Layout**: `layout/base.html` — navbar theo role (sec:authorize), flash messages, footer; admin dropdown có "Người Dùng" [Phase 3.5]
- ✅ **Auth**: `auth/login.html` — gradient background + link đăng ký + success alert khi redirect từ /register [Phase 3.5]; `auth/register.html` — standalone, form tự đăng ký Customer [Phase 3.5]
- ✅ **Home**: `home.html` — trang công khai: hero banner, brands grid, 8 xe nổi bật, dịch vụ, CTA
- ✅ **Admin**: `admin/dashboard.html` — stat cards; `admin/brands/list+form`; `admin/models/list+form`; `admin/showrooms/list+form`; `admin/employees/list+form`; `admin/users/list+form` — quản lý user (CRUD + search + phân quyền) [Phase 3.5]
- ✅ **Inventory**: `inventory/vehicles/list+form+detail` — search đa tiêu chí, upload ảnh, gallery, ngày nằm kho
- ✅ **Sales**: `sales/customers/list+form+detail`; `sales/orders/list+form+detail` — workflow đổi trạng thái, thanh toán; `sales/appointments/list+form+detail`
- ✅ **Dashboards**: `manager/dashboard.html` — stat cards + 10 đơn gần nhất; `sales/dashboard.html`; `customer/dashboard.html` — loyalty points + đơn + lịch hẹn
- ✅ **Error pages**: `error/403.html`, `error/404.html`, `error/500.html`
- ✅ `static/css/main.css`, `static/js/main.js`

#### Tính năng nâng cao (skeleton sẵn sàng)
- ✅ `AuditLogService` — async, ghi IP, username, action vào DB
- ✅ `EmailService` — async, gửi xác nhận đơn hàng, nhắc lịch hẹn
- ✅ `WebMvcConfig` — serve uploaded images từ thư mục ngoài classpath

#### Phase 3 — Tính năng nâng cao [HOÀN THÀNH — 2026-04-27]
- ✅ **Dashboard Charts (Chart.js 4.4.2)** — biểu đồ cột doanh thu 12 tháng (manager + admin dashboard), biểu đồ doughnut phân bổ xe theo trạng thái (admin dashboard); dropdown chọn năm; fetch JSON từ `/api/stats/revenue-monthly` và `/api/stats/vehicle-status`
- ✅ **Export PDF hóa đơn** — `PdfExportService` (iText 8), endpoint `GET /sales/orders/{id}/pdf`; nút PDF trên trang detail đơn hàng; bao gồm thông tin đơn, khách hàng, chi tiết xe, lịch sử thanh toán
- ✅ **Export Excel tồn kho** — `ExcelExportService` + `ReportController`, endpoint `GET /manager/reports/inventory.xlsx`; toàn bộ xe với brand, model, giá, trạng thái, showroom
- ✅ **Export Excel doanh thu** — endpoint `GET /manager/reports/revenue.xlsx?year=YYYY`; doanh thu 12 tháng + tỷ lệ phần trăm + tổng cộng; dropdown chọn năm
- ✅ **Trang báo cáo** — `GET /manager/reports` (template `manager/reports.html`); link trên navbar cho Manager + Admin
- ✅ **Trang Audit Log** — `AuditLogController` + `GET /admin/audit-logs`; filter theo username + action; phân trang 20 bản ghi/trang; link trong dropdown Admin navbar
- ✅ **API stats JSON** — `GET /api/stats/revenue-monthly?year=` và `GET /api/stats/vehicle-status` trong DashboardController (dùng `@ResponseBody`); dùng native SQL với `EXTRACT(MONTH/YEAR FROM ...)` cho PostgreSQL
- ✅ **Font tiếng Việt cho PDF** — `NotoSans-Regular.ttf` + `NotoSans-Bold.ttf` bundle vào `src/main/resources/fonts/`; `PdfExportService.loadFont()` dùng `IDENTITY_H` + `FORCE_EMBEDDED`
- ✅ **Email graceful degradation** — `EmailService` dùng `@Autowired(required=false)` + null-guard; wire vào `OrderServiceImpl` (gửi khi tạo đơn) và `ServiceAppointmentServiceImpl` (gửi khi đặt lịch); skip khi chưa cấu hình SMTP
- ✅ **Date range search** — thêm `fromDate`/`toDate` inputs trên UI (orders + appointments); Controller nhận `@DateTimeFormat(ISO.DATE)`; service convert sang sentinel LocalDateTime

#### Data Seed Thực Tế (V10) [HOÀN THÀNH — 2026-04-27]
- ✅ **26 users** — 1 admin + 3 managers + 5 sales + 3 techs + 12 customers + 2 mặc định (sales/customer)
- ✅ **12 employees** — EMP000001–EMP000012, phân bổ đủ các showroom và phòng ban
- ✅ **17 customers** — KH000001–KH000017, gồm 4 doanh nghiệp (is_corporate=true), các loại NEW/REGULAR/VIP
- ✅ **54 car models** — đủ 7 loại xe: SEDAN, SUV, HATCHBACK, PICKUP, VAN, MPV, COUPE; 10 hãng xe
- ✅ **45 vehicles** — 24 AVAILABLE, 13 SOLD, 6 RESERVED, 2 MAINTENANCE; VIN format `VN[BRAND2][YEAR4][SEQ9]`
- ✅ **21 orders** — đủ 6 trạng thái: COMPLETED(11), DELIVERED(2), CONFIRMED(2), PENDING(2), DRAFT(2), CANCELLED(2)
- ✅ **13 invoices** — PAID/PARTIAL/UNPAID; 17 payments với đủ 4 phương thức CASH/BANK_TRANSFER/INSTALLMENT/CARD
- ✅ **15 service appointments** — đủ 5 trạng thái: COMPLETED(5), IN_PROGRESS(3), CONFIRMED(3), SCHEDULED(2), CANCELLED(2)
- ✅ **5 service records** + **21 service items** — LABOR và PART

#### Phase 3.5 — Đăng Ký & Quản Lý User [HOÀN THÀNH — 2026-04-28]
- ✅ **Trang đăng ký Customer** (`/register`) — standalone form (không dùng layout), gradient background giống login; fields: username, email, fullName, phone (bắt buộc), password, confirmPassword
- ✅ **`UserRegistrationRequest.java`** — Bean Validation đầy đủ: `@Pattern(^[a-zA-Z0-9_]+$)`, `@Pattern(phải có 1 HOA + 1 số)`, phone `^[0-9]{10,11}$`
- ✅ **`UserRegistrationServiceImpl`** — `@Transactional`: validate cross-field (confirmPassword), uniqueness (username/email) → tạo User (ROLE_CUSTOMER, enabled=true) → tạo Customer (generateCustomerCode()) → gửi welcome email async; rollback nếu Customer save fail
- ✅ **Redirect sau đăng ký** — `redirect:/login?registered=true`; `AuthController` nhận param, `login.html` hiện alert xanh success
- ✅ **`EmailService.sendWelcomeEmail()`** — graceful null-guard pattern (skip nếu chưa cấu hình SMTP)
- ✅ **Admin User Management** (`/admin/users`) — list + search (keyword + role filter) + create + edit; `@PreAuthorize("hasRole('ADMIN')")` class-level
- ✅ **`UserCreateRequest.java`** — phone optional (`^$|^[0-9]{10,11}$`); password required on create, optional on edit; `Set<Long> roleIds`; enabled/locked Boolean
- ✅ **`UserController`** — full CRUD: tạo account bất kỳ role, edit (username readonly), reset password (để trống = giữ nguyên), lock/unlock/disable account
- ✅ **`admin/users/list.html`** — color-coded role badges (ADMIN=đỏ/MANAGER=vàng/SALES=xanh info/CUSTOMER=xám); status badges; pagination có persist keyword+roleName
- ✅ **`admin/users/form.html`** — dynamic title (create vs edit); checkbox binding với `name="roleIds"` + `th:value`; hidden+checkbox pattern cho Boolean enabled/locked
- ✅ **`UserRepository.search()`** — DISTINCT + CAST(:keyword AS string) cho LIKE + empty-string sentinel cho roleName + `countQuery` riêng (tránh pageable count sai)
- ✅ **`base.html` admin dropdown** — thêm "Người Dùng" → `/admin/users` đầu danh sách

#### Bug Fixes — LazyInitializationException [HOÀN THÀNH — 2026-04-27]
Khi có dữ liệu thật (employees có showroom/department, orders có employee), các trang list/detail bị 500 do lazy loading sau khi session đóng (`open-in-view=false`). Fixed toàn bộ:
- ✅ `OrderRepository.search()` — thêm `LEFT JOIN FETCH o.employee e LEFT JOIN FETCH e.user`
- ✅ `ServiceAppointmentRepository.search()` — thêm `LEFT JOIN FETCH a.employee e LEFT JOIN FETCH e.user LEFT JOIN FETCH a.showroom`
- ✅ `EmployeeRepository.search()` — thêm `LEFT JOIN FETCH e.showroom LEFT JOIN FETCH e.department`
- ✅ `VehicleRepository.search()` — thêm `LEFT JOIN FETCH v.showroom`
- ✅ `OrderRepository.findWithDetailsById()` — thêm `LEFT JOIN FETCH e.user`
- ✅ `OrderController.detail()` — đổi từ `findById()` sang `findWithDetailsById()`
- ✅ `OrderService.findWithDetailsById()` — dùng `Hibernate.initialize()` để load `invoice` + `invoice.payments` trong cùng transaction (tránh MultipleBagFetchException)

---

### ⏳ CHƯA HOÀN THÀNH (Phase 4)

#### Phase 4 — Testing & Docs
- ❌ Unit tests (JUnit 5 + Mockito) — target ≥ 60% coverage (JaCoCo đã config trong pom.xml)
- ❌ Integration tests (TestContainers — dependency đã có)
- ❌ `README.md` — hướng dẫn cài đặt đầy đủ
- ❌ ERD diagram
- ❌ Video demo 15-20 phút (xem gợi ý luồng demo đã cập nhật ở mục "Bước tiếp theo")
- ❌ PowerPoint slides
- ⏳ SMTP email — cấu hình `spring.mail.*` trong `application-dev.properties` (code đã sẵn sàng, dùng cho cả đơn hàng, lịch hẹn, và welcome email đăng ký)

---

## 📅 LỘ TRÌNH THỰC HIỆN (12 TUẦN)

### Phase 1: Setup & Core (Tuần 1-3) — ✅ HOÀN THÀNH
- ✅ Setup môi trường (Docker, Maven, Java 21)
- ✅ Tạo 19 bảng database với Flyway migrations + seed data
- ✅ Implement Authentication & Authorization (Spring Security)
- ✅ Base CRUD backend: Vehicle, Brand, CarModel, Customer, Order, Employee
- ✅ Ứng dụng khởi động thành công tại `http://localhost:8080`

### Phase 2: Main Features (Tuần 4-6) — ✅ HOÀN THÀNH
- ✅ Toàn bộ Thymeleaf templates (29 files)
- ✅ CRUD đầy đủ: CarModel, Employee, Showroom, ServiceAppointment
- ✅ Trang chi tiết xe với upload ảnh + gallery
- ✅ Trang đặt lịch dịch vụ (ServiceAppointment) — list + form + detail + status update
- ✅ Dashboard riêng cho Manager, Sales, Customer với data thật
- ✅ Form tạo đơn hàng end-to-end (chọn xe + khách + thanh toán)
- ✅ Trang công khai `/home` với xe nổi bật và thông tin dịch vụ
- ✅ Fix lỗi JPQL null-param bytea (Hibernate 6 + PostgreSQL)
- ✅ Fix lỗi CarType enum case mismatch trong seed data

### Phase 3: Advanced Features (Tuần 7-9) — ✅ HOÀN THÀNH (2026-04-27)
- ✅ Chart.js 4.4.2 — biểu đồ cột doanh thu 12 tháng (manager + admin), doughnut phân bổ xe (admin)
- ✅ Export PDF hóa đơn (iText 8) — `PdfExportService`, `GET /sales/orders/{id}/pdf`
- ✅ Export Excel báo cáo tồn kho (Apache POI) — `GET /manager/reports/inventory.xlsx`
- ✅ Export Excel báo cáo doanh thu — `GET /manager/reports/revenue.xlsx?year=YYYY`
- ✅ Trang báo cáo tổng hợp — `GET /manager/reports` (Manager + Admin)
- ✅ Trang Audit Log — `GET /admin/audit-logs` với filter username/action
- ✅ Font tiếng Việt NotoSans cho PDF — IDENTITY_H + FORCE_EMBEDDED
- ✅ Email graceful skip — `@Autowired(required=false)` + null-guard
- ✅ Date range search — orders + appointments UI + backend
- ✅ Dữ liệu Việt Nam thực tế (V10) — 45 xe, 21 đơn, 15 lịch hẹn, đủ trạng thái
- ✅ Fix LazyInitializationException — LEFT JOIN FETCH trên 4 repositories, Hibernate.initialize cho invoice.payments

### Phase 3.5: User Account Management (Ngoài lộ trình gốc) — ✅ HOÀN THÀNH (2026-04-28)
- ✅ Trang tự đăng ký Customer — `/register` (public, standalone), validation đầy đủ, tạo User + Customer cùng transaction
- ✅ `UserRegistrationService` + `UserRegistrationServiceImpl` — cross-field validate, unique check, rollback an toàn
- ✅ Welcome email async khi đăng ký thành công (graceful skip nếu chưa cấu hình SMTP)
- ✅ Admin User Management — `/admin/users` CRUD đầy đủ, tìm kiếm, phân quyền, đổi mật khẩu, lock/unlock
- ✅ `UserService` + `UserServiceImpl` + `UserController` + 2 templates (list + form)
- ✅ `UserRepository.search()` — DISTINCT + countQuery riêng để paginate đúng với JOIN roles

### Phase 4: Testing & Documentation (Tuần 10-12) — ⏳ CHƯA BẮT ĐẦU
- [ ] Unit Tests ≥ 60% coverage (JaCoCo)
- [ ] Integration Tests với TestContainers
- [ ] UI/UX polish — responsive, loading states
- [ ] README.md + ERD + User Manual
- [ ] Video demo 15-20 phút

---

## 🔧 BƯỚC TIẾP THEO (Phase 4 — ưu tiên cao nhất)

**1. Unit Tests (JUnit 5 + Mockito)**
- Test target: service layer (`OrderServiceImpl`, `VehicleServiceImpl`, `CustomerServiceImpl`, `PdfExportService`, `ExcelExportService`)
- JaCoCo đã config trong `pom.xml` — chạy `mvn test` để xem coverage report tại `target/site/jacoco/index.html`
- Lưu ý: JaCoCo check sẽ FAIL nếu coverage < 60%; có thể tạm thời bỏ `<minimum>0.60</minimum>` khi đang viết test

**2. Integration Tests (TestContainers)**
- Dependency `testcontainers:junit-jupiter` + `testcontainers:postgresql` đã có trong `pom.xml`
- Test flow end-to-end: tạo đơn hàng → thanh toán → xuất PDF

**3. Cấu hình SMTP Email (nếu cần demo)**
- Thêm vào `application-dev.properties` (không commit):
  ```properties
  spring.mail.host=smtp.gmail.com
  spring.mail.port=587
  spring.mail.username=your@gmail.com
  spring.mail.password=app-password
  spring.mail.properties.mail.smtp.starttls.enable=true
  ```
- Code `EmailService` đã sẵn sàng — chỉ cần cấu hình là hoạt động ngay

**4. README.md + ERD + Tài liệu**
- README.md: hướng dẫn cài đặt, yêu cầu môi trường, accounts mặc định
- ERD: dùng dbdiagram.io hoặc pgAdmin → export diagram 19 bảng

**5. Video demo (15-20 phút)**
- Luồng demo gợi ý: Trang chủ công khai → Đăng ký Customer (/register) → Đăng nhập Customer → Customer dashboard → Admin login → Dashboard charts → Quản lý User (/admin/users: tạo Sales mới) → Thêm xe → Sales login → Tạo đơn → Thanh toán → Xuất PDF → Manager reports Excel → Audit Log

---

## 🗄️ THIẾT KẾ DATABASE (19 BẢNG — ĐÃ TRIỂN KHAI)

| # | Bảng | Mô tả | Migration |
|---|------|--------|-----------|
| 1 | users | Người dùng hệ thống | V1 |
| 2 | roles | Vai trò (Admin, Manager, Sales, Customer) | V1 |
| 3 | user_roles | Phân quyền many-to-many | V1 |
| 4 | departments | Phòng ban | V1 |
| 5 | showrooms | Chi nhánh/Showroom | V1 |
| 6 | employees | Nhân viên | V2 |
| 7 | brands | Hãng xe | V3 |
| 8 | car_models | Dòng xe | V3 |
| 9 | vehicles | Xe cá thể (VIN) | V3 |
| 10 | vehicle_images | Hình ảnh xe | V3 |
| 11 | customers | Khách hàng | V4 |
| 12 | orders | Đơn hàng | V4 |
| 13 | order_items | Chi tiết đơn hàng | V4 |
| 14 | invoices | Hóa đơn (generated column: remaining) | V4 |
| 15 | payments | Thanh toán | V4 |
| 16 | service_appointments | Lịch hẹn dịch vụ | V5 |
| 17 | service_records | Lịch sử bảo dưỡng | V5 |
| 18 | service_items | Chi tiết dịch vụ (LABOR/PART) | V5 |
| 19 | audit_logs | Nhật ký hệ thống | V6 |

---

## 📁 CẤU TRÚC DỰ ÁN THỰC TẾ (sau Phase 3.5)

```
CAR MANAGEMENT SYSTEM/
│
├── src/main/java/com/carmanagement/
│   ├── CarManagementApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java           ← permit /home, /
│   │   └── WebMvcConfig.java
│   ├── controller/
│   │   ├── AuthController.java           ← redirect / → /home
│   │   ├── HomeController.java           ← /home (public, 8 xe nổi bật)
│   │   ├── DashboardController.java      ← admin/manager/sales/customer dashboard
│   │   ├── BrandController.java          ← /admin/brands (CRUD)
│   │   ├── CarModelController.java       ← /admin/models (CRUD) [Phase 2]
│   │   ├── ShowroomController.java       ← /admin/showrooms (CRUD) [Phase 2]
│   │   ├── EmployeeController.java       ← /admin/employees (CRUD) [Phase 2]
│   │   ├── VehicleController.java        ← /inventory/vehicles
│   │   ├── CustomerController.java       ← /sales/customers
│   │   ├── OrderController.java          ← /sales/orders + GET /{id}/pdf [Phase 3]
│   │   ├── ServiceAppointmentController.java ← /sales/appointments [Phase 2]
│   │   ├── ReportController.java         ← /manager/reports + xlsx downloads [Phase 3]
│   │   ├── AuditLogController.java       ← /admin/audit-logs [Phase 3]
│   │   ├── RegistrationController.java   ← GET/POST /register (public) [Phase 3.5]
│   │   └── UserController.java           ← /admin/users CRUD [Phase 3.5]
│   ├── service/
│   │   ├── BrandService.java + impl/
│   │   ├── VehicleService.java + impl/
│   │   ├── CustomerService.java + impl/
│   │   ├── OrderService.java + impl/     ← sentinel date cho null from/to
│   │   ├── CarModelService.java + impl/
│   │   ├── ShowroomService.java + impl/
│   │   ├── EmployeeService.java + impl/
│   │   ├── ServiceAppointmentService.java + impl/ ← [Phase 2]
│   │   ├── AuditLogService.java
│   │   ├── EmailService.java             ← thêm sendWelcomeEmail() [Phase 3.5]
│   │   ├── PdfExportService.java         ← iText 8, exportInvoice(orderId) [Phase 3]
│   │   ├── ExcelExportService.java       ← Apache POI, inventory + revenue [Phase 3]
│   │   ├── UserRegistrationService.java + impl/ ← [Phase 3.5]
│   │   └── UserService.java + impl/      ← [Phase 3.5]
│   ├── repository/                       ← 13 repos; UserRepository thêm search() DISTINCT+countQuery [Phase 3.5]
│   ├── entity/                           ← 14+ @Entity classes
│   ├── dto/request/                      ← 9 DTOs (+ UserRegistrationRequest, UserCreateRequest [Phase 3.5])
│   ├── dto/response/                     ← DashboardStatsResponse
│   ├── enums/                            ← 9 enums
│   ├── exception/
│   ├── security/
│   └── util/
│
├── src/main/resources/
│   ├── application.properties            ← DB port: 5434
│   ├── application-dev.properties        ← gitignored
│   ├── db/migration/
│   │   ├── V1–V7__*.sql                 ← schema + seed data
│   │   ├── V8__fix_car_type_case.sql    ← UPPER(car_type) [Phase 2 bugfix]
│   │   ├── V9__add_sales_customer_users.sql ← sales/Sales@123 + customer/Customer@123
│   │   └── V10__comprehensive_vietnamese_data.sql ← 45 xe, 21 đơn, 17 KH, 12 NV ...
│   ├── fonts/                            ← NotoSans-Regular.ttf + NotoSans-Bold.ttf [Phase 3]
│   ├── static/css/main.css
│   ├── static/js/main.js
│   └── templates/                        ← 32 templates
│       ├── layout/base.html              ← admin dropdown: Người Dùng link [Phase 3.5]
│       ├── home.html                     ← trang công khai [Phase 2]
│       ├── auth/login.html               ← success alert + link đăng ký [Phase 3.5]
│       ├── auth/register.html            ← tự đăng ký Customer (standalone) [Phase 3.5]
│       ├── admin/
│       │   ├── dashboard.html
│       │   ├── brands/list.html + form.html
│       │   ├── models/list.html + form.html    [Phase 2]
│       │   ├── showrooms/list.html + form.html [Phase 2]
│       │   ├── employees/list.html + form.html [Phase 2]
│       │   └── users/list.html + form.html     [Phase 3.5]
│       ├── inventory/vehicles/
│       │   ├── list.html
│       │   ├── form.html                 [Phase 2]
│       │   └── detail.html               [Phase 2]
│       ├── sales/
│       │   ├── customers/list+form+detail.html [Phase 2]
│       │   ├── orders/list+form+detail.html    [Phase 2]
│       │   └── appointments/list+form+detail.html [Phase 2]
│       ├── manager/dashboard.html        ← biểu đồ doanh thu 12 tháng [Phase 3]
│       ├── manager/reports.html          ← trang báo cáo + download Excel [Phase 3]
│       ├── sales/dashboard.html
│       ├── customer/dashboard.html
│       ├── admin/audit-logs/list.html    ← nhật ký hệ thống có filter [Phase 3]
│       └── error/{403,404,500}.html
│
├── docker-compose.yml
├── pom.xml
└── .gitignore
```

---

## 🛠️ CÔNG NGHỆ ĐÃ CHỐT (phiên bản thực tế)

| Layer | Công nghệ | Phiên bản |
|---|---|---|
| Language | Java | **21** (chạy bằng JDK 24 — xem lưu ý bên dưới) |
| Framework | Spring Boot | **3.2.5** |
| Security | Spring Security | 6.x (theo Boot) |
| ORM | Spring Data JPA / Hibernate | 6.x (theo Boot) |
| Template | Thymeleaf + extras-springsecurity6 | theo Boot |
| Database | PostgreSQL | **15.17** (Docker) |
| Migration | Flyway | **9.22.3** (theo Boot 3.2.x) |
| Build | Maven | 3.9.14 |
| Lombok | Project Lombok | **1.18.38** |
| MapStruct | MapStruct | **1.6.3** |
| PDF | iText | 8.0.3 |
| Excel | Apache POI | 5.2.5 |
| CSS | Bootstrap | 5.3.2 (CDN) |
| JS | jQuery | 3.7.1 (CDN) |
| Charts | Chart.js | **4.4.2** (CDN) |
| Icons | Font Awesome | 6.5.1 (CDN) |
| Coverage | JaCoCo | 0.8.11 |

---

## 🔑 QUYẾT ĐỊNH QUAN TRỌNG & LÝ DO

### 1. Java 21 thay vì Java 17
**Lý do**: Máy dev cài JDK 25 qua Homebrew. Đặt `<java.version>21</java.version>`, compile bằng JDK 24 (`JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home`). Spring Boot 3.2.x hỗ trợ tốt Java 21.

### 2. Lombok 1.18.38 (nâng từ 1.18.36)
**Lý do**: Lombok 1.18.30 (Boot default) gây `TypeTag::UNKNOWN` với JDK 21+. Lombok 1.18.36 vẫn bị lỗi với JDK 25 — phải dùng 1.18.38 + JDK 24 (không phải JDK 25).

### 3. PostgreSQL chạy trên port 5434
**Lý do**: Port 5432 bị PostgreSQL@14 Homebrew chiếm, port 5433 bị dự án khác dùng.

### 4. Bỏ `flyway-database-postgresql` dependency
**Lý do**: Module này chỉ có từ Flyway 10.x (Boot 3.3+). Boot 3.2.x dùng Flyway 9.x, chỉ cần `flyway-core`.

### 5. Thymeleaf layout pattern — PHẢI dùng đúng cú pháp
**Lý do**: `base.html` dùng `th:fragment="layout(title, main, scripts)"`. Bên trong base, phải dùng `${main}` (không phải `~{:: main}`). Bên ngoài (trang con) dùng `th:replace="~{layout/base :: layout(~{::title}, ~{::main}, ~{::scripts})}"`.

### 6. Sentinel date thay vì null cho date range query
**Lý do**: Hibernate 6 + PostgreSQL không thể suy ra kiểu của parameter null khi dùng `(:from IS NULL OR col >= :from)` — gây `could not determine data type` (binds as bytea). Fix: bỏ IS NULL check, service truyền `LocalDateTime.of(2000,1,1,0,0)` và `LocalDateTime.of(2099,12,31,23,59)` thay vì null.

### 7. `CAST(:keyword AS string)` cho LIKE queries
**Lý do**: Cùng vấn đề bytea — khi keyword null, Hibernate bind bytea. `CAST(:keyword AS string)` trong JPQL tạo `cast(? as text)` trong SQL, PostgreSQL xác định được kiểu.

### 8. `FetchType.LAZY` toàn bộ + `spring.jpa.open-in-view=false`
**Lý do**: Tránh N+1 và lazy loading ngoài transaction. Dùng `JOIN FETCH` trong JPQL khi cần data liên quan.

### 9. `@Async` cho AuditLog và Email
**Lý do**: Không block response; `@Transactional(REQUIRES_NEW)` để audit có transaction độc lập.

### 10. Native SQL cho `findMonthlyRevenue` (không dùng JPQL)
**Lý do**: Hàm `EXTRACT(MONTH/YEAR FROM ...)` trong JPQL cần `FUNCTION('date_part', ...)` — cú pháp dài và khó đọc. Native SQL với `EXTRACT(...FROM order_date)::int` trên PostgreSQL rõ ràng hơn và đã được test. Trả về `List<Object[]>`, service tự map vào `BigDecimal[]` 12 phần tử.

### 11. `@ResponseBody` trên method trong `@Controller` cho JSON API
**Lý do**: Thay vì tạo `@RestController` riêng, thêm `@ResponseBody` vào các method `/api/stats/**` trong `DashboardController`. Giữ codebase gọn hơn, tránh thêm class mới chỉ để trả JSON. Bảo mật qua `@PreAuthorize` trên method.

### 12. iText 8 + NotoSans — hỗ trợ tiếng Việt đầy đủ
**Lý do**: Standard PDF fonts (Helvetica, Times, Courier) không có Unicode glyph cho tiếng Việt. Đã bundle `NotoSans-Regular.ttf` + `NotoSans-Bold.ttf` vào `src/main/resources/fonts/`, dùng `IDENTITY_H` encoding + `FORCE_EMBEDDED` strategy trong `PdfExportService.loadFont()`. File font ~296KB mỗi file, load bằng `getResourceAsStream()`.

### 13. `findWithDetailsById` — chỉ fetch 1 collection + Hibernate.initialize cho phần còn lại
**Lý do**: Hibernate 6 ném `MultipleBagFetchException` nếu JOIN FETCH nhiều hơn 1 `List` (Bag) cùng lúc. Query chỉ JOIN FETCH `o.orderItems` (1 collection). `invoice` và `invoice.payments` được load bằng `Hibernate.initialize()` trong `OrderServiceImpl.findWithDetailsById()` vì method là `@Transactional(readOnly = true)` — session vẫn mở khi gọi initialize.

### 14. `CarModel.name` — không phải `modelName`
**Lý do**: Column trong DB là `name` (unique constraint trên `brand_id + name + year`). Khi viết PDF/Excel export, cần dùng `carModel.getName()` chứ không phải `getModelName()`. Lỗi này gây compile error, phát hiện qua IDE diagnostic ngay khi save.

### 15. `LEFT JOIN FETCH` trên to-one + `countQuery` riêng cho Page queries
**Lý do**: Khi có dữ liệu thật, các trang list bị `LazyInitializationException` do template truy cập `employee.user.fullName`, `showroom.name` sau khi session đóng (`open-in-view=false`). Fix bằng `LEFT JOIN FETCH` cho to-one associations (không gây MultipleBagFetchException). Phải cung cấp `countQuery` riêng trong `@Query(value=..., countQuery=...)` để Spring Data tạo đúng câu đếm cho phân trang — nếu không, Spring Data tự sinh countQuery có thể giữ lại JOIN FETCH gây lỗi hoặc trả số sai.

### 16. V10 dữ liệu Việt Nam — dùng `INSERT...SELECT FROM (VALUES...)` cho service_items
**Lý do**: `service_items` cần ID của `service_records` vừa insert. Không thể dùng giá trị literal vì ID là BIGSERIAL. Giải pháp: `INSERT INTO service_items (...) SELECT (SELECT id FROM service_records WHERE vehicle_id=... AND service_date='...'), v.col... FROM (VALUES (...)) AS v(col...)` — tìm service_record ID qua business key (vehicle_id + service_date) thay vì ID thực tế.

### 17. `flyway:repair` khi sửa migration đã apply
**Lý do**: Nếu V10 đã apply (checksum ghi vào `flyway_schema_history`) mà sau đó file .sql bị sửa, Flyway sẽ báo checksum mismatch khi khởi động. Fix: `mvn flyway:repair -Dflyway.url=... -Dflyway.user=... -Dflyway.password=...` — cập nhật lại checksum trong DB theo file hiện tại. Dữ liệu trong DB KHÔNG bị xóa, chỉ metadata được sửa.

### 18. `OrderController.detail()` — phải dùng `findWithDetailsById`, không dùng `findById`
**Lý do**: `findById` dùng JPA standard (không fetch gì), template detail cần customer, employee.user, showroom, orderItems, invoice.payments — tất cả đều lazy. `findWithDetailsById` JOIN FETCH đủ associations + dùng Hibernate.initialize cho invoice.payments. Nếu nhầm sang `findById` sẽ bị 500 LazyInitializationException trên trang detail.

### 19. `register.html` là standalone (không dùng layout/base fragment)
**Lý do**: Trang đăng ký là public pre-auth page — không cần navbar, footer, hay sec:authorize. Dùng layout/base sẽ load thêm JS/CSS không cần thiết và có thể gây lỗi nếu Spring Security chưa khởi tạo context. Giữ consistent với `login.html` cùng design pattern: gradient background `linear-gradient(135deg, #1a1a2e, #16213e, #0f3460)`, card center.

### 20. `UserRepository.search()` — DISTINCT + empty string sentinel + countQuery riêng
**Lý do**: JOIN với `u.roles` (ManyToMany) tạo duplicate row khi user có nhiều role → phải dùng `SELECT DISTINCT u`. Tuy nhiên `SELECT DISTINCT` làm Spring Data tự sinh countQuery sai (giữ lại DISTINCT không cần thiết) → phải cung cấp `countQuery` riêng không có DISTINCT. Dùng `''` (empty string) thay vì `null` cho roleName filter để tránh bytea binding error của Hibernate 6 + PostgreSQL (cùng pattern với keyword).

### 21. `UserRegistrationServiceImpl` — validate trước khi ghi DB, không throw exception
**Lý do**: Nếu validation fail (confirmPassword không khớp, username đã tồn tại, email đã dùng), service gọi `bindingResult.rejectValue(...)` rồi return sớm — không throw exception. Controller kiểm tra `bindingResult.hasErrors()` sau khi service return để quyết định re-render form hay redirect. Cách này giữ lỗi gắn với field cụ thể (inline error) thay vì hiện global error message.

### 22. `UserCreateRequest.phone` — optional nhưng Customer registration phone bắt buộc
**Lý do**: Admin tạo user không nhất thiết phải có phone (tạo tài khoản Sales/Manager không cần SĐT). Customer tự đăng ký bắt buộc phone vì bảng `customers.phone` có `NOT NULL` constraint — nếu thiếu sẽ bị DataIntegrityViolationException. Hai DTO tách riêng (`UserCreateRequest` vs `UserRegistrationRequest`) để handle hai case khác nhau.

---

## 🚀 HƯỚNG DẪN CHẠY DỰ ÁN

### Yêu cầu
- JDK 24 (không phải JDK 25 — Lombok 1.18.38 chưa hỗ trợ JDK 25)
- Maven 3.9+
- Docker Desktop

### Khởi động
```bash
# 1. Khởi động PostgreSQL
cd "CAR MANAGEMENT SYSTEM"
docker-compose up -d

# 2. Chạy ứng dụng (PHẢI dùng JDK 24, không phải JDK 25)
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home \
  mvn spring-boot:run

# 3. Mở trình duyệt
open http://localhost:8080
```

### Tài khoản mặc định
| Username | Password | Role |
|---|---|---|
| `admin` | `Admin@123` | ADMIN |
| `manager1` | `Manager@123` | MANAGER |
| `sales` | `Sales@123` | SALES |
| `sales1` | `Sales@123` | SALES |
| `customer` | `Customer@123` | CUSTOMER |
| `customer1`–`customer12` | `Customer@123` | CUSTOMER |

> **Tạo tài khoản Customer mới**: tự đăng ký tại `/register` (không cần đăng nhập)
> **Tạo tài khoản Sales/Manager/Admin**: Admin đăng nhập → `/admin/users/new`

### Ports
| Service | Port | URL |
|---|---|---|
| Spring Boot App | 8080 | http://localhost:8080 |
| PostgreSQL | **5434** | localhost:5434/car_management |
| pgAdmin | 8888 | http://localhost:8888 |

### pgAdmin login
- Email: `admin@carmanagement.com` / Password: `admin123`
- DB Server: host=`postgres`, port=`5432`, user=`caruser`, pass=`carpassword123`

---

## ✅ YÊU CẦU HOÀN THÀNH

### Code Quality
- [x] Code tuân thủ Java naming conventions
- [x] Service pattern: interface + impl tách riêng
- [x] Global exception handler, DTO pattern, Bean Validation
- [ ] Comments đầy đủ cho business logic phức tạp

### Testing
- [ ] Unit Test coverage >= 60% (JaCoCo)
- [ ] Integration Tests pass
- [ ] Manual testing checklist

### Documentation
- [ ] README.md
- [ ] Database ERD
- [ ] User Manual

### Presentation
- [ ] Video demo 15-20 phút
- [ ] PowerPoint slides

---

## 💡 LƯU Ý QUAN TRỌNG

### Security
- ❌ KHÔNG commit `application-dev.properties` vào Git
- ✅ BCrypt strength 12, CSRF bật, Session timeout 30 phút
- ✅ Remember-me lưu DB (`persistent_logins` — Flyway tạo tự động)

### Performance
- ✅ Pagination 15 records/page, FetchType.LAZY toàn bộ, HikariCP max 10 conn
- ✅ `spring.jpa.open-in-view=false`

---

## 🐛 VẤN ĐỀ ĐÃ GẶP & CÁCH XỬ LÝ

| Vấn đề | Nguyên nhân | Giải pháp |
|---|---|---|
| Port 5432 bị chiếm | PostgreSQL@14 Homebrew đang chạy | Đổi Docker map sang port **5434** |
| `flyway-database-postgresql` missing version | Module chỉ có từ Flyway 10 (Boot 3.3+) | Xóa dependency, chỉ giữ `flyway-core` |
| `TypeTag::UNKNOWN` compile error | Lombok 1.18.30/1.18.36 không tương thích JDK 21+ | Nâng Lombok→**1.18.38**, dùng JDK 24 (không phải JDK 25) |
| Login admin không được | BCrypt hash trong seed data không khớp "Admin@123" | Tạo hash mới bằng `htpasswd -nbBC 10 "" "Admin@123"`, update DB + V7 |
| Flyway checksum mismatch | Sửa V7 sau khi đã apply | `UPDATE flyway_schema_history SET checksum = <new> WHERE version = '7'` |
| HTTP 500: `lower(bytea) does not exist` | Hibernate 6 + PG bind null param as bytea cho LIKE | Thêm `CAST(:keyword AS string)` trong mọi LIKE clause |
| HTTP 500: `could not determine data type of parameter $6` | `:from`/`:to` LocalDateTime null → Hibernate bind bytea | Xóa IS NULL check date khỏi JPQL; service dùng sentinel date 2000/2099 |
| HTTP 500: Thymeleaf StackOverflowError | `base.html` thiếu `th:fragment="layout(title, main, scripts)"` hoặc dùng `~{:: main}` thay vì `${main}` | Thêm fragment vào `<html>` tag; đổi sang `${main}`, `${title}`, `${scripts}` |
| HTTP 500: `No enum constant CarType.Sedan` | Seed data dùng `'Sedan'` nhưng enum Java cần `'SEDAN'` | Migration **V8**: `UPDATE car_models SET car_type = UPPER(car_type)` |
| `success or error` SpEL null error | `${success or error}` khi cả hai null không thể convert sang boolean | Đổi thành `${success != null or error != null}` |
| Compile error: `getModelName()` undefined | `CarModel` entity có field `name`, không phải `modelName` | Dùng `carModel.getName()` trong `PdfExportService` + `VehicleRepository` query |
| `VehicleRepository` compile error: `List` not found | Thêm method `findAllWithDetails()` trả `List<Vehicle>` nhưng thiếu import | Thêm `import java.util.List;` vào VehicleRepository |
| Chart.js không render trên dashboard | Gọi `fetch()` API trước khi Chart.js CDN load xong | Đặt `<script>` Chart.js CDN trong `<head>` (trước content), không phải cuối body |
| HTTP 500 `/sales/orders`, `/sales/appointments`, `/admin/employees`, `/inventory/vehicles` | Dữ liệu thật có employee.showroom/user nhưng list query thiếu JOIN FETCH | Thêm `LEFT JOIN FETCH` + `countQuery` riêng vào OrderRepository, ServiceAppointmentRepository, EmployeeRepository, VehicleRepository |
| HTTP 500 `/sales/orders/{id}` (detail) | Controller gọi `findById` thay vì `findWithDetailsById`; invoice.payments lazy ngoài session | Thêm `findWithDetailsById()` vào OrderService dùng `Hibernate.initialize(invoice.payments)`, đổi controller gọi method mới |
| Flyway checksum mismatch (V10) | File V10 bị sửa sau khi đã apply vào DB | `mvn flyway:repair -Dflyway.url=jdbc:postgresql://localhost:5434/car_management -Dflyway.user=caruser -Dflyway.password=carpassword123` |
| Port 8080 còn bị chiếm sau kill | Spring DevTools restart process mới, process cũ chưa chết hẳn | `lsof -ti:8080 | xargs kill -9` rồi đợi 5-10 giây trước khi restart |
