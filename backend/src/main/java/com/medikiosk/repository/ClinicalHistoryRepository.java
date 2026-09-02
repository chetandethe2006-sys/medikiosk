package com.medikiosk.repository;

import com.medikiosk.entity.ClinicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicalHistoryRepository extends JpaRepository<ClinicalHistory, Long> {
    Optional<ClinicalHistory> findBySessionId(Long sessionId);
    List<ClinicalHistory> findByPatientIdOrderByRecordedAtDesc(Long patientId);
}
