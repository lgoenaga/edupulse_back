package com.cesde.edupulse.service;

import com.cesde.edupulse.domain.model.Teacher;
import com.cesde.edupulse.dto.catalog.TeacherRequest;
import com.cesde.edupulse.dto.catalog.TeacherResponse;
import com.cesde.edupulse.repository.AcademicLoadRepository;
import com.cesde.edupulse.repository.SurveyResponseRepository;
import com.cesde.edupulse.repository.TeacherRepository;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final AcademicLoadRepository academicLoadRepository;
    private final SurveyResponseRepository surveyResponseRepository;

    @Transactional(readOnly = true)
    public List<TeacherResponse> findAll() {
        return teacherRepository.findAllByOrderByFirstNameAscLastNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TeacherResponse create(TeacherRequest request) {
        String normalizedDocument = normalizeDocument(request.documentNumber());
        String normalizedFirstName = normalizeName(request.firstName());
        String normalizedLastName = normalizeName(request.lastName());
        String normalizedEmail = normalizeEmail(request.email());

        validateUniqueness(normalizedDocument, normalizedEmail, null);

        Teacher saved = teacherRepository.save(Objects.requireNonNull(Teacher.builder()
                .documentNumber(normalizedDocument)
                .firstName(normalizedFirstName)
                .lastName(normalizedLastName)
                .email(normalizedEmail)
                .build()));

        return toResponse(saved);
    }

    @Transactional
    public TeacherResponse update(Long id, TeacherRequest request) {
        Long teacherId = Objects.requireNonNull(id);
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Docente no encontrado"));

        String normalizedDocument = normalizeDocument(request.documentNumber());
        String normalizedFirstName = normalizeName(request.firstName());
        String normalizedLastName = normalizeName(request.lastName());
        String normalizedEmail = normalizeEmail(request.email());

        validateUniqueness(normalizedDocument, normalizedEmail, teacherId);

        teacher.setDocumentNumber(normalizedDocument);
        teacher.setFirstName(normalizedFirstName);
        teacher.setLastName(normalizedLastName);
        teacher.setEmail(normalizedEmail);

        return toResponse(teacherRepository.save(teacher));
    }

    @Transactional
    public void delete(Long id) {
        Long teacherId = Objects.requireNonNull(id);
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Docente no encontrado"));

        if (academicLoadRepository.existsByTeacherId(teacherId)
                || surveyResponseRepository.existsByTeacherId(teacherId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar un docente asociado a carga academica o respuestas de encuesta");
        }

        teacherRepository.delete(Objects.requireNonNull(teacher));
    }

    private void validateUniqueness(String documentNumber, String email, Long currentId) {
        boolean documentExists = currentId == null
                ? teacherRepository.existsByDocumentNumber(documentNumber)
                : teacherRepository.existsByDocumentNumberAndIdNot(documentNumber, currentId);
        if (documentExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un docente con ese numero de documento");
        }

        boolean emailExists = currentId == null
                ? teacherRepository.existsByEmailIgnoreCase(email)
                : teacherRepository.existsByEmailIgnoreCaseAndIdNot(email, currentId);
        if (emailExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un docente con ese correo");
        }
    }

    private String normalizeDocument(String documentNumber) {
        return documentNumber == null ? "" : documentNumber.trim();
    }

    private String normalizeName(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private TeacherResponse toResponse(Teacher teacher) {
        return new TeacherResponse(
                teacher.getId(),
                teacher.getDocumentNumber(),
                teacher.getFirstName(),
                teacher.getLastName(),
                teacher.getEmail(),
                teacher.getFullName());
    }
}