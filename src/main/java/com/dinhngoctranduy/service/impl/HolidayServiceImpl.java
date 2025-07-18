package com.dinhngoctranduy.service.impl;

import com.dinhngoctranduy.model.Holiday;
import com.dinhngoctranduy.repository.HolidayRepository;
import com.dinhngoctranduy.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.ZoneId;
import java.util.List;

@Service
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;

    @Autowired
    public HolidayServiceImpl(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    @Override
    public boolean isHoliday(Instant date) {
        LocalDate localDate = date.atZone(ZoneId.systemDefault()).toLocalDate();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        return holidayRepository.findAll().stream()
                .anyMatch(h -> h.getMonth() == month && h.getDay() == day);
    }

    @Override
    public boolean hasHolidayInRange(Instant start, Instant end) {
        LocalDate startDate = start.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = end.atZone(ZoneId.systemDefault()).toLocalDate();
        List<Holiday> holidays = holidayRepository.findAll();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            int month = date.getMonthValue();
            int day = date.getDayOfMonth();
            if (holidays.stream().anyMatch(h -> h.getMonth() == month && h.getDay() == day)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Holiday> getAllHolidays() {
        return holidayRepository.findAll();
    }
}
