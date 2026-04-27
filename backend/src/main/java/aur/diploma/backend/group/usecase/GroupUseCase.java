package aur.diploma.backend.group.usecase;

import aur.diploma.backend.attendance.repository.AttendanceRepository;
import aur.diploma.backend.group.dto.AddStudentsRequest;
import aur.diploma.backend.group.dto.CreateGroupRequest;
import aur.diploma.backend.group.dto.GroupResponse;
import aur.diploma.backend.group.entity.StudentGroup;
import aur.diploma.backend.group.repository.StudentGroupRepository;
import aur.diploma.backend.lesson.repository.LessonRepository;
import aur.diploma.backend.student.entity.Student;
import aur.diploma.backend.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupUseCase {

    private final StudentGroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public GroupResponse create(CreateGroupRequest request) {
        StudentGroup group = StudentGroup.builder()
                .name(request.name())
                .students(new HashSet<>())
                .build();
        return GroupResponse.from(groupRepository.save(group));
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> getAll() {
        return groupRepository.findAll().stream().map(GroupResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public GroupResponse getById(Long id) {
        return GroupResponse.from(groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group not found")));
    }

    @Transactional
    public GroupResponse update(Long id, CreateGroupRequest request) {
        StudentGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        group.setName(request.name());
        return GroupResponse.from(groupRepository.save(group));
    }

    @Transactional
    public void delete(Long id) {
        StudentGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        attendanceRepository.deleteAllByLessonGroupId(id);
        lessonRepository.deleteAllByGroupId(id);
        group.getStudents().clear();
        groupRepository.delete(group);
    }

    @Transactional
    public GroupResponse addStudents(Long groupId, AddStudentsRequest request) {
        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        List<Student> students = studentRepository.findByIdIn(request.studentIds());

        if (students.size() != request.studentIds().size()) {
            List<Long> foundIds = students.stream().map(Student::getId).toList();
            List<Long> notFoundIds = request.studentIds().stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new IllegalArgumentException("Students not found: " + notFoundIds);
        }

        group.getStudents().addAll(students);
        return GroupResponse.from(groupRepository.save(group));
    }

    @Transactional
    public GroupResponse removeStudent(Long groupId, Long studentId) {
        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        boolean removed = group.getStudents().removeIf(s -> s.getId().equals(studentId));
        if (!removed) {
            throw new IllegalArgumentException("Student " + studentId + " is not in this group");
        }

        return GroupResponse.from(groupRepository.save(group));
    }
}
