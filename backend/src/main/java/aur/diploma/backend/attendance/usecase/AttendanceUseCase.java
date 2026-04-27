package aur.diploma.backend.attendance.usecase;

import aur.diploma.backend.attendance.dto.*;
import aur.diploma.backend.attendance.entity.Attendance;
import aur.diploma.backend.attendance.repository.AttendanceRepository;
import aur.diploma.backend.lesson.dto.LessonResponse;
import aur.diploma.backend.lesson.entity.Lesson;
import aur.diploma.backend.lesson.repository.LessonRepository;
import aur.diploma.backend.student.dto.StudentResponse;
import aur.diploma.backend.student.entity.Student;
import aur.diploma.backend.student.repository.StudentRepository;
import aur.diploma.backend.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceUseCase {

    private final AttendanceRepository attendanceRepository;
    private final LessonRepository lessonRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public List<AttendanceResponse> markAttendance(Long lessonId, BulkAttendanceRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        List<AttendanceResponse> results = new ArrayList<>();
        for (MarkAttendanceRequest record : request.records()) {
            Student student = studentRepository.findById(record.studentId())
                    .orElseThrow(() -> new IllegalArgumentException("Student not found: " + record.studentId()));

            Attendance attendance = attendanceRepository.findByStudentIdAndLessonId(record.studentId(), lessonId)
                    .orElse(Attendance.builder()
                            .student(student)
                            .lesson(lesson)
                            .build());
            attendance.setPresent(record.present());
            results.add(AttendanceResponse.from(attendanceRepository.save(attendance)));
        }
        return results;
    }

    @Transactional(readOnly = true)
    public LessonAttendanceSummary getLessonAttendance(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        List<Attendance> records = attendanceRepository.findByLessonId(lessonId);
        Map<Long, Attendance> attendanceByStudentId = records.stream()
                .collect(Collectors.toMap(a -> a.getStudent().getId(), a -> a));

        // Build response for ALL students in the group, not just those with records
        List<AttendanceResponse> allStudentRecords = lesson.getGroup().getStudents().stream()
                .map(student -> {
                    Attendance existing = attendanceByStudentId.get(student.getId());
                    if (existing != null) {
                        return AttendanceResponse.from(existing);
                    }
                    String name = student.getLastName() + " " + student.getFirstName();
                    return new AttendanceResponse(null, student.getId(), name, lessonId, false);
                })
                .toList();

        long totalStudents = allStudentRecords.size();
        long presentCount = allStudentRecords.stream().filter(AttendanceResponse::present).count();
        double percentage = totalStudents > 0 ? (double) presentCount / totalStudents * 100 : 0;

        return new LessonAttendanceSummary(
                LessonResponse.from(lesson),
                totalStudents,
                presentCount,
                Math.round(percentage * 100.0) / 100.0,
                allStudentRecords
        );
    }

    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentProfile(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        LocalDateTime now = LocalDateTime.now();

        long total = attendanceRepository.countTotalByStudentId(studentId, now);
        long present = attendanceRepository.countPresentByStudentId(studentId, now);
        long absent = attendanceRepository.countAbsentByStudentId(studentId, now);
        double percentage = total > 0 ? (double) present / total * 100 : 0;

        List<Attendance> allRecords = attendanceRepository.findByStudentIdOrderByLessonDateTime(studentId, now);
        int streak = calculateStreak(allRecords, now);

        AttendanceStatsResponse stats = new AttendanceStatsResponse(
                total, present, absent,
                Math.round(percentage * 100.0) / 100.0,
                streak
        );

        List<SubjectAttendanceResponse> subjectStats = subjectRepository.findAll().stream()
                .map(subject -> {
                    long subTotal = attendanceRepository.countTotalByStudentIdAndSubjectId(studentId, subject.getId(), now);
                    if (subTotal == 0) return null;
                    long subPresent = attendanceRepository.countPresentByStudentIdAndSubjectId(studentId, subject.getId(), now);
                    double subPerc = (double) subPresent / subTotal * 100;
                    return new SubjectAttendanceResponse(
                            subject.getId(), subject.getName(), subTotal, subPresent,
                            Math.round(subPerc * 100.0) / 100.0
                    );
                })
                .filter(s -> s != null)
                .toList();

        List<AttendanceRecordResponse> recent = allRecords.stream()
                .limit(20)
                .map(a -> new AttendanceRecordResponse(
                        a.getLesson().getSubject().getName(),
                        a.getLesson().getDateTime(),
                        a.getPresent()
                ))
                .toList();

        return new StudentProfileResponse(StudentResponse.from(student), stats, subjectStats, recent);
    }

    private int calculateStreak(List<Attendance> records, LocalDateTime now) {
        int streak = 0;
        for (Attendance a : records) {
            if (a.getPresent()) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }
}
