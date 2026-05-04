package aur.diploma.backend.student.controller;

import aur.diploma.backend.student.dto.CreateStudentRequest;
import aur.diploma.backend.student.dto.StudentImportResult;
import aur.diploma.backend.student.dto.StudentResponse;
import aur.diploma.backend.student.service.StudentImportService;
import aur.diploma.backend.student.usecase.StudentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Управление учениками (только админ)")
public class StudentController {

    private final StudentUseCase studentUseCase;
    private final StudentImportService studentImportService;

    @PostMapping
    @Operation(summary = "Создать ученика")
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.ok(studentUseCase.create(request));
    }

    @GetMapping
    @Operation(summary = "Список всех учеников")
    public ResponseEntity<List<StudentResponse>> getAll() {
        return ResponseEntity.ok(studentUseCase.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить ученика по ID")
    public ResponseEntity<StudentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentUseCase.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить ученика")
    public ResponseEntity<StudentResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.ok(studentUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить ученика")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentUseCase.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Пакетный импорт студентов из CSV-файла.
     *
     * <p>Ожидаемый формат (первая строка — заголовок, поля через запятую):
     * <pre>login,password,lastName,firstName,middleName,birthDate,inn,phone</pre>
     *
     * <p>Каждая строка обрабатывается независимо: ошибки отдельных строк
     * не влияют на уже успешно созданные записи.
     *
     * @param file CSV-файл (Content-Type: multipart/form-data, параметр "file")
     * @return объект {@link StudentImportResult} со списком созданных студентов
     *         и списком ошибок (номер строки + причина)
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Импорт студентов из CSV",
            description = "Построчно читает CSV-файл, создаёт студентов независимо друг от друга " +
                          "и возвращает список успешных записей и список ошибок с номером строки."
    )
    public ResponseEntity<StudentImportResult> importStudents(
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(studentImportService.importFromCsv(file));
    }
}
