# Hệ thống quản lý đại lý bán xe mới

> Phạm vi: Đại lý bán xe mới · Quy mô 6–15 người · 6 module · 4 role chính

---

## Tổng quan

| Thông tin | Chi tiết |
|---|---|
| Loại hình | Đại lý bán xe mới |
| Quy mô nhân sự | 6–15 người |
| Số module | 6 |
| Số role | 4 |
| Tổng chức năng | 27 |

---

## Các role trong hệ thống

### 1. Giám đốc
- Xem toàn bộ dữ liệu các module
- Duyệt các giao dịch có giá trị lớn
- Xem báo cáo doanh thu, KPI tổng hợp
- Quản lý tài khoản và phân quyền người dùng

### 2. Nhân viên kinh doanh
- Tạo và quản lý leads, khách hàng
- Lập hợp đồng đặt cọc và mua xe
- Xem danh sách xe tồn kho (chỉ xem, không sửa)
- Xem lịch sử tương tác và lịch hẹn khách hàng

### 3. Kế toán
- Ghi nhận thu tiền đặt cọc, mua xe
- Xuất hóa đơn bán hàng
- Quản lý công nợ khách hàng
- Xem hợp đồng bán hàng (chỉ xem)
- Xuất bảng lương nhân viên

### 4. Thủ kho
- Nhập xe vào kho, cập nhật thông tin xe
- Quản lý trạng thái xe (có sẵn / đặt cọc / đã bán / trưng bày)
- Tra cứu xe theo số khung (VIN)

---

## Ma trận phân quyền

| Module | Giám đốc | NV Kinh doanh | Kế toán | Thủ kho |
|---|---|---|---|---|
| Khách hàng (CRM) | Xem | Full | — | — |
| Bán hàng | Xem | Full | Xem | — |
| Kho xe | Xem | Xem | — | Full |
| Tài chính | Xem | — | Full | — |
| Nhân viên | Full | — | Xem lương | — |
| Hệ thống | Full | — | — | — |

> **Full** = xem + thêm + sửa · **Xem** = chỉ đọc · **—** = không truy cập

---

## Module 1 — Quản lý khách hàng (CRM)

**Mô tả:** Quản lý toàn bộ thông tin và lịch sử tương tác với khách hàng từ lúc tiếp cận đến sau bán hàng.

| # | Chức năng | Mô tả | Ưu tiên |
|---|---|---|---|
| 1.1 | Danh sách khách hàng | Thêm, sửa, tìm kiếm hồ sơ khách hàng. Gắn tag tiềm năng / đã mua. | Cao |
| 1.2 | Lịch sử tương tác | Ghi chú cuộc gọi, lái thử, hẹn gặp theo timeline. | Cao |
| 1.3 | Nhắc lịch hẹn | Tạo lịch lái thử, giao xe. Thông báo trước ngày hẹn. | Cao |
| 1.4 | Nguồn leads | Phân loại theo kênh: Facebook, walk-in, giới thiệu... | Nên có |

---

## Module 2 — Quản lý bán hàng

**Mô tả:** Quản lý toàn bộ quy trình bán xe từ báo giá đến giao xe, theo dõi trạng thái từng đơn hàng.

| # | Chức năng | Mô tả | Ưu tiên |
|---|---|---|---|
| 2.1 | Tạo hợp đồng bán xe | Chọn xe từ kho, gắn khách hàng, điền giá bán, phụ phí. | Cao |
| 2.2 | Trạng thái đơn hàng | Tiềm năng → Đặt cọc → Ký hợp đồng → Giao xe. | Cao |
| 2.3 | Quản lý đặt cọc | Ghi nhận, hoàn cọc, theo dõi các khoản cọc đang giữ. | Cao |
| 2.4 | Báo giá xe | Tạo báo giá PDF gửi khách hàng, có thể chỉnh sửa phụ phí. | Nên có |
| 2.5 | KPI nhân viên | Doanh số, số xe bán theo từng nhân viên trong tháng. | Nên có |

---

## Module 3 — Quản lý kho xe

**Mô tả:** Quản lý toàn bộ xe trong kho từ lúc nhập về đến khi bàn giao cho khách hàng.

| # | Chức năng | Mô tả | Ưu tiên |
|---|---|---|---|
| 3.1 | Danh sách xe tồn kho | Xem toàn bộ xe, lọc theo trạng thái, màu, phiên bản. | Cao |
| 3.2 | Nhập xe vào kho | Nhập số khung (VIN), màu, phiên bản, giá nhập, ngày về kho. | Cao |
| 3.3 | Trạng thái xe | Có sẵn / Đặt cọc / Đã bán / Trưng bày / Bảo dưỡng. | Cao |
| 3.4 | Tra cứu theo VIN | Tìm nhanh xe qua số khung (VIN) hoặc biển số. | Nên có |

> **VIN (Vehicle Identification Number):** Mã số nhận dạng xe duy nhất gồm 17 ký tự, còn gọi là "số khung". Mỗi xe có một mã riêng, không trùng lặp.

---

## Module 4 — Tài chính & báo cáo

**Mô tả:** Ghi nhận các khoản thu chi liên quan đến bán xe, xuất hóa đơn và tổng hợp báo cáo doanh thu.

| # | Chức năng | Mô tả | Ưu tiên |
|---|---|---|---|
| 4.1 | Ghi nhận thu tiền | Thu tiền cọc, thu tiền mua xe, liên kết với hợp đồng. | Cao |
| 4.2 | Xuất hóa đơn | Hóa đơn bán xe PDF, hóa đơn điện tử (VAT). | Cao |
| 4.3 | Báo cáo doanh thu | Doanh thu ngày/tháng/năm, lợi nhuận gộp theo xe. | Cao |
| 4.4 | Công nợ khách hàng | Khoản còn nợ, nhắc thu nợ, trạng thái thanh toán. | Nên có |
| 4.5 | Chi phí nhập xe | Ghi chi phí vận chuyển, thuế, đăng ký xe. | Tùy chọn |

---

## Module 5 — Quản lý nhân viên

**Mô tả:** Quản lý hồ sơ, chấm công, lương và hiệu suất làm việc của toàn bộ nhân viên trong đại lý.

| # | Chức năng | Mô tả | Ưu tiên |
|---|---|---|---|
| 5.1 | Hồ sơ nhân viên | Thông tin cá nhân, chức vụ, ngày vào làm, CMND, hợp đồng lao động. | Cao |
| 5.2 | Gắn role hệ thống | Gắn role (NV KD / Kế toán / Thủ kho...) trực tiếp từ hồ sơ nhân viên. | Cao |
| 5.3 | Chấm công | Ghi nhận đi làm, nghỉ phép, đi trễ theo ngày/tháng. | Nên có |
| 5.4 | Tính lương | Lương cơ bản + hoa hồng xe bán được. Xuất bảng lương tháng. | Nên có |
| 5.5 | KPI doanh số | Số xe bán, doanh thu theo nhân viên từng tháng. So sánh với chỉ tiêu. | Nên có |
| 5.6 | Nghỉ phép | Đăng ký nghỉ phép, duyệt, theo dõi số ngày phép còn lại. | Tùy chọn |

---

## Module 6 — Hệ thống & phân quyền

**Mô tả:** Quản lý tài khoản đăng nhập, phân quyền truy cập và ghi lại nhật ký hoạt động.

| # | Chức năng | Mô tả | Ưu tiên |
|---|---|---|---|
| 6.1 | Quản lý tài khoản | Tạo tài khoản, gắn role, đặt lại mật khẩu. | Cao |
| 6.2 | Phân quyền role | Gán quyền xem/sửa/xóa cho từng module theo role. | Cao |
| 6.3 | Nhật ký hoạt động | Log ai làm gì, lúc nào — để kiểm tra khi cần. | Tùy chọn |

---

## Lộ trình triển khai đề xuất

### Giai đoạn 1 — Core (làm trước)
Mục tiêu: hệ thống chạy được thực tế ngay

- Kho xe: nhập xe, trạng thái, danh sách tồn
- Bán hàng: tạo hợp đồng, quản lý đặt cọc, trạng thái đơn hàng
- Tài chính: ghi nhận thu tiền, xuất hóa đơn
- Hệ thống: quản lý tài khoản, phân quyền
- Nhân viên: hồ sơ, gắn role

### Giai đoạn 2 — Vận hành trơn tru
Mục tiêu: nâng cao trải nghiệm hàng ngày

- CRM đầy đủ: lịch hẹn, lịch sử tương tác, nhắc lịch
- Báo cáo doanh thu
- Công nợ khách hàng
- Chấm công, tính lương, KPI nhân viên

### Giai đoạn 3 — Nâng cao
Mục tiêu: tối ưu và bổ sung tiện ích

- Báo giá PDF
- Nguồn leads
- Chi phí nhập xe
- Nghỉ phép nhân viên
- Nhật ký hoạt động hệ thống

---

*Tài liệu này được tạo cho đại lý bán xe mới quy mô nhỏ (6–15 người). Có thể mở rộng thêm module bảo hành, dịch vụ sửa chữa khi quy mô tăng.*
