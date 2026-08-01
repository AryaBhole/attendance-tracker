package com.arya.attendance.config;

import com.arya.attendance.model.Course;
import com.arya.attendance.model.TimetableSlot;
import com.arya.attendance.repository.CourseRepository;
import com.arya.attendance.repository.TimetableSlotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final TimetableSlotRepository slotRepository;

    public DataSeeder(CourseRepository courseRepository, TimetableSlotRepository slotRepository) {
        this.courseRepository = courseRepository;
        this.slotRepository = slotRepository;
    }

    @Override
    public void run(String... args) {
        if (courseRepository.count() > 0) {
            return; // already seeded, don't duplicate on every restart
        }

        // ---- Courses ----
        Course bt3181 = save(new Course("BT3181", "Immunology", "Dr. Sarada P Mallick"));
        Course bt3191 = save(new Course("BT3191", "Genetic Engineering", "Dr. Danish Diwan"));
        Course bt3201 = save(new Course("BT3201", "Upstream Bioprocessing", "Dr. Mohammed Rafi"));
        Course bt3231 = save(new Course("BT3231", "Industrial Biotechnology (Dept. Elective-2)", "Dr. Antony Vincy F C"));
        Course bt3261 = save(new Course("BT3261", "Bioreactor Design and Analysis (Dept. Elective-3)", "Dr. Arpita Sahoo"));
        Course bt3171 = save(new Course("BT3171", "Environmental Technology (Open Elective)", "Dr. Ajit Kumar"));
        Course bt3212 = save(new Course("BT3212", "Genetic Engineering and Immunology Lab", "Dr. Sarada P Mallick / Dr. Danish Diwan / team"));
        Course bt3222 = save(new Course("BT3222", "Bioreaction and Upstream Bioprocessing Lab", "Dr. Seenivasan A / team"));
        Course sm3011 = save(new Course("SM3011", "Introduction to Entrepreneurship", "D. A Shrikant"));

        // ---- Weekly slots (from the timetable) ----
        // Monday
        slot(DayOfWeek.MONDAY, 9, bt3181);
        slot(DayOfWeek.MONDAY, 10, bt3261);
        slot(DayOfWeek.MONDAY, 11, bt3201);
        slot(DayOfWeek.MONDAY, 12, bt3171); // OE

        // Tuesday
        slot(DayOfWeek.TUESDAY, 9, bt3181);
        slot(DayOfWeek.TUESDAY, 10, bt3261);
        slot(DayOfWeek.TUESDAY, 11, bt3231);
        slot(DayOfWeek.TUESDAY, 12, bt3171); // OE

        // Wednesday
        slot(DayOfWeek.WEDNESDAY, 9, bt3201);
        slot(DayOfWeek.WEDNESDAY, 10, bt3261);
        slot(DayOfWeek.WEDNESDAY, 11, bt3231);
        slot(DayOfWeek.WEDNESDAY, 12, bt3171); // OE
        slotRange(DayOfWeek.WEDNESDAY, 14, 0, 16, 50, bt3212); // lab, 14:00-16:50

        // Thursday
        slot(DayOfWeek.THURSDAY, 9, bt3181);
        slot(DayOfWeek.THURSDAY, 10, bt3191);
        slot(DayOfWeek.THURSDAY, 11, bt3181);
        slotRange(DayOfWeek.THURSDAY, 14, 0, 16, 50, bt3222); // lab, moved from Tuesday
        slot(DayOfWeek.THURSDAY, 12, bt3231);

        // Friday
        slot(DayOfWeek.FRIDAY, 9, bt3201);
        slot(DayOfWeek.FRIDAY, 10, bt3191);
        slot(DayOfWeek.FRIDAY, 11, sm3011);
        slot(DayOfWeek.FRIDAY, 12, bt3191);

        System.out.println("Seeded courses and timetable slots from III B.Tech Biotech timetable.");
    }

    private Course save(Course c) {
        return courseRepository.save(c);
    }

    private void slot(DayOfWeek day, int hour, Course course) {
        // helper for the common "same hour, :00 to :50" slots
        slotRepository.save(new TimetableSlot(day, LocalTime.of(hour, 0), LocalTime.of(hour, 50), course));
    }

    private void slotRange(DayOfWeek day, int startHour, int startMin, int endHour, int endMin, Course course) {
        slotRepository.save(new TimetableSlot(day, LocalTime.of(startHour, startMin), LocalTime.of(endHour, endMin), course));
    }
}
