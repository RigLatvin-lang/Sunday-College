package aur.diploma.backend.student.dto;

/**
 * Описывает ошибку обработки одной строки CSV-файла при импорте студентов.
 *
 * @param lineNumber номер строки в файле (1-based; 0 означает ошибку уровня файла)
 * @param reason     текстовое описание причины ошибки
 */
public record StudentImportError(
        int lineNumber,
        String reason
) {}
