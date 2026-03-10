package com.example.project_web.service.impl;

import com.example.project_web.config.JwtProvider;
import com.example.project_web.dto.JwtResponse;
import com.example.project_web.dto.LoginRequest;
import com.example.project_web.dto.RegisterRequest;
import com.example.project_web.entity.Role;
import com.example.project_web.entity.User;
import com.example.project_web.enums.RoleName;
import com.example.project_web.entity.Student;
import com.example.project_web.repository.RoleRepository;
import com.example.project_web.repository.StudentRepository;
import com.example.project_web.repository.UserRepository;
import com.example.project_web.service.AuthService;
import com.example.project_web.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentService studentService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtUtils;

    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // Get user from DB to get Email and ID
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Error: User not found in database after authentication."));
            
            return ResponseEntity.ok(new JwtResponse(jwt,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    roles));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Error: Invalid username or password!");
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            return ResponseEntity.status(500).body("Internal Login Error: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> registerUser(RegisterRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // Kiểm tra thêm trùng lặp trong bảng Student
        if (studentRepository.existsByStudentEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: This email is already assigned to another student!");
        }

        if (studentRepository.existsByStudentPhone(signUpRequest.getPhone())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: This phone number is already assigned to another student!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(RoleName.STUDENT)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "teacher":
                        Role modRole = roleRepository.findByName(RoleName.TEACHER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(RoleName.STUDENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        // If the registered user is a student, create a Student profile
        if (roles.stream().anyMatch(r -> r.getName().equals(RoleName.STUDENT))) {
            Student student = new Student();
            student.setUser(savedUser);
            student.setStudentName(savedUser.getUsername());
            student.setStudentEmail(savedUser.getEmail());
            student.setGender(signUpRequest.getGender()); // Lấy giới tính từ form đăng ký
            
            // Xử lý ngày sinh bảo mật hơn
            if (signUpRequest.getDateOfBirth() != null && !signUpRequest.getDateOfBirth().isEmpty()) {
                student.setDateOfBirth(LocalDate.parse(signUpRequest.getDateOfBirth()));
            } else {
                student.setDateOfBirth(LocalDate.of(2000, 1, 1)); // Default
            }

            student.setAddress(signUpRequest.getAddress()); // Lấy quê quán
            student.setStudentPhone(signUpRequest.getPhone()); 
            student.setMajor("None"); // Default
            student.setGpa(0.0);
            studentService.createStudent(student);
        }

        return ResponseEntity.ok("User registered successfully!");
    }
}
