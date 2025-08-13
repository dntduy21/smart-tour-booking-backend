# Smart Tour Booking - Hệ thống đặt tour du lịch thông minh

## Giới thiệu
Smart Tour Booking là hệ thống đặt tour du lịch thông minh, được phát triển trong khuôn khổ luận văn tốt nghiệp.  
Mục tiêu của hệ thống là giúp người dùng dễ dàng tìm kiếm, đặt tour phù hợp và nhận được các gợi ý cá nhân hóa dựa trên nội dung tour và lịch sử đặt tour.  
Đồng thời, hệ thống cung cấp cho quản trị viên (admin) công cụ quản lý toàn diện, hỗ trợ quản lý tour, người dùng, khuyến mãi, đơn đặt hàng và báo cáo thống kê.

**Đặc điểm nổi bật:**
1. Tích hợp gợi ý thông minh dựa trên Content-Based Filtering (TF-IDF + Cosine Similarity). [GitHub Repo](https://github.com/dntduy21/recommend-tours)
2. Tìm kiếm & lọc tour đa tiêu chí.
3. Quy trình đặt tour linh hoạt, hỗ trợ thanh toán VNPay và tại quầy.
4. Bảng điều khiển thống kê trực quan cho quản trị viên.
5. Quản lý khuyến mãi, vai trò, phân quyền, tin tức và nhiều tính năng khác.

---

## Công nghệ sử dụng

1. Spring: Framework Java để xây dựng ứng dụng web nhanh chóng, dễ cấu hình và dễ triển khai
2. MySQL: Hệ quản trị cơ sở dữ liệu quan hệ, ổn định và phổ biến.
3. Flask (Python): Xây dựng API gợi ý tour dựa trên Content-Based Filtering.
4. Pandas, Scikit-learn: Xử lý dữ liệu và triển khai TF-IDF + Cosine Similarity cho gợi ý.

## Công cụ phát triển
1. IntelliJ IDEA: IDE mạnh mẽ cho phát triển Java và Spring Boot
2. PyCharm: IDE chuyên dụng cho Python, hỗ trợ mạnh mẽ trong việc phát triển, gỡ lỗi và quản lý code.
3. Git: Hệ thống quản lý mã nguồn phân tán

---

## Các tính năng chính

### Người dùng (Khách hàng)
1. Đăng ký / Đăng nhập / Quên mật khẩu (xác thực email).
2. Tìm kiếm & lọc tour theo giá, điểm đến, thời gian, đánh giá.
3. Xem chi tiết tour (mô tả, lịch trình, đánh giá, xuất PDF).
4. Gợi ý tour liên quan dựa trên tour đang xem.
5. Gợi ý tour cá nhân hóa dựa trên lịch sử đặt tour.
6. Đặt tour (hỗ trợ thanh toán VNPay hoặc tại quầy, áp dụng mã khuyến mãi nếu đăng nhập).
7. Hủy tour (theo quy định về ngày lễ/ngày thường, tính phí phạt).
8. Quản lý tài khoản (cập nhật thông tin, đổi mật khẩu, xóa tài khoản, xem lịch sử đánh giá).
9. Xem & tìm kiếm khuyến mãi.
10. Đánh giá tour sau khi hoàn thành chuyến đi.
11. Đặt tour theo yêu cầu (yêu cầu tư vấn riêng).
12. Xem tin tức du lịch.

### Quản trị viên (Admin)
1. Đăng nhập quản trị.
2. Quản lý tour (thêm, sửa, xóa, tìm kiếm, thay đổi trạng thái, xuất PDF/Excel).
3. Gửi mã khuyến mãi sinh nhật tự động.
4. Quản lý khuyến mãi (thêm, sửa, xóa, gửi mã qua email).
5. Quản lý đơn đặt tour (xác nhận, hoàn thành, hoàn tiền, hủy, khôi phục).
6. Quản lý người dùng (khóa/mở khóa, xóa, xem lịch sử, chỉnh sửa thông tin).
7. Quản lý tour theo yêu cầu (cập nhật trạng thái, gửi email tư vấn).
8. Quản lý vai trò & phân quyền.
9. Báo cáo & thống kê (doanh thu, top tour, tỷ lệ hủy).
10. Quản lý tin tức.
11. Quản lý phân loại tour.
12. Quản lý ngày lễ (xác định mức phạt khi hủy tour).
13. Quản lý banner hiển thị trên trang chủ.
14. Quản lý thông tin website (thông tin liên hệ, footer).

---

## Cấu trúc thư mục

```plaintext
smart-tour-booking-backend/
├── src/
│   └── main/
│       ├── java/            # Mã nguồn Java
│       └── resources/       # Cấu hình và tài nguyên
├── pom.xml                  # Cấu hình Maven
 ```

## Yêu cầu hệ thống
1. Java 17+
2. MySQL 8.0+
3. Maven
4. Python 3.9+
5. Pip – Quản lý thư viện Python.
6. Các thư viện Python bắt buộc:
   - pandas
   - sqlalchemy
   - mysql-connector-python
   - scikit-learn
   - flask
   - flask-cors
   - numpy

## Cài đặt
```plaintext
Backend
git clone https://github.com/dntduy21/smart-tour-booking-backend.git
cd smart-tour-booking-backend

Recommend system
Truy cập https://github.com/dntduy21/recommend-tours
git clone https://github.com/dntduy21/recommend-tours.git
cd recommend-tours
```
Ứng dụng backend chạy tại: http://localhost:8080

Ứng dụng recommend system chạy tại: http://localhost:5000

## Liên hệ
Email: dinhngoctranduy.2105@gmail.com
