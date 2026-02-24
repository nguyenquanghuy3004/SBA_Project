package com.example.project_web.service;

import com.example.project_web.entity.Enrollment;
import java.util.List;

public interface EnrollmentService {
    Enrollment enrollStudent(Long studentId, Long classId);
    void cancelEnrollment(Long studentId, Long classId);
    List<Enrollment> getStudentEnrollments(Long studentId);
    List<Enrollment> getClassEnrollments(Long classId);
    Enrollment updateGrades(Long enrollmentId, Double attendance, Double midterm, Double finalScore);
}
