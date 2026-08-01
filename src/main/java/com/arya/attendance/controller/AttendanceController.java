package com.arya.attendance.controller;

import com.arya.attendance.model.*;
import com.arya.attendance.repository.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final TimetableSlotRepository slotRepository;
    private final AttendanceRecordRepository recordRepository;
    private final CourseRepository courseRepository;

    public AttendanceController(TimetableSlotRepository slotRepository,
                                 AttendanceRecordRepository recordRepository,
                                 CourseRepository courseRepository) {
        this.slotRepository = slotRepository;
        this.recordRepository = recordRepository;
        this.courseRepository = courseRepository;
    }

    /** GET /attendance/day?date=2026-07-30 -> classes scheduled that day + whether already marked */
    @GetMapping("/day")
    public List<Map<String, Object>> classesForDay(@RequestParam(required = false) String date) {
        LocalDate d = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        List<TimetableSlot> slots = slotRepository.findByDay(d.getDayOfWeek());
        List<AttendanceRecord> existing = recordRepository.findByDate(d);

        return slots.stream().map(slot -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("slotId", slot.getId());
            m.put("courseCode", slot.getCourse().getCode());
            m.put("courseTitle", slot.getCourse().getTitle());
            m.put("start", slot.getStartTime().toString());
            m.put("end", slot.getEndTime().toString());
            Optional<AttendanceRecord> rec = existing.stream()
                    .filter(r -> r.getSlot().getId().equals(slot.getId()))
                    .findFirst();
            m.put("status", rec.map(r -> r.getStatus().name()).orElse("UNMARKED"));
            return m;
        }).collect(Collectors.toList());
    }

    /** POST /attendance/mark  body: {"slotId":1,"date":"2026-07-30","status":"PRESENT"} */
    @PostMapping("/mark")
    public AttendanceRecord mark(@RequestBody MarkRequest req) {
        TimetableSlot slot = slotRepository.findById(req.slotId)
                .orElseThrow(() -> new NoSuchElementException("Slot not found: " + req.slotId));
        LocalDate date = LocalDate.parse(req.date);
        AttendanceStatus status = AttendanceStatus.valueOf(req.status.toUpperCase());

        AttendanceRecord record = recordRepository
                .findByCourse_CodeAndDateAndSlot_Id(slot.getCourse().getCode(), date, slot.getId())
                .orElse(new AttendanceRecord(slot.getCourse(), slot, date, status));
        record.setStatus(status);
        return recordRepository.save(record);
    }

    /** GET /attendance/percentage -> per-course attendance % across all marked records */
    @GetMapping("/percentage")
    public List<Map<String, Object>> percentages(@RequestParam(defaultValue = "75") double threshold) {
        List<Course> courses = courseRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Course c : courses) {
            List<AttendanceRecord> records = recordRepository.findByCourse_Code(c.getCode());
            long counted = records.stream().filter(r -> r.getStatus() != AttendanceStatus.CANCELLED).count();
            long present = records.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count();
            double pct = counted == 0 ? 100.0 : (present * 100.0 / counted);

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("courseCode", c.getCode());
            m.put("courseTitle", c.getTitle());
            m.put("held", counted);
            m.put("present", present);
            m.put("percentage", Math.round(pct * 100.0) / 100.0);
            m.put("belowThreshold", pct < threshold);
            result.add(m);
        }
        return result;
    }

    public static class MarkRequest {
        public Long slotId;
        public String date;
        public String status;
    }
}
