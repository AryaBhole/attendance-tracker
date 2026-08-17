package com.arya.attendance.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendance_records", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"course_code", "attendance_date", "slot_id", "user_id"})
})
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_code")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "slot_id")
    private TimetableSlot slot;

    @Column(name = "attendance_date")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    public AttendanceRecord() {}

    public AttendanceRecord(Course course, TimetableSlot slot, LocalDate date, AttendanceStatus status, User user) {
        this.course = course;
        this.slot = slot;
        this.date = date;
        this.status = status;
        this.user = user;
    }

    public Long getId() { return id; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public TimetableSlot getSlot() { return slot; }
    public void setSlot(TimetableSlot slot) { this.slot = slot; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
