package aur.diploma.backend.group.repository;

import aur.diploma.backend.group.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {

    Optional<StudentGroup> findByName(String name);

    /** Группы, в которые входит данный ученик. */
    @Query("SELECT g FROM StudentGroup g JOIN g.students s WHERE s.id = :studentId")
    List<StudentGroup> findByStudentId(@Param("studentId") Long studentId);
}
