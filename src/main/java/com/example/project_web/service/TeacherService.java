package com.example.project_web.service;

import com.example.project_web.entity.Teacher;
import java.util.List;
import java.util.Optional;

public interface TeacherService {
    List<Teacher> getAllTeachers();
    Optional<Teacher> getTeacherById(Long id);
    Teacher createTeacher(Teacher teacher, String username, String email, String password);
    Optional<Teacher> getTeacherByUserId(Long userId);
    Teacher updateTeacher(Long id, Teacher teacherDetails);
    void deleteTeacher(Long id);
}
