package aur.diploma.backend.student.repository;

import aur.diploma.backend.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByIdIn(List<Long> ids);

    Optional<Student> findByUserId(Long userId);
}
