package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.Booking;
import com.dinhngoctranduy.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(User user, String token) {
        String subject = "Xác thực tài khoản của bạn";
        String confirmationUrl = "http://localhost:8080/api/v1/verify?token=" + token;
        String message = "Click vào link sau để xác thực email: " + confirmationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }

    public void sendBookingConfirmationEmail(String toEmail, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = send HTML

        mailSender.send(message);
    }

    public String buildBookingConfirmationHtml(String guestName, String tourTitle, String bookingId, double price) {
        return """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; }
                        .header { background-color: #f2f2f2; padding: 10px; }
                        .content { margin-top: 20px; }
                        .footer { margin-top: 30px; font-size: 13px; color: gray; }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h2>Chào %s,</h2>
                    </div>
                    <div class="content">
                        <p>Bạn đã đặt tour thành công 🎉</p>
                        <p><strong>Tên tour:</strong> %s</p>
                        <p><strong>Mã đơn hàng:</strong> %s</p>
                        <p><strong>Tổng số tiền:</strong> %, .0f VND</p>
                        <br>
                        <p>Chúng tôi sẽ liên hệ bạn sớm để xác nhận thông tin chi tiết.</p>
                    </div>
                    <div class="footer">
                        <p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.</p>
                        <p>— Đội ngũ hỗ trợ TourVN</p>
                    </div>
                </body>
                </html>
                """.formatted(guestName, tourTitle, bookingId, price);
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email: " + e.getMessage());
        }
    }

    public void sendBookingCancelEmail(Booking booking) {
        String to = booking.getEmail();

        String subject = "Thông báo hủy tour thành công";

        String bookingTime = formatInstantToReadable(booking.getBookingDate());
        String cancelTime = formatInstantToReadable(Instant.now());

        String content = "<html><body>"
                + "<h3>Xin chào " + booking.getUserName() + ",</h3>"
                + "<p>Tour <b>" + booking.getTour().getTitle() + "</b> của bạn đã được hủy thành công.</p>"
                + "<p>Thông tin chi tiết:</p>"
                + "<ul>"
                + "<li>Ngày đặt tour: " + bookingTime + "</li>"
                + "<li>Ngày hủy: " + cancelTime + "</li>"
                + "<li>Tổng tiền: " + String.format("%,.0f", booking.getTotalPrice()) + " VND</li>"
                + "</ul>"
                + "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.</p>"
                + "</body></html>";

        sendHtmlEmail(to, subject, content);
    }

    public String formatInstantToReadable(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));  // múi giờ VN
        return formatter.format(instant);
    }


}