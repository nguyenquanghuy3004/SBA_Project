package com.example.project_web.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false)
    private String courseCode; // max môn

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(nullable = false)
    private Integer creadits;

    public Course() {
    }

    public Course(Long id, String courseCode, String name, Integer creadits) {
        this.id = id;
        this.courseCode = courseCode;
        this.name = name;
        this.creadits = creadits;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCreadits() {
        return creadits;
    }

    public void setCreadits(Integer creadits) {
        this.creadits = creadits;
    }
}
