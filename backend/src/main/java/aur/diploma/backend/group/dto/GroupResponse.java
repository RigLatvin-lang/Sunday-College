package aur.diploma.backend.group.dto;

import aur.diploma.backend.group.entity.StudentGroup;
import aur.diploma.backend.student.dto.StudentResponse;

import java.util.List;

public record GroupResponse(
        Long id,
        String name,
        List<StudentResponse> students
) {
    public static GroupResponse from(StudentGroup g) {
        List<StudentResponse> studentList = g.getStudents().stream()
                .map(StudentResponse::from)
                .toList();
        return new GroupResponse(g.getId(), g.getName(), studentList);
    }
}
