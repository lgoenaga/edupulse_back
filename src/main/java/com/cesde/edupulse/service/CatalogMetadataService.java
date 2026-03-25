package com.cesde.edupulse.service;

import com.cesde.edupulse.domain.model.AcademicPeriod;
import com.cesde.edupulse.dto.catalog.CatalogMetadataResponse;
import com.cesde.edupulse.dto.common.SelectOptionResponse;
import com.cesde.edupulse.repository.AcademicGroupRepository;
import com.cesde.edupulse.repository.AcademicLevelRepository;
import com.cesde.edupulse.repository.AcademicPeriodRepository;
import com.cesde.edupulse.repository.TeacherRepository;
import com.cesde.edupulse.repository.TechniqueRepository;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatalogMetadataService {

    private final AcademicLevelRepository academicLevelRepository;
    private final AcademicGroupRepository academicGroupRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final TechniqueRepository techniqueRepository;
    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public CatalogMetadataResponse getMetadata() {
        return new CatalogMetadataResponse(
                academicLevelRepository.findAllByOrderByDisplayOrderAscNameAsc().stream()
                        .map(level -> new SelectOptionResponse(level.getId(), level.getName()))
                        .toList(),
                academicGroupRepository.findAllByOrderByNameAsc().stream()
                        .map(group -> new SelectOptionResponse(group.getId(), group.getName()))
                        .toList(),
                academicPeriodRepository.findAll().stream()
                        .sorted(Comparator.comparing(AcademicPeriod::getYear)
                                .thenComparing(AcademicPeriod::getTermNumber)
                                .reversed())
                        .map(period -> new SelectOptionResponse(period.getId(), period.getName()))
                        .toList(),
                techniqueRepository.findAllByOrderByNameAsc().stream()
                        .map(technique -> new SelectOptionResponse(technique.getId(), technique.getName()))
                        .toList(),
                teacherRepository.findAll().stream()
                        .sorted(Comparator.comparing(teacher -> teacher.getFullName().toLowerCase()))
                        .map(teacher -> new SelectOptionResponse(teacher.getId(), teacher.getFullName()))
                        .toList());
    }
}