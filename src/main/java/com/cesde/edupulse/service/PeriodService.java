package com.cesde.edupulse.service;

import com.cesde.edupulse.domain.model.AcademicPeriod;
import com.cesde.edupulse.dto.common.PageResponse;
import com.cesde.edupulse.dto.catalog.AcademicPeriodRequest;
import com.cesde.edupulse.dto.catalog.AcademicPeriodResponse;
import com.cesde.edupulse.repository.AcademicPeriodRepository;
import com.cesde.edupulse.repository.SurveySubmissionRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PeriodService {

    private final AcademicPeriodRepository academicPeriodRepository;
    private final SurveySubmissionRepository surveySubmissionRepository;

    @Transactional(readOnly = true)
    public List<AcademicPeriodResponse> findAll() {
        return academicPeriodRepository.findAllByOrderByYearDescTermNumberDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<AcademicPeriodResponse> findPage(int page, int size) {
        validatePagination(page, size);

        Page<AcademicPeriod> periodPage = academicPeriodRepository.findAllByOrderByYearDescTermNumberDesc(
                PageRequest.of(page, size));
        List<AcademicPeriodResponse> items = periodPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                items,
                periodPage.getNumber(),
                periodPage.getSize(),
                periodPage.getTotalElements(),
                periodPage.getTotalPages(),
                periodPage.isFirst(),
                periodPage.isLast());
    }

    @Transactional
    public AcademicPeriodResponse create(AcademicPeriodRequest request) {
        validateRequest(request);
        deactivateCurrentActivePeriod(null, request.active());

        AcademicPeriod saved = academicPeriodRepository.save(Objects.requireNonNull(AcademicPeriod.builder()
                .year(request.year())
                .termNumber(request.termNumber())
                .name(request.name())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .active(request.active())
                .build()));

        return toResponse(saved);
    }

    @Transactional
    public AcademicPeriodResponse update(Long id, AcademicPeriodRequest request) {
        validateRequest(request);

        Long periodId = Objects.requireNonNull(id);

        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo no encontrado"));

        deactivateCurrentActivePeriod(periodId, request.active());

        period.setYear(request.year());
        period.setTermNumber(request.termNumber());
        period.setName(request.name());
        period.setStartDate(request.startDate());
        period.setEndDate(request.endDate());
        period.setActive(request.active());

        return toResponse(academicPeriodRepository.save(period));
    }

    @Transactional
    public void delete(Long id) {
        Long periodId = Objects.requireNonNull(id);

        AcademicPeriod period = academicPeriodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo no encontrado"));

        if (surveySubmissionRepository.existsByPeriodId(periodId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar un periodo que ya tiene encuestas registradas");
        }

        academicPeriodRepository.delete(Objects.requireNonNull(period));
    }

    private void validateRequest(AcademicPeriodRequest request) {
        if (request.startDate().isAfter(request.endDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de inicio no puede ser posterior a la fecha de fin");
        }
    }

    private void deactivateCurrentActivePeriod(Long currentId, boolean shouldActivateRequestedPeriod) {
        if (!shouldActivateRequestedPeriod) {
            return;
        }

        academicPeriodRepository.findByActiveTrue().ifPresent(period -> {
            if (currentId == null || !period.getId().equals(currentId)) {
                period.setActive(false);
                academicPeriodRepository.save(period);
            }
        });
    }

    private AcademicPeriodResponse toResponse(AcademicPeriod period) {
        return new AcademicPeriodResponse(
                period.getId(),
                period.getYear(),
                period.getTermNumber(),
                period.getName(),
                period.getStartDate(),
                period.getEndDate(),
                period.isActive());
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
}