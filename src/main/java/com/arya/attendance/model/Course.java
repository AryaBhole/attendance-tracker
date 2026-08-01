package com.arya.attendance.model;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    private String code; // e.g. BT3181

    private String title;
    private String faculty;

    public Course() {}

    public Course(String code, String title, String faculty) {
        this.code = code;
        this.title = title;
        this.faculty = faculty;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }
}
