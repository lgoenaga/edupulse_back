package com.cesde.edupulse.service;

import com.cesde.edupulse.domain.enums.EvaluationCategory;
import com.cesde.edupulse.domain.enums.RoleType;
import com.cesde.edupulse.domain.model.AcademicGroup;
import com.cesde.edupulse.domain.model.AcademicLevel;
import com.cesde.edupulse.domain.model.AcademicLoad;
import com.cesde.edupulse.domain.model.AcademicPeriod;
import com.cesde.edupulse.domain.model.AcademicSubject;
import com.cesde.edupulse.domain.model.AppUser;
import com.cesde.edupulse.domain.model.Question;
import com.cesde.edupulse.domain.model.Student;
import com.cesde.edupulse.domain.model.Teacher;
import com.cesde.edupulse.domain.model.Technique;
import com.cesde.edupulse.repository.AppUserRepository;
import com.cesde.edupulse.repository.QuestionRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        if (appUserRepository.count() > 0 || questionRepository.count() > 0) {
            return;
        }

        AcademicLevel levelOne = persist(AcademicLevel.builder().name("Nivel 1").displayOrder(1).build());
        AcademicLevel levelTwo = persist(AcademicLevel.builder().name("Nivel 2").displayOrder(2).build());
        AcademicLevel levelThree = persist(AcademicLevel.builder().name("Nivel 3").displayOrder(3).build());

        Technique technique = persist(
                Technique.builder().code("TEC-DES").name("Tecnica en Desarrollo de Software").build());
        AcademicGroup groupA = persist(
                AcademicGroup.builder().name("Grupo A").level(levelOne).technique(technique).build());

        AcademicSubject subjectOne = persist(
                AcademicSubject.builder().code("MAT-101").name("Fundamentos de Programacion").level(levelOne).build());
        AcademicSubject subjectTwo = persist(
                AcademicSubject.builder().code("MAT-102").name("Bases de Datos I").level(levelOne).build());
        AcademicSubject subjectThree = persist(
                AcademicSubject.builder().code("MAT-103").name("Arquitectura de Software").level(levelOne).build());
        persist(AcademicSubject.builder().code("MAT-201").name("Desarrollo Web").level(levelTwo).build());
        persist(AcademicSubject.builder().code("MAT-202").name("Integracion de Sistemas").level(levelTwo).build());
        persist(AcademicSubject.builder().code("MAT-203").name("Testing de Software").level(levelTwo).build());
        persist(AcademicSubject.builder().code("MAT-301").name("Cloud Computing").level(levelThree).build());
        persist(AcademicSubject.builder().code("MAT-302").name("Analitica Aplicada").level(levelThree).build());
        persist(AcademicSubject.builder().code("MAT-303").name("Proyecto Integrador").level(levelThree).build());

        Teacher teacherOne = persist(Teacher.builder()
                .documentNumber("9001001")
                .firstName("Luisa")
                .lastName("Restrepo")
                .email("luisa.restrepo@cesde.edu.co")
                .build());
        Teacher teacherTwo = persist(Teacher.builder()
                .documentNumber("9001002")
                .firstName("Carlos")
                .lastName("Mejia")
                .email("carlos.mejia@cesde.edu.co")
                .build());
        Teacher teacherThree = persist(Teacher.builder()
                .documentNumber("9001003")
                .firstName("Paola")
                .lastName("Quintero")
                .email("paola.quintero@cesde.edu.co")
                .build());

        AcademicPeriod activePeriod = persist(AcademicPeriod.builder()
                .year(LocalDate.now().getYear())
                .termNumber(1)
                .name("Periodo 1 " + LocalDate.now().getYear())
                .startDate(LocalDate.now().withMonth(1).withDayOfMonth(15))
                .endDate(LocalDate.now().withMonth(6).withDayOfMonth(30))
                .active(true)
                .build());

        persist(AppUser.builder()
                .username("admin@edupulse.local")
                .password(passwordEncoder.encode("Admin123*"))
                .fullName("Administrador EduPulse")
                .role(RoleType.ADMIN)
                .build());
        AppUser studentUser = persist(AppUser.builder()
                .username("estudiante@edupulse.local")
                .password(passwordEncoder.encode("Estudiante123*"))
                .fullName("Laura Gomez")
                .role(RoleType.ESTUDIANTE)
                .build());

        persist(Student.builder()
                .studentCode("EST-001")
                .firstName("Laura")
                .lastName("Gomez")
                .email("laura.gomez@cesde.edu.co")
                .user(studentUser)
                .group(groupA)
                .build());

        persist(AcademicLoad.builder().teacher(teacherOne).subject(subjectOne).group(groupA).period(activePeriod)
                .build());
        persist(AcademicLoad.builder().teacher(teacherTwo).subject(subjectTwo).group(groupA).period(activePeriod)
                .build());
        persist(AcademicLoad.builder().teacher(teacherThree).subject(subjectThree).group(groupA).period(activePeriod)
                .build());

        persist(Question.builder().prompt("La infraestructura institucional favorece el aprendizaje")
                .category(EvaluationCategory.INSTITUCION).displayOrder(1).build());
        persist(Question.builder().prompt("Los recursos tecnologicos de la institucion son pertinentes")
                .category(EvaluationCategory.INSTITUCION).displayOrder(2).build());
        persist(Question.builder().prompt("La tecnica responde a las necesidades del mercado laboral")
                .category(EvaluationCategory.TECNICA).displayOrder(3).build());
        persist(Question.builder().prompt("Los contenidos de la tecnica son claros y actualizados")
                .category(EvaluationCategory.TECNICA).displayOrder(4).build());
        persist(Question.builder().prompt("El docente comunica con claridad los temas de la materia")
                .category(EvaluationCategory.DOCENTE).displayOrder(5).build());
        persist(Question.builder().prompt("El docente promueve un ambiente respetuoso y participativo")
                .category(EvaluationCategory.DOCENTE).displayOrder(6).build());

        entityManager.flush();
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        return entity;
    }
}