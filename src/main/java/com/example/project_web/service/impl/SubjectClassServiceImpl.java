package com.example.project_web.service.impl;

import com.example.project_web.entity.SubjectClass;
import com.example.project_web.entity.Teacher;
import com.example.project_web.repository.EnrollmentRepository;
import com.example.project_web.repository.SubjectClassRepository;
import com.example.project_web.repository.TeacherRepository;
import com.example.project_web.service.SubjectClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectClassServiceImpl implements SubjectClassService {

    @Autowired
    private SubjectClassRepository subjectClassRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public List<SubjectClass> getAllClasses() {
        return subjectClassRepository.findAll();
    }

    @Override
    public List<SubjectClass> getClassesBySemester(Long semesterId) {
        return subjectClassRepository.findBySemesterId(semesterId);
    }

    @Override
    public List<SubjectClass> getClassesByTeacher(Long userId) {
        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElse(null);
        if (teacher == null) return java.util.Collections.emptyList();
        return subjectClassRepository.findByTeacherId(teacher.getId());
    }

    @Override
    public SubjectClass createClass(SubjectClass subjectClass) {
        return subjectClassRepository.save(subjectClass);
    }

    @Override
    public SubjectClass updateClass(Long id, SubjectClass subjectClass) {
        if (subjectClassRepository.existsById(id)) {
            subjectClass.setId(id);
            return subjectClassRepository.save(subjectClass);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteClass(Long id) {
        // Xóa tất cả sinh viên đã đăng ký vào lớp này trước (tránh lỗi khóa ngoại)
        enrollmentRepository.deleteBySubjectClassId(id);
        // Sau đó mới xóa lớp
        subjectClassRepository.deleteById(id);
    }

    @Override
    public Optional<SubjectClass> getClassById(Long id) {
        return subjectClassRepository.findById(id);
    }

    @Override
    public void lockClass(Long id, boolean lock) {
        subjectClassRepository.findById(id).ifPresent(cls -> {
            cls.setLocked(lock);
            subjectClassRepository.save(cls);
        });
    }
}
