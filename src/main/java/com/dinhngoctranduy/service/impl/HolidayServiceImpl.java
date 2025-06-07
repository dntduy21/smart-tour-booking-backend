package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.service.HolidayService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.ZoneId;
import java.util.List;

@Service
public class HolidayServiceImpl implements HolidayService {

    private static final List<MonthDay> HOLIDAYS = List.of(
            MonthDay.of(1, 1),    // Tết Dương lịch
            MonthDay.of(4, 30),   // Giải phóng miền Nam
            MonthDay.of(5, 1),    // Quốc tế Lao động
            MonthDay.of(9, 2)     // Quốc khánh
            // Add more if needed
    );

    @Override
    public boolean isHoliday(Instant date) {
        LocalDate localDate = date.atZone(ZoneId.systemDefault()).toLocalDate();
        MonthDay monthDay = MonthDay.from(localDate);
        return HOLIDAYS.contains(monthDay);
    }
}
