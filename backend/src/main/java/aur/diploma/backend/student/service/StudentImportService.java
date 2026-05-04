package aur.diploma.backend.student.service;

import aur.diploma.backend.student.dto.CreateStudentRequest;
import aur.diploma.backend.student.dto.StudentImportError;
import aur.diploma.backend.student.dto.StudentImportResult;
import aur.diploma.backend.student.dto.StudentResponse;
import aur.diploma.backend.student.usecase.StudentUseCase;
import aur.diploma.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Сервис пакетного импорта студентов из CSV-файла.
 *
 * <p>Ожидаемый формат файла (первая строка — заголовок, далее данные):
 * <pre>
 * login,password,lastName,firstName,middleName,birthDate,inn,phone
 * ivanov01,secret,Иванов,Иван,Иванович,2007-05-20,123456789012,+79001234567
 * </pre>
 *
 * <ul>
 *   <li>Обязательные поля: {@code login}, {@code password}, {@code lastName}, {@code firstName}.</li>
 *   <li>Поля {@code middleName}, {@code birthDate}, {@code inn}, {@code phone} — необязательные.</li>
 *   <li>{@code birthDate} должен быть в формате {@code yyyy-MM-dd}.</li>
 *   <li>Разделитель — запятая; поля могут быть обёрнуты в двойные кавычки.</li>
 * </ul>
 *
 * <p>Каждая строка обрабатывается независимо — ошибка в одной строке
 * не откатывает уже созданные записи.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentImportService {

    private final StudentUseCase studentUseCase;
    private final UserRepository userRepository;

    /**
     * Читает CSV-файл, валидирует каждую строку и создаёт студентов.
     *
     * @param file загруженный multipart-файл
     * @return результат с двумя списками: успешно созданные и ошибки
     */
    public StudentImportResult importFromCsv(MultipartFile file) {
        List<StudentResponse> created = new ArrayList<>();
        List<StudentImportError> errors = new ArrayList<>();

        // Логины из текущего файла — для проверки дублей внутри самого файла.
        Set<String> seenLoginsInFile = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            // Пропускаем строку заголовка.
            String headerLine = reader.readLine();
            if (headerLine == null) {
                errors.add(new StudentImportError(0, "Файл пустой — отсутствует строка заголовка"));
                return new StudentImportResult(created, errors);
            }

            String line;
            int lineNumber = 1; // считаем со строки заголовка = 1, данные начинаются с 2

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue; // пустые строки пропускаем без сообщения об ошибке
                }

                processLine(line, lineNumber, seenLoginsInFile, created, errors);
            }

        } catch (Exception e) {
            log.error("Ошибка чтения CSV-файла при импорте студентов", e);
            errors.add(new StudentImportError(0, "Ошибка чтения файла: " + e.getMessage()));
        }

        log.info("Импорт завершён: создано {}, ошибок {}", created.size(), errors.size());
        return new StudentImportResult(created, errors);
    }

    // ─── private helpers ──────────────────────────────────────────────────

    /**
     * Обрабатывает одну строку CSV: парсинг → валидация → создание студента.
     * Все ошибки добавляются в {@code errors}; успех — в {@code created}.
     * Метод намеренно не оборачивается в общую транзакцию: каждый вызов
     * {@link StudentUseCase#create} несёт свою собственную транзакцию.
     */
    private void processLine(String line,
                             int lineNumber,
                             Set<String> seenLoginsInFile,
                             List<StudentResponse> created,
                             List<StudentImportError> errors) {

        // 1. Парсинг CSV.
        String[] fields = parseCsvLine(line);
        if (fields.length < 4) {
            errors.add(new StudentImportError(lineNumber,
                    "Недостаточно полей: ожидается минимум 4 " +
                    "(login, password, lastName, firstName), получено " + fields.length));
            return;
        }

        String login       = fields[0].trim();
        String password    = fields[1].trim();
        String lastName    = fields[2].trim();
        String firstName   = fields[3].trim();
        String middleName  = fields.length > 4 ? emptyToNull(fields[4].trim()) : null;
        String birthDateRaw = fields.length > 5 ? fields[5].trim() : null;
        String inn         = fields.length > 6 ? emptyToNull(fields[6].trim()) : null;
        String phone       = fields.length > 7 ? emptyToNull(fields[7].trim()) : null;

        // 2. Валидация обязательных полей.
        if (login.isEmpty()) {
            errors.add(new StudentImportError(lineNumber, "Поле 'login' обязательно и не может быть пустым"));
            return;
        }
        if (password.isEmpty()) {
            errors.add(new StudentImportError(lineNumber, "Поле 'password' обязательно и не может быть пустым"));
            return;
        }
        if (lastName.isEmpty()) {
            errors.add(new StudentImportError(lineNumber, "Поле 'lastName' обязательно и не может быть пустым"));
            return;
        }
        if (firstName.isEmpty()) {
            errors.add(new StudentImportError(lineNumber, "Поле 'firstName' обязательно и не может быть пустым"));
            return;
        }

        // 3. Валидация формата даты рождения (если передана).
        LocalDate birthDate = null;
        if (birthDateRaw != null && !birthDateRaw.isEmpty()) {
            try {
                birthDate = LocalDate.parse(birthDateRaw); // ISO_LOCAL_DATE: yyyy-MM-dd
            } catch (DateTimeParseException e) {
                errors.add(new StudentImportError(lineNumber,
                        "Некорректный формат даты рождения: '" + birthDateRaw
                        + "' (ожидается yyyy-MM-dd)"));
                return;
            }
        }

        // 4. Проверка уникальности логина внутри файла.
        if (seenLoginsInFile.contains(login)) {
            errors.add(new StudentImportError(lineNumber,
                    "Дублирующийся логин внутри файла: '" + login + "'"));
            return;
        }

        // 5. Проверка уникальности логина в базе данных.
        if (userRepository.existsByLogin(login)) {
            errors.add(new StudentImportError(lineNumber,
                    "Логин уже занят в базе данных: '" + login + "'"));
            return;
        }

        // Фиксируем логин как «уже встреченный» независимо от результата создания,
        // чтобы последующие строки с тем же логином отклонялись корректно.
        seenLoginsInFile.add(login);

        // 6. Создание студента через штатный метод use-case.
        //    Каждый вызов выполняется в собственной транзакции (@Transactional внутри create()).
        try {
            CreateStudentRequest request = new CreateStudentRequest(
                    login, password, lastName, firstName,
                    middleName, birthDate, inn, phone
            );
            StudentResponse response = studentUseCase.create(request);
            created.add(response);
            log.debug("Строка {}: студент '{}' успешно создан (id={})",
                    lineNumber, login, response.id());
        } catch (Exception e) {
            log.warn("Строка {}: ошибка при создании студента '{}': {}",
                    lineNumber, login, e.getMessage());
            errors.add(new StudentImportError(lineNumber,
                    "Ошибка при создании студента: " + e.getMessage()));
        }
    }

    /**
     * Парсит одну строку CSV с поддержкой двойных кавычек и экранирования
     * (два кавычки подряд {@code ""} внутри поля → один символ {@code "}).
     */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Экранированная кавычка внутри поля.
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString()); // последнее поле
        return fields.toArray(new String[0]);
    }

    /** Возвращает {@code null}, если строка пустая; иначе саму строку. */
    private String emptyToNull(String value) {
        return (value == null || value.isEmpty()) ? null : value;
    }
}
