package com.archive.digitization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Здесь пока ничего писать не нужно, JpaRepository всё сделает за нас
}