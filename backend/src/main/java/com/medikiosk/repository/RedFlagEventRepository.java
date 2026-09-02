package com.medikiosk.repository;

import com.medikiosk.entity.RedFlagEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RedFlagEventRepository extends JpaRepository<RedFlagEvent, Long> {
    List<RedFlagEvent> findBySessionId(Long sessionId);
    List<RedFlagEvent> findByPatientIdOrderByDetectedAtDesc(Long patientId);
    List<RedFlagEvent> findByTriageStatusOrderByDetectedAtDesc(String triageStatus);
    List<RedFlagEvent> findAllByOrderByDetectedAtDesc();
}
