package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.repository.UserRepository;
import com.dinhngoctranduy.service.impl.PromotionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BirthdayPromotionScheduler {

    private static final Logger log = LoggerFactory.getLogger(BirthdayPromotionScheduler.class);

    private final UserRepository userRepository;
    private final PromotionServiceImpl promotionService;

    @Scheduled(cron = "0 22 12 * * ?", zone = "Asia/Ho_Chi_Minh")
    public void sendBirthdayPromotions() {
        log.info("--- [SCHEDULER] Bắt đầu tác vụ kiểm tra và gửi quà sinh nhật ---");

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();

        log.info("[SCHEDULER] Đang kiểm tra sinh nhật cho ngày: {} tháng {}", day, month);

        try {
            List<User> birthdayUsers = userRepository.findUsersByBirthday(month, day);

            if (birthdayUsers.isEmpty()) {
                log.info("[SCHEDULER] Hôm nay không có sinh nhật của người dùng nào.");
            } else {
                log.info("[SCHEDULER] Tìm thấy {} người dùng có sinh nhật hôm nay. Bắt đầu gửi quà.", birthdayUsers.size());
                for (User user : birthdayUsers) {
                    log.info("[SCHEDULER] Chuẩn bị gửi quà sinh nhật cho người dùng: {} (ID: {})", user.getUsername(), user.getId());
                    promotionService.createAndSendBirthdayPromotion(user);
                }
            }
        } catch (Exception e) {
            log.error("[SCHEDULER] Đã xảy ra lỗi không mong muốn trong quá trình gửi quà sinh nhật: {}", e.getMessage(), e);
        }


        log.info("--- [SCHEDULER] Kết thúc tác vụ gửi quà sinh nhật ---");
    }
}
