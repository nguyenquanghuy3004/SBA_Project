package com.example.project_web.repository;

import com.example.project_web.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SemesterRepository extends JpaRepository<Semester, Long> {
    List<Semester> findByStatus(String status);
}
