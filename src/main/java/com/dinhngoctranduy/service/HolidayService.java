package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.Holiday;

import java.time.Instant;
import java.util.List;

public interface HolidayService {
    boolean isHoliday(Instant date);

    boolean hasHolidayInRange(Instant start, Instant end);

    List<Holiday> getAllHolidays();
}
