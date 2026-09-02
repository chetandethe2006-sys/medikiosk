package com.medikiosk.repository;

import com.medikiosk.entity.PatientSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientSessionRepository extends JpaRepository<PatientSession, Long> {
    Optional<PatientSession> findBySessionToken(String sessionToken);
    List<PatientSession> findByPatientIdOrderByStartedAtDesc(Long patientId);
    List<PatientSession> findByIntakeStatusOrderByStartedAtAsc(String intakeStatus);
    List<PatientSession> findAllByOrderByStartedAtDesc();
}
