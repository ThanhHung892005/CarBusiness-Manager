# 🚗 Car Business Manager

Hệ thống quản lý doanh nghiệp ô tô toàn diện — Spring Boot 3.2.5 + PostgreSQL 15 + Thymeleaf.

---

## ✅ Tính năng

| Module | Mô tả |
|---|---|
| **Kho xe** | Quản lý xe theo VIN, upload ảnh, tìm kiếm đa tiêu chí |
| **Bán hàng** | Tạo đơn hàng, thanh toán nhiều đợt, xuất PDF hóa đơn |
| **Khách hàng** | CRUD, loyalty points, tự đăng ký tài khoản |
| **Lịch hẹn dịch vụ** | Đặt lịch, theo dõi trạng thái, lịch sử bảo dưỡng |
| **Báo cáo** | Export Excel tồn kho + doanh thu theo tháng |
| **Dashboard** | Biểu đồ Chart.js doanh thu 12 tháng, phân bổ xe |
| **Phân quyền** | 4 vai trò: Admin / Manager / Sales / Customer |
| **Audit Log** | Nhật ký mọi thao tác, filter theo user và hành động |
| **Quản lý User** | Admin tạo/sửa/khóa tài khoản, reset mật khẩu |

---

## 🛠️ Yêu cầu môi trường

| Công cụ | Phiên bản |
|---|---|
| JDK | **24** (không dùng JDK 25 — Lombok 1.18.38 chưa hỗ trợ) |
| Maven | 3.9+ |
| Docker Desktop | Bất kỳ phiên bản ổn định |

---

## 🚀 Hướng dẫn cài đặt & chạy

### 1. Clone dự án

```bash
git clone <repo-url>
cd "CarBusiness Manager"
```

### 2. Khởi động PostgreSQL + pgAdmin

```bash
docker-compose up -d
```

Chờ khoảng 10-15 giây để PostgreSQL sẵn sàng.

### 3. Chạy ứng dụng

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home \
  mvn spring-boot:run
```

> **Windows:** Set `JAVA_HOME` trong System Environment Variables trỏ đến JDK 24, rồi chạy `mvn spring-boot:run`

### 4. Mở trình duyệt

```
http://localhost:9090
```

---

## 🔌 Ports

| Service | Port | URL |
|---|---|---|
| Spring Boot App | **9090** | http://localhost:9090 |
| PostgreSQL | **5434** | `localhost:5434/car_management` |
| pgAdmin | **8888** | http://localhost:8888 |

### pgAdmin login
- **Email:** `admin@carmanagement.com`
- **Password:** `admin123`
- **DB Server:** host = `postgres`, port = `5432`, user = `caruser`, password = `carpassword123`

---

## 👤 Tài khoản mặc định

| Username | Password | Vai trò |
|---|---|---|
| `admin` | `Admin@123` | ADMIN |
| `manager1` | `Manager@123` | MANAGER |
| `manager2` | `Manager@123` | MANAGER |
| `manager3` | `Manager@123` | MANAGER |
| `sales` | `Sales@123` | SALES |
| `sales1` — `sales4` | `Sales@123` | SALES |
| `customer` | `Customer@123` | CUSTOMER |
| `customer1` — `customer12` | `Customer@123` | CUSTOMER |

> **Tạo tài khoản Customer mới:** Đăng ký tại `/register` (không cần đăng nhập)
>
> **Tạo tài khoản Sales / Manager / Admin:** Admin đăng nhập → `/admin/users/new`

---

## 🗂️ Cấu trúc dự án

```
CarBusiness Manager/
├── src/main/java/com/carmanagement/
│   ├── config/          # SecurityConfig, WebMvcConfig
│   ├── controller/      # 15 controllers
│   ├── service/         # 11 interfaces + 11 implementations
│   ├── repository/      # 13 Spring Data JPA repositories
│   ├── entity/          # 14+ JPA entities
│   ├── dto/             # Request/Response DTOs
│   ├── enums/           # 9 enums
│   ├── exception/       # ResourceNotFoundException, BusinessException
│   ├── security/        # CustomUserDetailsService, AuthSuccessHandler
│   └── util/            # FileUtil, CodeGeneratorUtil
├── src/main/resources/
│   ├── db/migration/    # V1–V11 Flyway migrations
│   ├── fonts/           # NotoSans (hỗ trợ tiếng Việt cho PDF)
│   ├── templates/       # 32 Thymeleaf templates
│   └── static/          # CSS + JS
├── src/test/            # Unit tests + Integration tests
├── docker-compose.yml
└── pom.xml
```

---

## 🗄️ Database

19 bảng, quản lý bởi **Flyway** (tự động migrate khi khởi động):

`users` · `roles` · `user_roles` · `departments` · `showrooms` · `employees` · `brands` · `car_models` · `vehicles` · `vehicle_images` · `customers` · `orders` · `order_items` · `invoices` · `payments` · `service_appointments` · `service_records` · `service_items` · `audit_logs`

Seed data đầy đủ: 45 xe, 21 đơn hàng, 17 khách hàng, 12 nhân viên, 15 lịch hẹn.

---

## 🧪 Chạy tests

```bash
# Unit tests (không cần Docker)
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home \
  mvn test -Dtest="OrderServiceImplTest,VehicleServiceImplTest,CustomerServiceImplTest,ExcelExportServiceTest,PdfExportServiceTest"

# Integration tests (cần Docker Desktop đang chạy)
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home \
  mvn test -Dtest="OrderFlowIntegrationTest"

# Tất cả tests + JaCoCo report
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home \
  mvn test

# Xem coverage report
open target/site/jacoco/index.html
```

---

## 📧 Cấu hình Email (tuỳ chọn)

Tạo file `src/main/resources/application-dev.properties` (không commit):

```properties
# Dev overrides
spring.jpa.show-sql=true
spring.thymeleaf.cache=false

# Gmail SMTP — dùng App Password, không phải mật khẩu Gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your@gmail.com
spring.mail.password=xxxx-xxxx-xxxx-xxxx
spring.mail.properties.mail.smtp.starttls.enable=true
```

> **Lấy App Password:** Gmail → Manage Google Account → Security → 2-Step Verification → App Passwords

---

## 🔧 Công nghệ sử dụng

| Layer | Công nghệ | Phiên bản |
|---|---|---|
| Backend | Spring Boot | 3.2.5 |
| Language | Java | 21 (build bằng JDK 24) |
| ORM | Spring Data JPA / Hibernate | 6.x |
| Database | PostgreSQL | 15 |
| Migration | Flyway | 9.x |
| Template | Thymeleaf | 3.x |
| CSS | Bootstrap | 5.3.2 |
| Charts | Chart.js | 4.4.2 |
| PDF | iText | 8.0.3 |
| Excel | Apache POI | 5.2.5 |
| Security | Spring Security | 6.x |
| Testing | JUnit 5 + Mockito + TestContainers | — |
| Coverage | JaCoCo | 0.8.11 |
# CarBusiness-Manager
