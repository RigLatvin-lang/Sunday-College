package aur.diploma.backend.lesson.repository;

import aur.diploma.backend.lesson.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("SELECT l FROM Lesson l JOIN FETCH l.subject JOIN FETCH l.group LEFT JOIN FETCH l.teacher " +
           "WHERE l.dateTime >= :from AND l.dateTime <= :to ORDER BY l.dateTime")
    List<Lesson> findByDateTimeBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT l FROM Lesson l JOIN FETCH l.subject JOIN FETCH l.group LEFT JOIN FETCH l.teacher " +
           "WHERE l.group.id = :groupId AND l.dateTime >= :from AND l.dateTime <= :to ORDER BY l.dateTime")
    List<Lesson> findByGroupIdAndDateTimeBetween(@Param("groupId") Long groupId,
                                                  @Param("from") LocalDateTime from,
                                                  @Param("to") LocalDateTime to);

    @Query("SELECT l FROM Lesson l JOIN FETCH l.subject JOIN FETCH l.group LEFT JOIN FETCH l.teacher " +
           "WHERE l.teacher.id = :teacherId AND l.dateTime >= :from AND l.dateTime <= :to ORDER BY l.dateTime")
    List<Lesson> findByTeacherIdAndDateTimeBetween(@Param("teacherId") Long teacherId,
                                                    @Param("from") LocalDateTime from,
                                                    @Param("to") LocalDateTime to);

    @Query("SELECT l FROM Lesson l JOIN FETCH l.subject JOIN FETCH l.group LEFT JOIN FETCH l.teacher " +
           "WHERE l.group.id = :groupId AND l.teacher.id = :teacherId AND l.dateTime >= :from AND l.dateTime <= :to ORDER BY l.dateTime")
    List<Lesson> findByGroupIdAndTeacherIdAndDateTimeBetween(@Param("groupId") Long groupId,
                                                             @Param("teacherId") Long teacherId,
                                                             @Param("from") LocalDateTime from,
                                                             @Param("to") LocalDateTime to);

    @Query("SELECT l FROM Lesson l JOIN FETCH l.subject JOIN FETCH l.group LEFT JOIN FETCH l.teacher " +
           "WHERE l.dateTime >= :now ORDER BY l.dateTime")
    List<Lesson> findUpcoming(@Param("now") LocalDateTime now);

    @Query("SELECT l FROM Lesson l JOIN FETCH l.subject JOIN FETCH l.group LEFT JOIN FETCH l.teacher " +
           "WHERE l.teacher.id = :teacherId AND l.dateTime >= :now ORDER BY l.dateTime")
    List<Lesson> findUpcomingByTeacherId(@Param("teacherId") Long teacherId, @Param("now") LocalDateTime now);

    @Query("SELECT l FROM Lesson l JOIN FETCH l.subject JOIN FETCH l.group LEFT JOIN FETCH l.teacher " +
           "ORDER BY l.dateTime")
    List<Lesson> findAllWithDetails();

    void deleteAllByGroupId(Long groupId);
}
