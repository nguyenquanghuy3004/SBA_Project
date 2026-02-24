package com.example.project_web.service.impl;

import com.example.project_web.entity.Enrollment;
import com.example.project_web.entity.Semester;
import com.example.project_web.entity.Student;
import com.example.project_web.entity.SubjectClass;
import com.example.project_web.exception.AppException;
import com.example.project_web.repository.EnrollmentRepository;
import com.example.project_web.repository.StudentRepository;
import com.example.project_web.repository.SubjectClassRepository;
import com.example.project_web.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectClassRepository subjectClassRepository;

    @Override
    @Transactional
    public Enrollment enrollStudent(Long studentId, Long classId) {
        // 1. Check if student and class exist
        Student student = studentRepository.findById(studentId)
                .orElseGet(() -> studentRepository.findByUserId(studentId)
                .orElseThrow(() -> new AppException("Student not found")));
        SubjectClass subjectClass = subjectClassRepository.findById(classId)
                .orElseThrow(() -> new AppException("Class not found"));

        // 2. NEW: Check Registration Period
        LocalDate now = LocalDate.now();
        Semester semester = subjectClass.getSemester();
        if (semester.getStartDate() != null && semester.getEndDate() != null) {
            if (now.isBefore(semester.getStartDate()) || now.isAfter(semester.getEndDate())) {
                throw new AppException("Hiện tại không nằm trong thời gian đăng ký học phần của học kỳ này.");
            }
        }

        // 3. Check if already enrolled in this SPECIFIC class
        if (enrollmentRepository.existsByStudentStudentIdAndSubjectClassId(student.getStudentId(), classId)) {
            throw new AppException("Sinh viên đã đăng ký lớp học này rồi.");
        }

        // 3. New Business Rule: Check for Schedule Overlap
        List<Enrollment> currentEnrollments = enrollmentRepository.findByStudentStudentId(student.getStudentId());
        String newSchedule = subjectClass.getSchedule();
        
        boolean isOverlapped = currentEnrollments.stream()
                .anyMatch(en -> en.getSubjectClass().getSchedule().equalsIgnoreCase(newSchedule));
        
        if (isOverlapped) {
            throw new AppException("Trùng lịch học! Bạn đã có môn học khác vào thời gian: " + newSchedule);
        }

        // 4. New Business Rule: Maximum Credits Check (Max 25)
        int currentCredits = currentEnrollments.stream()
                .mapToInt(en -> en.getSubjectClass().getCourse().getCreadits())
                .sum();
        
        int newCourseCredits = subjectClass.getCourse().getCreadits();
        if (currentCredits + newCourseCredits > 25) {
            throw new AppException("Vượt quá giới hạn 25 tín chỉ trong một học kỳ. Hiện tại: " + currentCredits);
        }

        // 5. Check capacity
        long currentEnrolledSize = enrollmentRepository.findBySubjectClassId(classId).size();
        if (subjectClass.getMaxStudents() != null && currentEnrolledSize >= subjectClass.getMaxStudents()) {
            throw new AppException("Lớp học đã đầy (Max: " + subjectClass.getMaxStudents() + ")");
        }

        // 6. Create enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSubjectClass(subjectClass);
        
        return enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional
    public void cancelEnrollment(Long studentId, Long classId) {
        Student student = studentRepository.findById(studentId)
                .orElseGet(() -> studentRepository.findByUserId(studentId)
                .orElseThrow(() -> new AppException("Student not found")));

        Enrollment enrollment = enrollmentRepository.findByStudentStudentIdAndSubjectClassId(student.getStudentId(), classId)
                .orElseThrow(() -> new AppException("Enrollment not found"));
        
        // In theory, should check if semester is still open for registration
        enrollmentRepository.delete(enrollment);
    }

    @Override
    public List<Enrollment> getStudentEnrollments(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseGet(() -> studentRepository.findByUserId(studentId).orElse(null));
        
        if (student == null) return java.util.Collections.emptyList();
        return enrollmentRepository.findByStudentStudentId(student.getStudentId());
    }

    @Override
    public List<Enrollment> getClassEnrollments(Long classId) {
        return enrollmentRepository.findBySubjectClassId(classId);
    }

    @Override
    @Transactional
    public Enrollment updateGrades(Long enrollmentId, Double attendance, Double midterm, Double finalScore) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException("Enrollment not found"));

        if (enrollment.getSubjectClass().isLocked()) {
            throw new AppException("Điểm của lớp học này đã bị khóa, không thể chỉnh sửa thêm.");
        }

        if (attendance != null) enrollment.setAttendanceScore(attendance);
        if (midterm != null) enrollment.setMidtermScore(midterm);
        if (finalScore != null) enrollment.setFinalScore(finalScore);

        // Calculate total score (example weight: 10% attendance, 30% midterm, 60% final)
        if (enrollment.getAttendanceScore() != null && enrollment.getMidtermScore() != null && enrollment.getFinalScore() != null) {
            double total = (enrollment.getAttendanceScore() * 0.1) + 
                          (enrollment.getMidtermScore() * 0.3) + 
                          (enrollment.getFinalScore() * 0.6);
            enrollment.setTotalScore(total);
        }

        return enrollmentRepository.save(enrollment);
    }
}
