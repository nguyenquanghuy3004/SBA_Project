package com.example.project_web.controller;

import com.example.project_web.entity.Enrollment;
import com.example.project_web.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5174", "http://localhost:5173"}, maxAge = 3600)
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    // Student registers for a class
    @PostMapping("/register")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Enrollment> enroll(@RequestBody Map<String, Long> request) {
        Long studentId = request.get("studentId");
        Long classId = request.get("classId");
        return ResponseEntity.ok(enrollmentService.enrollStudent(studentId, classId));
    }

    // Student cancels a registration
    @DeleteMapping("/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> cancel(@RequestParam Long studentId, @RequestParam Long classId) {
        enrollmentService.cancelEnrollment(studentId, classId);
        return ResponseEntity.noContent().build();
    }

    // Get all enrollments (subjects) of a student
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN', 'TEACHER')")
    public ResponseEntity<List<Enrollment>> getStudentSubjects(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.getStudentEnrollments(studentId));
    }

    // Get all students in a class (Teacher view)
    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<Enrollment>> getClassList(@PathVariable Long classId) {
        return ResponseEntity.ok(enrollmentService.getClassEnrollments(classId));
    }

    // Teacher updates grades
    @PutMapping("/grades/{enrollmentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Enrollment> updateGrades(
            @PathVariable Long enrollmentId,
            @RequestBody Map<String, Double> grades) {
        
        return ResponseEntity.ok(enrollmentService.updateGrades(
                enrollmentId,
                grades.get("attendance"),
                grades.get("midterm"),
                grades.get("finalScore")
        ));
    }
}
