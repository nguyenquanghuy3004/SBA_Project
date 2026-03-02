package com.example.project_web.service;

import com.example.project_web.entity.Student;
import com.example.project_web.entity.User;
import com.example.project_web.repository.EnrollmentRepository;
import com.example.project_web.repository.StudentRepository;
import com.example.project_web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Student> getAllStudents(){
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id){
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByUserId(Long userId){
        return studentRepository.findByUserId(userId);
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
        st.setAddress(studentDetails.getAddress());
        st.setStudentClass(studentDetails.getStudentClass());
        st.setMajor(studentDetails.getMajor());
        st.setGpa(studentDetails.getGpa());
        return studentRepository.save(st);
    }
    @Transactional
    public void deleteStudent(Long id) {
        Student st = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        // 1. Xóa tất cả đăng ký môn học (enrollments) của sinh viên này trước
        enrollmentRepository.deleteByStudentStudentId(st.getStudentId());

        // 2. Lưu lại user để xóa sau khi xóa student (vì student có FK tới user)
        User user = st.getUser();

        // 3. Xóa sinh viên
        studentRepository.delete(st);

        // 4. Xóa tài khoản user liên quan (nếu có)
        if (user != null) {
            userRepository.delete(user);
        }
    }
}