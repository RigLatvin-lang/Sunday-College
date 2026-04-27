package aur.diploma.backend.student.usecase;

import aur.diploma.backend.student.dto.CreateStudentRequest;
import aur.diploma.backend.student.dto.StudentResponse;
import aur.diploma.backend.student.entity.Student;
import aur.diploma.backend.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentUseCase {

    private final StudentRepository studentRepository;

    public StudentResponse create(CreateStudentRequest request) {
        Student student = Student.builder()
                .lastName(request.lastName())
                .firstName(request.firstName())
                .middleName(request.middleName())
                .birthDate(request.birthDate())
                .inn(request.inn())
                .phone(request.phone())
                .build();
        return StudentResponse.from(studentRepository.save(student));
    }

    public List<StudentResponse> getAll() {
        return studentRepository.findAll().stream().map(StudentResponse::from).toList();
    }

    public StudentResponse getById(Long id) {
        return StudentResponse.from(studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found")));
    }

    public StudentResponse update(Long id, CreateStudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        student.setLastName(request.lastName());
        student.setFirstName(request.firstName());
        student.setMiddleName(request.middleName());
        student.setBirthDate(request.birthDate());
        student.setInn(request.inn());
        student.setPhone(request.phone());
        return StudentResponse.from(studentRepository.save(student));
    }

    public void delete(Long id) {
        studentRepository.deleteById(id);
    }
}
