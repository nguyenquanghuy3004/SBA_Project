package com.example.project_web.service;

import com.example.project_web.entity.SubjectClass;
import java.util.List;
import java.util.Optional;

public interface SubjectClassService {
    List<SubjectClass> getAllClasses();
    List<SubjectClass> getClassesBySemester(Long semesterId);
    List<SubjectClass> getClassesByTeacher(Long userId);
    SubjectClass createClass(SubjectClass subjectClass);
    SubjectClass updateClass(Long id, SubjectClass subjectClass);
    void deleteClass(Long id);
    Optional<SubjectClass> getClassById(Long id);
    void lockClass(Long id, boolean lock);
}
