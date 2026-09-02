package com.medikiosk.repository;

import com.medikiosk.entity.ClinicalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClinicalDocumentRepository extends JpaRepository<ClinicalDocument, Long> {
    List<ClinicalDocument> findBySessionIdOrderByUploadedAtDesc(Long sessionId);
    List<ClinicalDocument> findByPatientIdOrderByUploadedAtDesc(Long patientId);
}
