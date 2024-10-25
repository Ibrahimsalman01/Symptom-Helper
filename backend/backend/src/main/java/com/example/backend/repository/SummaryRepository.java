package com.example.backend.repository;

import com.example.backend.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummaryRepository extends JpaRepository<Summary, Long> {

}
