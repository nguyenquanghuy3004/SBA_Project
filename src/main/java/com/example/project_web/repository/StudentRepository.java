package com.example.project_web.repository;

import com.example.project_web.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findById(Long studentId);
    Optional<Student> findByUserId(Long userId);
    
    boolean existsByStudentEmail(String email);
    boolean existsByStudentPhone(String phone);
    
    Optional<Student> findByStudentEmail(String email);
    Optional<Student> findByStudentPhone(String phone);
}
