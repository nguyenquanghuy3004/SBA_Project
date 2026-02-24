package com.example.project_web.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@Table(name = "teachers")
@JsonPropertyOrder({ "id", "user", "fullName", "department", "degree" })
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String fullName;

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String department;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String degree; // Học vị (Thạc sĩ, Tiến sĩ...)

    public Teacher() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }
}
