package com.example.project_web.util;

import com.example.project_web.entity.Student;
import com.example.project_web.exception.AppException;
import com.example.project_web.repository.StudentRepository;
import com.example.project_web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Component
public class StudentValidator {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    public void validate(Student student) {
        if (student.getStudentName() == null || student.getStudentName().trim().isEmpty()) {
            throw new AppException("Tên sinh viên không được để trống");
        }

        if (student.getStudentEmail() != null && !student.getStudentEmail().trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(student.getStudentEmail()).matches()) {
                throw new AppException("Email không hợp lệ");
            }
        }

        if (student.getStudentPhone() != null && !student.getStudentPhone().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(student.getStudentPhone()).matches()) {
                throw new AppException("Số điện thoại phải có đúng 10 chữ số (0-9)");
            }
        }

        if (student.getGpa() == null || student.getGpa() < 0 || student.getGpa() > 10) {
            throw new AppException("GPA phải nằm trong khoảng từ 0 đến 10");
        }

        if (student.getDateOfBirth() != null && student.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new AppException("Ngày sinh không được ở tương lai");
        }

        // Kiểm tra trùng lặp Email
        if (student.getStudentEmail() != null && !student.getStudentEmail().trim().isEmpty()) {
            String trimmedEmail = student.getStudentEmail().trim();
            
            // 1. Kiểm tra trong bảng Student
            studentRepository.findByStudentEmail(trimmedEmail).ifPresent(existing -> {
                if (student.getStudentId() == null || !existing.getStudentId().equals(student.getStudentId())) {
                    throw new AppException("Email sinh viên này đã tồn tại trong danh sách sinh viên");
                }
            });

            // 2. Kiểm tra trong bảng User (để tránh trùng với Giáo viên/Admin)
            // Chỉ kiểm tra nếu sinh viên này chưa có User hoặc Email mới khác Email của User hiện tại
            if (userRepository.existsByEmail(trimmedEmail)) {
                // Nếu là update, cần kiểm tra xem email này có phải là của CHÍNH User đó không
                boolean isOwnEmail = false;
                if (student.getUser() != null && trimmedEmail.equalsIgnoreCase(student.getUser().getEmail())) {
                    isOwnEmail = true;
                }
                
                if (!isOwnEmail) {
                    throw new AppException("Email này đã được sử dụng bởi một tài khoản khác trong hệ thống");
                }
            }
        }

        // Kiểm tra trùng lặp Số điện thoại
        if (student.getStudentPhone() != null && !student.getStudentPhone().trim().isEmpty()) {
            String trimmedPhone = student.getStudentPhone().trim();
            studentRepository.findByStudentPhone(trimmedPhone).ifPresent(existing -> {
                if (student.getStudentId() == null || !existing.getStudentId().equals(student.getStudentId())) {
                    throw new AppException("Số điện thoại này đã tồn tại trong hệ thống");
                }
            });
        }
    }
}
