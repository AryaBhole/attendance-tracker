package com.arya.attendance.model;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "timetable_slots")
public class TimetableSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private DayOfWeek day;

    private LocalTime startTime;
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "course_code")
    private Course course;

    public TimetableSlot() {}

    public TimetableSlot(DayOfWeek day, LocalTime startTime, LocalTime endTime, Course course) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.course = course;
    }

    public Long getId() { return id; }

    public DayOfWeek getDay() { return day; }
    public void setDay(DayOfWeek day) { this.day = day; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}
