package com.arya.attendance.repository;

import com.arya.attendance.model.TimetableSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface TimetableSlotRepository extends JpaRepository<TimetableSlot, Long> {
    List<TimetableSlot> findByDay(DayOfWeek day);
}
