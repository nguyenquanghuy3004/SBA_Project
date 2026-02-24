package com.example.project_web.controller;

import com.example.project_web.entity.Course;
import com.example.project_web.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5174", "http://localhost:5173"}, maxAge = 3600)
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping
    public ResponseEntity<List<Course>> getAll() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> create(@RequestBody Course course) {
        return ResponseEntity.ok(courseRepository.save(course));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> update(@PathVariable Long id, @RequestBody Course courseDetails) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setCourseCode(courseDetails.getCourseCode());
                    course.setName(courseDetails.getName());
                    course.setCreadits(courseDetails.getCreadits());
                    return ResponseEntity.ok(courseRepository.save(course));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
