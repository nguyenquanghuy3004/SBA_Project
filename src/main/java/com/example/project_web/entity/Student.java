package com.example.project_web.entity;


import jakarta.persistence.*;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "students")
@JsonPropertyOrder({ "studentId", "user", "studentName", "gender", "dateOfBirth", "studentEmail", "studentPhone", "address", "studentClass", "major", "gpa" })
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "mssv", nullable = false, unique = true, length = 8)
    private String mssv;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "student_name", nullable = false, length = 255, columnDefinition = "NVARCHAR(255)")
    private String studentName;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "date_of_birthday", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "student_email", nullable = false, unique = true)
    private String studentEmail;

    @Column(name = "student_phone", nullable = false, unique = true)
    private String studentPhone;

    @Column(name = "address", columnDefinition = "NVARCHAR(255)")
    private String address;

    @Column(name = "student_class", columnDefinition = "NVARCHAR(50)")
    private String studentClass;

    @Column(name = "major", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String major;

    @Column(name = "gpa", nullable = false)
    private Double gpa;

    public Student() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

}
