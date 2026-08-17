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

    // part of old code removed on 17-08-26 while adding user funcnality will check if error
//    private com.arya.attendance.model.User currentUser(org.springframework.security.core.annotation.AuthenticationPrincipal com.arya.attendance.security.AppUserDetails principal) {
//        return principal.getUser();
//    }

    /** GET /attendance/day?date=2026-07-30 -> classes scheduled that day + whether already marked */
    @GetMapping("/day")
    public List<Map<String, Object>> classesForDay(@RequestParam(required = false) String date,
                                                   @org.springframework.security.core.annotation.AuthenticationPrincipal com.arya.attendance.security.AppUserDetails principal) {
        LocalDate d = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        List<TimetableSlot> slots = slotRepository.findByDay(d.getDayOfWeek());
        List<AttendanceRecord> existing = recordRepository.findByDateAndUser_Id(d, principal.getUser().getId());

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
    public AttendanceRecord mark(@RequestBody MarkRequest req,
                                 @org.springframework.security.core.annotation.AuthenticationPrincipal com.arya.attendance.security.AppUserDetails principal) {
        TimetableSlot slot = slotRepository.findById(req.slotId)
                .orElseThrow(() -> new NoSuchElementException("Slot not found: " + req.slotId));
        LocalDate date = LocalDate.parse(req.date);
        AttendanceStatus status = AttendanceStatus.valueOf(req.status.toUpperCase());
        com.arya.attendance.model.User user = principal.getUser();

        AttendanceRecord record = recordRepository
                .findByCourse_CodeAndDateAndSlot_IdAndUser_Id(slot.getCourse().getCode(), date, slot.getId(), user.getId())
                .orElse(new AttendanceRecord(slot.getCourse(), slot, date, status, user));
        record.setStatus(status);
        return recordRepository.save(record);
    }
    /** GET /attendance/percentage -> per-course attendance % across all marked records */
    @GetMapping("/percentage")
    public List<Map<String, Object>> percentages(@RequestParam(defaultValue = "75") double threshold,
                                                 @org.springframework.security.core.annotation.AuthenticationPrincipal com.arya.attendance.security.AppUserDetails principal) {
        List<Course> courses = courseRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Course c : courses) {
            List<AttendanceRecord> records = recordRepository.findByCourse_CodeAndUser_Id(c.getCode(), principal.getUser().getId());
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
