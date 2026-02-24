package com.example.project_web.service.impl;

import com.example.project_web.entity.Role;
import com.example.project_web.entity.Teacher;
import com.example.project_web.entity.User;
import com.example.project_web.enums.RoleName;
import com.example.project_web.repository.RoleRepository;
import com.example.project_web.repository.TeacherRepository;
import com.example.project_web.repository.UserRepository;
import com.example.project_web.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    @Override
    public Optional<Teacher> getTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    @Override
    @Transactional
    public Teacher createTeacher(Teacher teacher, String username, String email, String password) {
        // 1. Check for duplicates
        if (userRepository.existsByUsername(username)) {
            throw new com.example.project_web.exception.AppException("Tài khoản (username) đã tồn tại");
        }
        if (userRepository.existsByEmail(email)) {
            throw new com.example.project_web.exception.AppException("Email đã tồn tại");
        }

        // 2. Create User account for teacher
        User user = new User(username, email, passwordEncoder.encode(password));
        Role teacherRole = roleRepository.findByName(RoleName.ROLE_TEACHER)
                .orElseThrow(() -> new RuntimeException("Error: Role TEACHER not found."));
        user.setRoles(new HashSet<>(Collections.singletonList(teacherRole)));
        user = userRepository.save(user);

        // 3. Link User to Teacher entity
        teacher.setUser(user);
        return teacherRepository.save(teacher);
    }

    @Override
    public Optional<Teacher> getTeacherByUserId(Long userId) {
        return teacherRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Teacher updateTeacher(Long id, Teacher teacherDetails) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));
        
        teacher.setFullName(teacherDetails.getFullName());
        teacher.setDepartment(teacherDetails.getDepartment());
        teacher.setDegree(teacherDetails.getDegree());
        
        return teacherRepository.save(teacher);
    }

    @Override
    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElse(null);
        if (teacher != null) {
            User user = teacher.getUser();
            teacherRepository.delete(teacher);
            if (user != null) {
                userRepository.delete(user);
            }
        }
    }
}
