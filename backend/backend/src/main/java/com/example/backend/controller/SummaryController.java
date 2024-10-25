package com.example.backend.controller;

import com.example.backend.entity.Summary;
import com.example.backend.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SummaryController {

    @Autowired
    private SummaryService summaryService;

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/summary")
    public ResponseEntity<Summary> createSummary() {
        try {
            Summary newSummary = summaryService.createSummary();
            return ResponseEntity.ok(newSummary);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(null);
        }
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/summary")
    public ResponseEntity<List<Summary>> getAllSummaries() {
        return ResponseEntity.ok(summaryService.getAllSummaries());
    }
}
