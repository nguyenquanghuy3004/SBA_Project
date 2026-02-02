package com.example.project_web.service;

import com.example.project_web.entity.Student;
import com.example.project_web.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents(){
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id){
        return studentRepository.findById(id);
    }

    public Student createStudent(Student students){
        return studentRepository.save(students);
    }


    public Student updateStudent(Long id, Student studentDetails) {
        Student st = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found for id: " + id));
        st.setStudentName(studentDetails.getStudentName());
        st.setGender(studentDetails.getGender());
        st.setDateOfBirth(studentDetails.getDateOfBirth());
        st.setStudentEmail(studentDetails.getStudentEmail());
        st.setStudentPhone(studentDetails.getStudentPhone());
        st.setMajor(studentDetails.getMajor());
        st.setGpa(studentDetails.getGpa());
        return studentRepository.save(st);
    }
    public void deleteStudent(Long id) {
        Student st = studentRepository.findById(id).get();
        studentRepository.delete(st);
    }



}