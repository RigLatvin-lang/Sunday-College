package aur.diploma.backend.subject.usecase;

import aur.diploma.backend.subject.dto.CreateSubjectRequest;
import aur.diploma.backend.subject.dto.SubjectResponse;
import aur.diploma.backend.subject.entity.Subject;
import aur.diploma.backend.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectUseCase {

    private final SubjectRepository subjectRepository;

    public SubjectResponse create(CreateSubjectRequest request) {
        Subject subject = Subject.builder().name(request.name()).build();
        return SubjectResponse.from(subjectRepository.save(subject));
    }

    public List<SubjectResponse> getAll() {
        return subjectRepository.findAll().stream().map(SubjectResponse::from).toList();
    }

    public SubjectResponse getById(Long id) {
        return SubjectResponse.from(subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found")));
    }

    public SubjectResponse update(Long id, CreateSubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        subject.setName(request.name());
        return SubjectResponse.from(subjectRepository.save(subject));
    }

    public void delete(Long id) {
        subjectRepository.deleteById(id);
    }
}
