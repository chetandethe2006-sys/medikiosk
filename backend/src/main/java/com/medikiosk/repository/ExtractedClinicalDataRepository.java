package com.medikiosk.repository;

import com.medikiosk.entity.ExtractedClinicalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExtractedClinicalDataRepository extends JpaRepository<ExtractedClinicalData, Long> {
    List<ExtractedClinicalData> findByDocumentId(Long documentId);
}
