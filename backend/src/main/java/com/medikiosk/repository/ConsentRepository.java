package com.medikiosk.repository;

import com.medikiosk.entity.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {
    Optional<Consent> findBySessionId(Long sessionId);
    Optional<Consent> findByPatientIdOrderByConsentedAtDesc(Long patientId);
}
