package com.medikiosk.repository;

import com.medikiosk.entity.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    List<AuditEvent> findBySessionIdOrderByTimestampDesc(Long sessionId);
    List<AuditEvent> findByPatientIdOrderByTimestampDesc(Long patientId);
    List<AuditEvent> findTop50ByOrderByTimestampDesc();
}
