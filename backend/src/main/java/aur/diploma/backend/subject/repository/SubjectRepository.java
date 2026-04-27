package aur.diploma.backend.subject.repository;

import aur.diploma.backend.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
