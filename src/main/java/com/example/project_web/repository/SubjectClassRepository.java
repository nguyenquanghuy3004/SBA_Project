package com.example.project_web.repository;

import com.example.project_web.entity.SubjectClass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubjectClassRepository extends JpaRepository<SubjectClass, Long> {
    List<SubjectClass> findBySemesterId(Long semesterId);
    List<SubjectClass> findByTeacherId(Long teacherId);
}
