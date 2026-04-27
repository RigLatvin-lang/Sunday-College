package aur.diploma.backend.subject.dto;

import aur.diploma.backend.subject.entity.Subject;

public record SubjectResponse(Long id, String name) {
    public static SubjectResponse from(Subject s) {
        return new SubjectResponse(s.getId(), s.getName());
    }
}
