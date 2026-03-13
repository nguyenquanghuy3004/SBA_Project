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

import com.example.project_web.exception.AppException;

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
        if (subjectClass.getCourse() == null || subjectClass.getCourse().getId() == null ||
            subjectClass.getSemester() == null || subjectClass.getSemester().getId() == null ||
            subjectClass.getTeacher() == null || subjectClass.getTeacher().getId() == null) {
            throw new AppException("Thiếu thông tin Môn học, Học kỳ hoặc Giáo viên");
        }

        boolean exists = subjectClassRepository.existsByCourseIdAndSemesterIdAndTeacherId(
                subjectClass.getCourse().getId(),
                subjectClass.getSemester().getId(),
                subjectClass.getTeacher().getId()
        );

        if (exists) {
            throw new AppException("Môn học này đã được giao cho giáo viên này trong học kỳ hiện tại!");
        }

        return subjectClassRepository.save(subjectClass);
    }

    @Override
    public SubjectClass updateClass(Long id, SubjectClass subjectClass) {
        SubjectClass existing = subjectClassRepository.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy lớp học với ID: " + id));

        if (subjectClass.getCourse() == null || subjectClass.getCourse().getId() == null ||
            subjectClass.getSemester() == null || subjectClass.getSemester().getId() == null ||
            subjectClass.getTeacher() == null || subjectClass.getTeacher().getId() == null) {
            throw new AppException("Dữ liệu cập nhật thiếu thông tin bắt buộc (Môn học/Học kỳ/Giáo viên)");
        }

        // Nếu thay đổi Course, Semester hoặc Teacher, cần kiểm tra trùng lặp (trừ chính nó)
        boolean infoChanged = !existing.getCourse().getId().equals(subjectClass.getCourse().getId()) ||
                              !existing.getSemester().getId().equals(subjectClass.getSemester().getId()) ||
                              !existing.getTeacher().getId().equals(subjectClass.getTeacher().getId());

        if (infoChanged) {
            boolean exists = subjectClassRepository.existsByCourseIdAndSemesterIdAndTeacherId(
                    subjectClass.getCourse().getId(),
                    subjectClass.getSemester().getId(),
                    subjectClass.getTeacher().getId()
            );
            if (exists) {
                throw new AppException("Phân công này đã tồn tại (Môn học - Giáo viên - Học kỳ)");
            }
        }

        subjectClass.setId(id);
        return subjectClassRepository.save(subjectClass);
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
