package com.medikiosk.repository;

import com.medikiosk.entity.MedicalTimelineEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalTimelineEventRepository extends JpaRepository<MedicalTimelineEvent, Long> {
    List<MedicalTimelineEvent> findByPatientIdOrderByEventDateDesc(Long patientId);
}
