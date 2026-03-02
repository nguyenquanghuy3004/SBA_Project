package com.example.project_web.util;

import com.example.project_web.entity.Student;
import com.example.project_web.exception.AppException;
import com.example.project_web.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Component
public class StudentValidator {

    @Autowired
    private StudentRepository studentRepository;

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

        // Kiểm tra trùng lặp (Uniqueness check)
        // Kiểm tra Email
        studentRepository.findByStudentEmail(student.getStudentEmail()).ifPresent(existing -> {
            if (student.getStudentId() == null || student.getStudentId() == 0 || !existing.getStudentId().equals(student.getStudentId())) {
                throw new AppException("Email đã tồn tại trong hệ thống");
            }
        });

        // Kiểm tra Số điện thoại
        studentRepository.findByStudentPhone(student.getStudentPhone()).ifPresent(existing -> {
            if (student.getStudentId() == null || student.getStudentId() == 0 || !existing.getStudentId().equals(student.getStudentId())) {
                throw new AppException("Số điện thoại đã tồn tại trong hệ thống");
            }
        });
    }
}
