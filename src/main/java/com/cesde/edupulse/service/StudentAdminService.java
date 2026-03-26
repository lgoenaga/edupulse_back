package com.cesde.edupulse.service;

import com.cesde.edupulse.domain.enums.RoleType;
import com.cesde.edupulse.domain.model.AcademicGroup;
import com.cesde.edupulse.domain.model.AppUser;
import com.cesde.edupulse.domain.model.Student;
import com.cesde.edupulse.dto.common.PageResponse;
import com.cesde.edupulse.dto.catalog.StudentRequest;
import com.cesde.edupulse.dto.catalog.StudentResponse;
import com.cesde.edupulse.repository.AcademicGroupRepository;
import com.cesde.edupulse.repository.AppUserRepository;
import com.cesde.edupulse.repository.StudentRepository;
import com.cesde.edupulse.repository.SurveySubmissionRepository;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StudentAdminService {

    private final StudentRepository studentRepository;
    private final AcademicGroupRepository academicGroupRepository;
    private final AppUserRepository appUserRepository;
    private final SurveySubmissionRepository surveySubmissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<StudentResponse> findAll() {
        return studentRepository.findAllByOrderByFirstNameAscLastNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> findPage(int page, int size) {
        validatePagination(page, size);

        Page<Student> studentPage = studentRepository
                .findAllByOrderByFirstNameAscLastNameAsc(PageRequest.of(page, size));
        List<StudentResponse> items = studentPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                items,
                studentPage.getNumber(),
                studentPage.getSize(),
                studentPage.getTotalElements(),
                studentPage.getTotalPages(),
                studentPage.isFirst(),
                studentPage.isLast());
    }

    @Transactional
    public StudentResponse create(StudentRequest request) {
        String normalizedStudentCode = normalizeStudentCode(request.studentCode());
        String normalizedFirstName = normalizeName(request.firstName());
        String normalizedLastName = normalizeName(request.lastName());
        String normalizedEmail = normalizeEmail(request.email());
        String rawPassword = normalizePassword(request.password());

        if (rawPassword.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La contrasena es obligatoria para crear el estudiante");
        }

        validateUniqueness(normalizedStudentCode, normalizedEmail, null, null);
        AcademicGroup group = findGroup(request.groupId());

        AppUser user = appUserRepository.save(Objects.requireNonNull(AppUser.builder()
                .username(normalizedEmail)
                .password(passwordEncoder.encode(rawPassword))
                .fullName(buildFullName(normalizedFirstName, normalizedLastName))
                .role(RoleType.ESTUDIANTE)
                .active(request.active())
                .build()));

        Student saved = studentRepository.save(Objects.requireNonNull(Student.builder()
                .studentCode(normalizedStudentCode)
                .firstName(normalizedFirstName)
                .lastName(normalizedLastName)
                .email(normalizedEmail)
                .user(user)
                .group(group)
                .build()));

        return toResponse(saved);
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Long studentId = Objects.requireNonNull(id);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));

        String normalizedStudentCode = normalizeStudentCode(request.studentCode());
        String normalizedFirstName = normalizeName(request.firstName());
        String normalizedLastName = normalizeName(request.lastName());
        String normalizedEmail = normalizeEmail(request.email());
        String rawPassword = normalizePassword(request.password());

        validateUniqueness(normalizedStudentCode, normalizedEmail, studentId, student.getUser().getId());

        AcademicGroup group = findGroup(request.groupId());
        AppUser user = student.getUser();

        user.setUsername(normalizedEmail);
        user.setFullName(buildFullName(normalizedFirstName, normalizedLastName));
        user.setActive(request.active());
        if (!rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        appUserRepository.save(user);

        student.setStudentCode(normalizedStudentCode);
        student.setFirstName(normalizedFirstName);
        student.setLastName(normalizedLastName);
        student.setEmail(normalizedEmail);
        student.setGroup(group);

        return toResponse(studentRepository.save(student));
    }

    @Transactional
    public void delete(Long id) {
        Long studentId = Objects.requireNonNull(id);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));

        if (surveySubmissionRepository.existsByStudentId(studentId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar un estudiante con envios de encuesta asociados");
        }

        AppUser user = student.getUser();
        studentRepository.delete(Objects.requireNonNull(student));
        appUserRepository.delete(Objects.requireNonNull(user));
    }

    private void validateUniqueness(String studentCode, String email, Long currentStudentId, Long currentUserId) {
        boolean codeExists = currentStudentId == null
                ? studentRepository.existsByStudentCode(studentCode)
                : studentRepository.existsByStudentCodeAndIdNot(studentCode, currentStudentId);
        if (codeExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un estudiante con ese codigo");
        }

        boolean emailExistsInStudents = currentStudentId == null
                ? studentRepository.existsByEmailIgnoreCase(email)
                : studentRepository.existsByEmailIgnoreCaseAndIdNot(email, currentStudentId);
        if (emailExistsInStudents) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un estudiante con ese correo");
        }

        boolean usernameExists = currentUserId == null
                ? appUserRepository.existsByUsername(email)
                : appUserRepository.existsByUsernameAndIdNot(email, currentUserId);
        if (usernameExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo");
        }
    }

    private AcademicGroup findGroup(Long groupId) {
        return academicGroupRepository.findById(Objects.requireNonNull(groupId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));
    }

    private String normalizeStudentCode(String studentCode) {
        return studentCode == null ? "" : studentCode.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeName(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizePassword(String password) {
        return password == null ? "" : password.trim();
    }

    private String buildFullName(String firstName, String lastName) {
        return (firstName + " " + lastName).trim();
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La pagina no puede ser negativa");
        }

        if (size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El tamano de pagina debe estar entre 1 y 100");
        }
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getStudentCode(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getFullName(),
                student.getGroup().getId(),
                student.getGroup().getName(),
                student.getUser().isEnabled());
    }
}