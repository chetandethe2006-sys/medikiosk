package com.medikiosk.repository;

import com.medikiosk.entity.ClinicalSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicalSummaryRepository extends JpaRepository<ClinicalSummary, Long> {
    Optional<ClinicalSummary> findBySessionId(Long sessionId);
    Optional<ClinicalSummary> findTopByPatientIdOrderByGeneratedAtDesc(Long patientId);
    List<ClinicalSummary> findByPatientIdOrderByGeneratedAtDesc(Long patientId);
}
