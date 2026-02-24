package com.example.project_web.controller;

import com.example.project_web.entity.SubjectClass;
import com.example.project_web.service.SubjectClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5174", "http://localhost:5173"}, maxAge = 3600)
public class SubjectClassController {

    @Autowired
    private SubjectClassService subjectClassService;

    @GetMapping
    public ResponseEntity<List<SubjectClass>> getAllClasses() {
        return ResponseEntity.ok(subjectClassService.getAllClasses());
    }

    @GetMapping("/semester/{semesterId}")
    public ResponseEntity<List<SubjectClass>> getBySemester(@PathVariable Long semesterId) {
        return ResponseEntity.ok(subjectClassService.getClassesBySemester(semesterId));
    }

    @GetMapping("/teacher/{userId}")
    public ResponseEntity<List<SubjectClass>> getByTeacher(@PathVariable Long userId) {
        return ResponseEntity.ok(subjectClassService.getClassesByTeacher(userId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectClass> createClass(@RequestBody SubjectClass subjectClass) {
        return ResponseEntity.ok(subjectClassService.createClass(subjectClass));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectClass> updateClass(@PathVariable Long id, @RequestBody SubjectClass subjectClass) {
        return ResponseEntity.ok(subjectClassService.updateClass(id, subjectClass));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        subjectClassService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/lock")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Void> lockClass(@PathVariable Long id, @RequestParam boolean lock) {
        subjectClassService.lockClass(id, lock);
        return ResponseEntity.ok().build();
    }
}
