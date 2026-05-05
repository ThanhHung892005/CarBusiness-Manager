package com.carmanagement.service;

import com.carmanagement.entity.Order;
import com.carmanagement.entity.ServiceAppointment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@carmanagement.com}")
    private String from;

    @Async
    public void sendOrderConfirmation(Order order) {
        String to = order.getCustomer().getEmail();
        if (to == null || to.isBlank()) return;

        send(to,
            "Xác nhận đơn hàng " + order.getOrderCode(),
            "Kính gửi " + order.getCustomer().getFullName() + ",\n\n" +
            "Đơn hàng " + order.getOrderCode() + " của bạn đã được xác nhận.\n" +
            "Tổng tiền: " + order.getTotalAmount() + " VNĐ\n\n" +
            "Trân trọng,\nCar Management System"
        );
    }

    @Async
    public void sendAppointmentReminder(ServiceAppointment appointment) {
        String to = appointment.getCustomer().getEmail();
        if (to == null || to.isBlank()) return;

        send(to,
            "Nhắc lịch hẹn dịch vụ " + appointment.getAppointmentCode(),
            "Kính gửi " + appointment.getCustomer().getFullName() + ",\n\n" +
            "Bạn có lịch hẹn dịch vụ vào " + appointment.getAppointmentDate() + ".\n" +
            "Loại dịch vụ: " + appointment.getServiceType() + "\n\n" +
            "Trân trọng,\nCar Management System"
        );
    }

    @Async
    public void sendWelcomeEmail(String to, String fullName, String username) {
        if (to == null || to.isBlank()) return;
        send(to,
            "Chào mừng bạn đến với Car Management System",
            "Kính gửi " + fullName + ",\n\n" +
            "Tài khoản của bạn đã được tạo thành công.\n" +
            "Tên đăng nhập: " + username + "\n\n" +
            "Trân trọng,\nCar Management System"
        );
    }

    private void send(String to, String subject, String body) {
        if (mailSender == null) {
            log.warn("Mail sender not configured — skipping email to {}: {}", to, subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {}: {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
