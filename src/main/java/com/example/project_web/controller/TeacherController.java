package com.example.project_web.controller;

import com.example.project_web.dto.TeacherCreateRequest;
import com.example.project_web.entity.Teacher;
import com.example.project_web.service.TeacherService;
import com.example.project_web.entity.User;
import com.example.project_web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5174", "http://localhost:5173"}, maxAge = 3600)
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Teacher> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // Lấy thông tin user hiện tại từ username trong token
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Tìm giáo viên tương ứng với user đó
        return teacherService.getTeacherByUserId(currentUser.getId())
                .map(teacher -> ResponseEntity.ok(teacher))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update-me")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Teacher> updateMyProfile(@RequestBody Teacher teacher, @AuthenticationPrincipal UserDetails userDetails) {
        // Lấy thông tin user hiện tại
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Tìm thông tin giáo viên hiện tại
        Teacher existing = teacherService.getTeacherByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));
        
        // Cập nhật và trả về kết quả
        Teacher updated = teacherService.updateTeacher(existing.getId(), teacher);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTeacher(@RequestBody TeacherCreateRequest request) {
        Teacher teacher = new Teacher();
        teacher.setFullName(request.getFullName());
        teacher.setDepartment(request.getDepartment());
        teacher.setDegree(request.getDegree());

        Teacher created = teacherService.createTeacher(
                teacher, 
                request.getUsername(), 
                request.getEmail(), 
                request.getPassword()
        );

        return ResponseEntity.ok(created);
    }
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacherDetails){
        Teacher updateTeacher = teacherService.updateTeacher(id, teacherDetails);
        if(updateTeacher != null){
            return ResponseEntity.ok(updateTeacher);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.ok().build();
    }
}
