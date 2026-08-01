package com.arya.attendance.repository;

import com.arya.attendance.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByCourse_Code(String courseCode);
    List<AttendanceRecord> findByDate(LocalDate date);
    Optional<AttendanceRecord> findByCourse_CodeAndDateAndSlot_Id(String courseCode, LocalDate date, Long slotId);
}
