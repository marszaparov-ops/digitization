package com.archive.digitization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
    // Этот интерфейс позволит нам сохранять логи одной командой: .save()
}