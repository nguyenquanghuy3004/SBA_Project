package com.example.project_web.repository;

import com.example.project_web.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentStudentId(Long studentId);
    List<Enrollment> findBySubjectClassId(Long classId);
    Optional<Enrollment> findByStudentStudentIdAndSubjectClassId(Long studentId, Long classId);
    boolean existsByStudentStudentIdAndSubjectClassId(Long studentId, Long classId);
    boolean existsByStudentStudentIdAndSubjectClassCourseIdAndSubjectClassSemesterId(Long studentId, Long courseId, Long semesterId);
    void deleteBySubjectClassId(Long classId);
    void deleteByStudentStudentId(Long studentId);
}
