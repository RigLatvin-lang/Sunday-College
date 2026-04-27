package aur.diploma.backend.attendance.repository;

import aur.diploma.backend.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByLessonId(Long lessonId);

    List<Attendance> findByStudentId(Long studentId);

    Optional<Attendance> findByStudentIdAndLessonId(Long studentId, Long lessonId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.present = true " +
           "AND a.lesson.dateTime < :now")
    long countPresentByStudentId(@Param("studentId") Long studentId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId " +
           "AND a.lesson.dateTime < :now")
    long countTotalByStudentId(@Param("studentId") Long studentId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.present = false " +
           "AND a.lesson.dateTime < :now")
    long countAbsentByStudentId(@Param("studentId") Long studentId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.present = true " +
           "AND a.lesson.subject.id = :subjectId AND a.lesson.dateTime < :now")
    long countPresentByStudentIdAndSubjectId(@Param("studentId") Long studentId,
                                              @Param("subjectId") Long subjectId,
                                              @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId " +
           "AND a.lesson.subject.id = :subjectId AND a.lesson.dateTime < :now")
    long countTotalByStudentIdAndSubjectId(@Param("studentId") Long studentId,
                                            @Param("subjectId") Long subjectId,
                                            @Param("now") LocalDateTime now);

    @Query("SELECT a FROM Attendance a JOIN FETCH a.lesson l JOIN FETCH l.subject " +
           "WHERE a.student.id = :studentId AND l.dateTime < :now ORDER BY l.dateTime DESC")
    List<Attendance> findByStudentIdOrderByLessonDateTime(@Param("studentId") Long studentId, @Param("now") LocalDateTime now);

    void deleteAllByLessonId(Long lessonId);

    void deleteAllByLessonGroupId(Long groupId);
}
