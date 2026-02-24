package com.example.project_web.controller;

import com.example.project_web.entity.Semester;
import com.example.project_web.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semesters")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5174", "http://localhost:5173"}, maxAge = 3600)
public class SemesterController {

    @Autowired
    private SemesterRepository semesterRepository;

    @GetMapping
    public ResponseEntity<List<Semester>> getAll() {
        return ResponseEntity.ok(semesterRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Semester> create(@RequestBody Semester semester) {
        return ResponseEntity.ok(semesterRepository.save(semester));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Semester> update(@PathVariable Long id, @RequestBody Semester semester) {
        semester.setId(id);
        return ResponseEntity.ok(semesterRepository.save(semester));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        semesterRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
