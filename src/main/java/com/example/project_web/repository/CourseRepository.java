package com.example.project_web.repository;

import com.example.project_web.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);
}
