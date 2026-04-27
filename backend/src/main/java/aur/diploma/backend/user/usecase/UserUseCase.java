package aur.diploma.backend.user.usecase;

import aur.diploma.backend.user.dto.CreateAdminRequest;
import aur.diploma.backend.user.dto.CreateTeacherRequest;
import aur.diploma.backend.user.dto.LoginRequest;
import aur.diploma.backend.user.dto.LoginResponse;
import aur.diploma.backend.user.dto.TeacherInfo;
import aur.diploma.backend.user.dto.TeacherResponse;
import aur.diploma.backend.user.dto.UpdateTeacherRequest;
import aur.diploma.backend.user.dto.UserProfileResponse;
import aur.diploma.backend.user.entity.Role;
import aur.diploma.backend.user.entity.User;
import aur.diploma.backend.user.repository.UserRepository;
import aur.diploma.backend.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${app.admin.security-key}")
    private String adminSecurityKey;

    public void createAdmin(String securityKey, CreateAdminRequest request) {
        if (!adminSecurityKey.equals(securityKey)) {
            throw new IllegalArgumentException("Invalid security key");
        }
        if (userRepository.existsByLogin(request.login())) {
            throw new IllegalArgumentException("Login already exists");
        }
        User admin = User.builder()
                .fullName("Administrator")
                .login(request.login())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.login(), request.password()));
        User user = userRepository.findByLogin(request.login())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String token = jwtService.generateToken(user);
        return new LoginResponse(token, user.getRole().name(), user.getFullName());
    }

    public TeacherResponse createTeacher(CreateTeacherRequest request) {
        if (userRepository.existsByLogin(request.login())) {
            throw new IllegalArgumentException("Login already exists");
        }
        User teacher = User.builder()
                .fullName(request.fullName())
                .login(request.login())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.TEACHER)
                .build();
        userRepository.save(teacher);
        return new TeacherResponse(teacher.getId(), teacher.getFullName(), teacher.getLogin(), request.password());
    }

    public List<TeacherInfo> getAllTeachers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.TEACHER)
                .map(TeacherInfo::from)
                .toList();
    }

    public TeacherInfo getTeacherById(Long id) {
        User teacher = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        if (teacher.getRole() != Role.TEACHER) {
            throw new IllegalArgumentException("User is not a teacher");
        }
        return TeacherInfo.from(teacher);
    }

    public TeacherInfo updateTeacher(Long id, UpdateTeacherRequest request) {
        User teacher = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        if (teacher.getRole() != Role.TEACHER) {
            throw new IllegalArgumentException("User is not a teacher");
        }

        teacher.setFullName(request.fullName());

        if (request.login() != null && !request.login().equals(teacher.getLogin())) {
            if (userRepository.existsByLogin(request.login())) {
                throw new IllegalArgumentException("Login already exists");
            }
            teacher.setLogin(request.login());
        }

        if (request.password() != null && !request.password().isBlank()) {
            teacher.setPassword(passwordEncoder.encode(request.password()));
        }

        return TeacherInfo.from(userRepository.save(teacher));
    }

    public void deleteTeacher(Long id) {
        User teacher = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        if (teacher.getRole() != Role.TEACHER) {
            throw new IllegalArgumentException("User is not a teacher");
        }
        userRepository.deleteById(id);
    }

    public UserProfileResponse getCurrentUserProfile(User user) {
        return UserProfileResponse.from(user);
    }
}
