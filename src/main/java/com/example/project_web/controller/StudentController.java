package com.example.project_web.controller;

import com.example.project_web.entity.Student;
import com.example.project_web.entity.User;
import com.example.project_web.repository.UserRepository;
import com.example.project_web.service.StudentService;
import com.example.project_web.util.StudentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


    @RestController
    @RequestMapping("/api/student")
    @CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5174", "http://localhost:5173"}, maxAge = 3600)
    public class StudentController {

        @Autowired
        private StudentService studentService;

        @Autowired
        private StudentValidator studentValidator;

        @Autowired
        private UserRepository userRepository;

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


        @GetMapping("/me")
        @PreAuthorize("hasRole('STUDENT')")
        public ResponseEntity<Student> getMyProfile(Authentication authentication) {
            String currentUsername = authentication.getName();
            
            User currentUser = userRepository.findByUsername(currentUsername)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            return studentService.getStudentByUserId(currentUser.getId())
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        @PutMapping("/update/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
        public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Student student, Authentication authentication) {
            
            // BƯỚC 1: Tìm sinh viên cũ trong database
            Optional<Student> studentOpt = studentService.getStudentById(id);
            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy sinh viên ID: " + id);
            }
            Student existingStudent = studentOpt.get();

            // BƯỚC 2: Kiểm tra quyền chỉnh sửa
            String authorities = authentication.getAuthorities().toString();
            String currentUsername = authentication.getName();

            // Nếu là SINH VIÊN (và không phải Admin/Teacher)
            if (authorities.contains("ROLE_STUDENT") && !authorities.contains("ROLE_ADMIN") && !authorities.contains("ROLE_TEACHER")) {
                
                // Kiểm tra xem sinh viên này có đang sửa "chính mình" không?
                if (existingStudent.getUser() == null || !existingStudent.getUser().getUsername().equals(currentUsername)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn chỉ có quyền chỉnh sửa hồ sơ của chính mình!");
                }

                // Hạn chế: Sinh viên không được tự sửa Điểm (GPA) và Tên (nếu đã có)
                student.setGpa(existingStudent.getGpa());
                student.setStudentName(existingStudent.getStudentName());
                
                // Cho phép sửa Chuyên ngành và Lớp NẾU hiện tại đang là "None" hoặc trống
                if (existingStudent.getMajor() != null && !existingStudent.getMajor().equalsIgnoreCase("None") 
                    && !existingStudent.getMajor().equalsIgnoreCase("Chưa cập nhật")) {
                    student.setMajor(existingStudent.getMajor());
                }
                
                if (existingStudent.getStudentClass() != null && !existingStudent.getStudentClass().equalsIgnoreCase("None")
                    && !existingStudent.getStudentClass().equalsIgnoreCase("Chưa cập nhật")
                    && !existingStudent.getStudentClass().isEmpty()) {
                    student.setStudentClass(existingStudent.getStudentClass());
                }
            }

            // BƯỚC 3: Validate và lưu dữ liệu
            student.setStudentId(id);
            studentValidator.validate(student);
            
            Student updatedStudent = studentService.updateStudent(id, student);
            return ResponseEntity.ok(updatedStudent);
        }


        @DeleteMapping("/delete/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
            studentService.deleteStudent(id);
            return ResponseEntity.noContent().build();
        }

        // Chức năng khác

    }
