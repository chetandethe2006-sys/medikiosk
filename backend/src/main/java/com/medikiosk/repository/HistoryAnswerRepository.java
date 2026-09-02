package com.medikiosk.repository;

import com.medikiosk.entity.HistoryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoryAnswerRepository extends JpaRepository<HistoryAnswer, Long> {
    List<HistoryAnswer> findByHistoryIdOrderByStepOrderAsc(Long historyId);
}
