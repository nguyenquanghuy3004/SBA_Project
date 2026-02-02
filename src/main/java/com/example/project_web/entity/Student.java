package com.example.project_web.entity;


import jakarta.persistence.*;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "students")
@JsonPropertyOrder({ "studentId", "studentName", "gender", "dateOfBirth", "studentEmail", "studentPhone", "major", "gpa" })
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Student_ID")
    private Long studentId;

    @Column(name = "Student_Name", nullable = false, length = 255, columnDefinition = "NVARCHAR(255)")
    private String studentName;

    @Column(name = "Gender",nullable = false)
    private String gender;

    @Column(name = "Date_Of_Birthday" ,nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "Student_Email",nullable = false,unique = true)
    private String studentEmail;

    @Column(name = "Student_Phone", nullable = false,unique = true)
    private String studentPhone;

    @Column(name = "Major",nullable = false)
    private String major;

    @Column(name = "Gpa",nullable = false)
    private Double gpa;

    public Student() {

    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

}
