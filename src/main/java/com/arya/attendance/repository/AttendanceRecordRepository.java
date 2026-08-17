package com.arya.attendance.repository;

import com.arya.attendance.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByCourse_CodeAndUser_Id(String courseCode, Long userId);
    List<AttendanceRecord> findByDateAndUser_Id(LocalDate date, Long userId);
    Optional<AttendanceRecord> findByCourse_CodeAndDateAndSlot_IdAndUser_Id(String courseCode, LocalDate date, Long slotId, Long userId);
}
