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
import java.util.Random;

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

    public Student createStudent(Student student) {
        // Generate MSSV if it's a new student creation
        if (student.getMssv() == null || student.getMssv().isEmpty()) {
            student.setMssv(generateMssv(student.getMajor()));
        }
        return studentRepository.save(student);
    }

    private String generateMssv(String major) {
        String prefix = getPrefixByMajor(major);
        Random random = new Random();
        String mssv;
        do {
            int number = random.nextInt(1000000);
            mssv = prefix + String.format("%06d", number);
        } while (studentRepository.existsByMssv(mssv));
        return mssv;
    }

    private String getPrefixByMajor(String major) {
        if (major == null) return "HE";
        String m = major.toLowerCase();
        if (m.contains("công nghệ thông tin")) return "HE";
        if (m.contains("phần mềm")) return "SE";
        if (m.contains("trí tuệ nhân tạo")) return "AI";
        if (m.contains("an toàn thông tin")) return "IA";
        if (m.contains("thiết kế đồ họa")) return "GD";
        if (m.contains("kinh doanh quốc tế")) return "IB";
        if (m.contains("khách sạn")) return "HM";
        if (m.contains("ngôn ngữ anh")) return "EN";
        if (m.contains("ngôn ngữ nhật")) return "JP";
        return "HE"; // Default prefix
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