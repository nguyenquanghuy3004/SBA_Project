package com.example.project_web.controller;

import com.example.project_web.entity.Student;
import com.example.project_web.service.StudentService;
import com.example.project_web.util.StudentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


    @RestController
    @RequestMapping("/api/student")
    @CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
    public class StudentController {

        @Autowired
        private StudentService studentService;

        @Autowired
        private StudentValidator studentValidator;

        @Autowired
        private com.example.project_web.repository.UserRepository userRepository;

        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        public ResponseEntity<List<Student>> getAllStudents() {
            return ResponseEntity.ok(studentService.getAllStudents());
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
            return studentService.getStudentById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }


        @PostMapping("/create")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
        public ResponseEntity<Student> createStudent(@RequestBody Student student) {
            studentValidator.validate(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(student));
        }


        @PutMapping("/update/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
        public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Student student, org.springframework.security.core.Authentication authentication) {
            student.setStudentId(id); // Set ID so Validator knows this is an update
            studentValidator.validate(student);

            // Check if requester is a STUDENT
            boolean isStudent = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

            if (isStudent) {
                // Verify ownership by checking if User email matches Student email
                org.springframework.security.core.userdetails.UserDetails userDetails = 
                        (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
                
                com.example.project_web.entity.User currentUser = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
                java.util.Optional<Student> existingStudentOpt = studentService.getStudentById(id);

                if (currentUser == null || existingStudentOpt.isEmpty() || 
                    !currentUser.getEmail().equals(existingStudentOpt.get().getStudentEmail())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to update this profile.");
                }
            }

            return ResponseEntity.ok(studentService.updateStudent(id, student));
        }


        @DeleteMapping("/delete/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
            studentService.deleteStudent(id);
            return ResponseEntity.noContent().build();
        }
    }
