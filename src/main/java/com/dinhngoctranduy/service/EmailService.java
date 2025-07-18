package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.Booking;
import com.dinhngoctranduy.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(User user, String token) throws MessagingException {
        final String APP_NAME = "SmartTour";
        final String subject = "Xác thực tài khoản của bạn - " + APP_NAME;
        final String confirmationUrl = "http://localhost:8080/api/v1/verify?token=" + token;

        final String htmlContent = buildVerificationEmailHtml(user.getUsername(), confirmationUrl, APP_NAME);

        final MimeMessage message = mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String buildVerificationEmailHtml(String username, String confirmationUrl, String appName) {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Xác thực tài khoản của bạn</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            background-color: #f4f4f4;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 20px auto;
                            background: #fff;
                            padding: 30px;
                            border-radius: 8px;
                            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                        }
                        .header {
                            text-align: center;
                            padding-bottom: 20px;
                            border-bottom: 1px solid #eee;
                        }
                        .header h1 {
                            color: #0056b3;
                            margin: 0;
                        }
                        .content {
                            padding: 20px 0;
                        }
                        .button {
                            display: inline-block;
                            background-color: #007bff;
                            color: #ffffff !important;
                            padding: 12px 25px;
                            text-decoration: none;
                            border-radius: 5px;
                            font-weight: bold;
                            margin-top: 20px;
                        }
                        .footer {
                            text-align: center;
                            padding-top: 20px;
                            border-top: 1px solid #eee;
                            font-size: 0.9em;
                            color: #777;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Xác thực tài khoản</h1>
                        </div>
                        <div class="content">
                            <p>Xin chào <strong>%s</strong>,</p>
                            <p>Cảm ơn bạn đã đăng ký! Để hoàn tất quá trình tạo tài khoản, vui lòng xác thực địa chỉ email của bạn bằng cách nhấn vào nút dưới đây:</p>
                            <p style="text-align: center;">
                                <a href="%s" class="button">Xác thực Email của bạn</a>
                            </p>
                            <p>Nếu nút trên không hoạt động, bạn có thể sao chép và dán liên kết sau vào trình duyệt của mình:</p>
                            <p><a href="%s">%s</a></p>
                            <p>Trân trọng,</p>
                            <p>Đội ngũ %s</p>
                        </div>
                        <div class="footer">
                            <p>&copy; %d %s. Tất cả quyền được bảo lưu.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                username,
                confirmationUrl,
                confirmationUrl, confirmationUrl,
                appName,
                java.time.Year.now().getValue(), appName
        );
    }

    public void sendBookingConfirmationEmail(String toEmail, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    public String buildBookingConfirmationHtml(String guestName, String tourTitle, String bookingId, double price) {
        DecimalFormat formatter = new DecimalFormat("#,###");

        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Xác nhận Đặt tour - SmartTour</title>
                    <style>
                        body {
                            font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                            margin: 0;
                            padding: 0;
                            background-color: #f4f4f4;
                            color: #333;
                        }
                        .container {
                            max-width: 600px;
                            margin: 20px auto;
                            background-color: #ffffff;
                            border-radius: 8px;
                            overflow: hidden;
                            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                        }
                        .header {
                            background-color: #007bff; /* Màu xanh dương chủ đạo */
                            color: #ffffff;
                            padding: 20px;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 28px;
                        }
                        .content {
                            padding: 25px;
                            line-height: 1.6;
                            font-size: 16px;
                        }
                        .content p {
                            margin-bottom: 10px;
                        }
                        .highlight {
                            background-color: #e6f2ff; /* Màu nền nhẹ cho thông tin quan trọng */
                            border-left: 5px solid #007bff;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 4px;
                        }
                        .highlight strong {
                            color: #0056b3; /* Màu đậm hơn cho tiêu đề trong highlight */
                        }
                        .button-container {
                            text-align: center;
                            margin-top: 30px;
                            margin-bottom: 20px;
                        }
                        .button {
                            display: inline-block;
                            background-color: #28a745; /* Màu xanh lá cây cho nút */
                            color: #ffffff;
                            padding: 12px 25px;
                            border-radius: 5px;
                            text-decoration: none;
                            font-weight: bold;
                        }
                        .footer {
                            background-color: #f2f2f2;
                            color: #777;
                            padding: 20px;
                            text-align: center;
                            font-size: 13px;
                            border-top: 1px solid #eee;
                        }
                        .footer p {
                            margin: 5px 0;
                        }
                        .tour-info {
                            list-style: none;
                            padding: 0;
                            margin: 15px 0;
                        }
                        .tour-info li {
                            margin-bottom: 8px;
                        }
                        .tour-info strong {
                            display: inline-block;
                            width: 120px; /* Căn chỉnh các nhãn */
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Xác nhận Đặt Tour</h1>
                        </div>
                        <div class="content">
                            <p>Xin chào <strong>%s</strong>,</p>
                            <p>Cảm ơn bạn đã đặt tour với chúng tôi! Chúng tôi rất vui được xác nhận rằng đơn đặt tour của bạn đã thành công 🎉.</p>
                
                            <div class="highlight">
                                <p><strong>Chi tiết Đặt Tour của bạn:</strong></p>
                                <ul class="tour-info">
                                    <li><strong>Tên Tour:</strong> %s</li>
                                    <li><strong>Mã Đơn hàng:</strong> %s</li>
                                    <li><strong>Tổng Số tiền:</strong> %s VND</li>
                                </ul>
                            </div>
                
                            <p>Chúng tôi sẽ sớm liên hệ với bạn để xác nhận lại thông tin chi tiết và chuẩn bị cho chuyến đi của bạn.</p>
                            <p>Nếu bạn có bất kỳ câu hỏi nào, xin đừng ngần ngại liên hệ với chúng tôi.</p>
                
                        </div>
                        <div class="footer">
                            <p>Trân trọng,</p>
                            <p>Đội ngũ hỗ trợ SmartTour</p>
                            <p>Bản quyền &copy; %s SmartTour. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(guestName, tourTitle, bookingId, formatter.format(price), java.time.Year.now().getValue());
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

    public void sendBookingCancelEmail(Booking booking, double originalAmount, double penaltyPercent, double refundAmount) {
        String to = booking.getEmail();
        String subject = "Thông báo Hủy Tour Thành Công và Chi tiết Hoàn Tiền";

        // Use DecimalFormat for currency formatting
        DecimalFormat currencyFormatter = new DecimalFormat("#,###");
        // Use DecimalFormat for percentage formatting, if needed more precisely than String.format
        DecimalFormat percentFormatter = new DecimalFormat("#.#");

        String bookingTime = formatInstantToReadable(booking.getBookingDate());
        String cancelTime = formatInstantToReadable(Instant.now());

        String htmlContent = """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Hủy Tour Thành Công - SmartTour</title>
                    <style>
                        body {
                            font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                            margin: 0;
                            padding: 0;
                            background-color: #f4f4f4;
                            color: #333;
                        }
                        .container {
                            max-width: 600px;
                            margin: 20px auto;
                            background-color: #ffffff;
                            border-radius: 8px;
                            overflow: hidden;
                            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                        }
                        .header {
                            background-color: #dc3545; /* Màu đỏ cho hủy tour */
                            color: #ffffff;
                            padding: 20px;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 28px;
                        }
                        .content {
                            padding: 25px;
                            line-height: 1.6;
                            font-size: 16px;
                        }
                        .content p {
                            margin-bottom: 10px;
                        }
                        .info-block {
                            background-color: #ffe6e6; /* Nền nhẹ cho thông tin hủy */
                            border-left: 5px solid #dc3545;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 4px;
                        }
                        .info-block strong {
                            color: #a71d2a;
                        }
                        .detail-list {
                            list-style: none;
                            padding: 0;
                            margin: 15px 0;
                        }
                        .detail-list li {
                            margin-bottom: 8px;
                        }
                        .detail-list strong {
                            display: inline-block;
                            width: 150px; /* Căn chỉnh các nhãn */
                        }
                        .footer {
                            background-color: #f2f2f2;
                            color: #777;
                            padding: 20px;
                            text-align: center;
                            font-size: 13px;
                            border-top: 1px solid #eee;
                        }
                        .footer p {
                            margin: 5px 0;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Xác nhận Hủy Tour</h1>
                        </div>
                        <div class="content">
                            <p>Xin chào <strong>%s</strong>,</p>
                            <p>Chúng tôi xác nhận rằng tour <b>%s</b> của bạn (mã đơn hàng: %s) đã được hủy thành công.</p>
                            <p>Chúng tôi rất tiếc khi bạn phải hủy chuyến đi này.</p>
                
                            <div class="info-block">
                                <p><strong>Chi tiết Đơn hàng:</strong></p>
                                <ul class="detail-list">
                                    <li><strong>Mã Đơn hàng:</strong> %s</li>
                                    <li><strong>Ngày Đặt Tour:</strong> %s</li>
                                    <li><strong>Ngày Hủy:</strong> %s</li>
                                    <li><strong>Tổng Tiền Ban Đầu:</strong> %s VND</li>
                                </ul>
                            </div>
                
                            <div class="info-block">
                                <p><strong>Chi tiết Hoàn Tiền:</strong></p>
                                <ul class="detail-list">
                                    <li><strong>Phí Hủy Tour:</strong> %s%%</li>
                                    <li><strong>Số Tiền Hoàn Lại Dự Kiến:</strong> %s VND</li>
                                </ul>
                                <p>Số tiền hoàn lại sẽ được xử lý trong vòng <strong>15 ngày làm việc</strong></p>
                            </div>
                
                            <p>Nếu bạn có bất kỳ câu hỏi hoặc cần hỗ trợ thêm, vui lòng liên hệ với bộ phận hỗ trợ khách hàng của chúng tôi.</p>
                        </div>
                        <div class="footer">
                            <p>Trân trọng,</p>
                            <p>Đội ngũ hỗ trợ SmartTour</p>
                            <p>Bản quyền &copy; %s SmartTour. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                booking.getUserName(),
                booking.getTour().getTitle(),
                booking.getId(),
                booking.getId(),
                bookingTime,
                cancelTime,
                currencyFormatter.format(originalAmount),
                percentFormatter.format(penaltyPercent),
                currencyFormatter.format(refundAmount),
                java.time.Year.now().getValue()
        );

        sendHtmlEmail(to, subject, htmlContent);
    }

    public String formatInstantToReadable(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));  // múi giờ VN
        return formatter.format(instant);
    }

    public void sendBookingReinstatedEmail(Booking booking) {
        String to = booking.getEmail();
        String subject = "SmartTour: Đơn hàng của bạn đã được khôi phục thành công";

        String htmlContent = buildBookingReinstatedHtml(booking);

        sendHtmlEmail(to, subject, htmlContent);
    }

    private String buildBookingReinstatedHtml(Booking booking) {
        DecimalFormat currencyFormatter = new DecimalFormat("#,###");
        String reinstatedTime = formatInstantToReadable(Instant.now());

        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Khôi phục Đơn hàng thành công - SmartTour</title>
                    <style>
                        body {
                            font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                            margin: 0; padding: 0; background-color: #f4f4f4; color: #333;
                        }
                        .container {
                            max-width: 600px; margin: 20px auto; background-color: #ffffff;
                            border-radius: 8px; overflow: hidden; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                        }
                        .header {
                            background-color: #28a745; /* Màu xanh lá cho sự thành công/khôi phục */
                            color: #ffffff; padding: 20px; text-align: center;
                        }
                        .header h1 {
                            margin: 0; font-size: 28px;
                        }
                        .content {
                            padding: 25px; line-height: 1.6; font-size: 16px;
                        }
                        .content p {
                            margin-bottom: 10px;
                        }
                        .highlight {
                            background-color: #e9f5e9; /* Nền xanh lá nhạt */
                            border-left: 5px solid #28a745;
                            padding: 15px; margin: 20px 0; border-radius: 4px;
                        }
                        .highlight strong {
                            color: #155724; /* Xanh lá đậm */
                        }
                        .detail-list {
                            list-style: none; padding: 0; margin: 15px 0;
                        }
                        .detail-list li {
                            margin-bottom: 8px;
                        }
                        .detail-list strong {
                            display: inline-block; width: 140px;
                        }
                        .footer {
                            background-color: #f2f2f2; color: #777; padding: 20px;
                            text-align: center; font-size: 13px; border-top: 1px solid #eee;
                        }
                        .footer p {
                            margin: 5px 0;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Đơn Hàng Được Khôi Phục</h1>
                        </div>
                        <div class="content">
                            <p>Xin chào <strong>%s</strong>,</p>
                            <p>Chúng tôi vui mừng thông báo rằng đơn hàng đã hủy của bạn đã được khôi phục thành công theo yêu cầu. Đơn hàng của bạn hiện đã <strong>hoạt động trở lại</strong>.</p>
                
                            <div class="highlight">
                                <p><strong>Thông tin chi tiết đơn hàng được khôi phục:</strong></p>
                                <ul class="detail-list">
                                    <li><strong>Tên Tour:</strong> %s</li>
                                    <li><strong>Mã Đơn hàng:</strong> %s</li>
                                    <li><strong>Tổng chi phí:</strong> %s VND</li>
                                    <li><strong>Ngày khôi phục:</strong> %s</li>
                                </ul>
                            </div>
                
                            <p>Tất cả thông tin chi tiết về chuyến đi của bạn đã được giữ lại. Vui lòng chuẩn bị cho một cuộc phiêu lưu thú vị sắp tới!</p>
                            <p>Nếu bạn có bất kỳ câu hỏi nào, xin đừng ngần ngại liên hệ với chúng tôi.</p>
                        </div>
                        <div class="footer">
                            <p>Trân trọng,</p>
                            <p>Đội ngũ hỗ trợ SmartTour</p>
                            <p>Bản quyền &copy; %d SmartTour. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                booking.getUserName(),
                booking.getTour().getTitle(),
                booking.getId(),
                currencyFormatter.format(booking.getTotalPrice()),
                reinstatedTime,
                java.time.Year.now().getValue()
        );
    }

    public void sendPasswordResetEmail(User user, String newPassword) {
        final String APP_NAME = "SmartTour";
        final String subject = "Mật khẩu mới cho tài khoản " + APP_NAME + " của bạn";
        String htmlContent = buildPasswordResetEmailHtml(user.getFullName(), newPassword, APP_NAME);

        // Gọi lại hàm sendHtmlEmail đã có
        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }

    private String buildPasswordResetEmailHtml(String fullName, String newPassword, String appName) {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Cấp Lại Mật Khẩu</title>
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f0f2f5; }
                        .container { max-width: 600px; margin: 30px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); overflow: hidden; }
                        .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                        .header h1 { margin: 0; font-size: 24px; }
                        .content { padding: 30px; line-height: 1.7; color: #333; }
                        .content p { margin-bottom: 15px; }
                        .password-box { background-color: #e9ecef; border: 1px dashed #ced4da; border-radius: 5px; padding: 15px; text-align: center; margin: 20px 0; }
                        .password-box b { font-size: 20px; color: #dc3545; letter-spacing: 2px; font-family: 'Courier New', Courier, monospace; }
                        .footer { text-align: center; padding: 20px; font-size: 12px; color: #6c757d; background-color: #f8f9fa; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Yêu Cầu Cấp Lại Mật Khẩu</h1>
                        </div>
                        <div class="content">
                            <p>Xin chào <strong>%s</strong>,</p>
                            <p>Bạn đã yêu cầu cấp lại mật khẩu cho tài khoản của mình tại %s.</p>
                            <p>Dưới đây là mật khẩu mới của bạn:</p>
                            <div class="password-box">
                                <b>%s</b>
                            </div>
                            <p>Để đảm bảo an toàn, vui lòng đăng nhập bằng mật khẩu này và đổi sang một mật khẩu khác mà bạn có thể ghi nhớ.</p>
                            <p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>
                        </div>
                        <div class="footer">
                            <p>Trân trọng,<br>Đội ngũ %s</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                fullName,
                appName,
                newPassword,
                appName
        );
    }
}