package aur.diploma.backend.student.dto;

import java.util.List;

/**
 * Итог обработки CSV-файла импорта студентов.
 *
 * @param created список успешно созданных студентов
 * @param errors  список ошибок с указанием строки и причины
 */
public record StudentImportResult(
        List<StudentResponse> created,
        List<StudentImportError> errors
) {}
