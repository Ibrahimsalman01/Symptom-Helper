package com.example.backend.service;

import com.example.backend.TranscriptionClient;
import com.example.backend.TranscriptionSummary;
import com.example.backend.entity.Summary;
import com.example.backend.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SummaryService {

    @Autowired
    private SummaryRepository summaryRepository;

    public SummaryService() { }

    public List<Summary> getAllSummaries() {
        return summaryRepository.findAll();
    }

    public Optional<Summary> getSummary(Long id) {
        return summaryRepository.findById(id);
    }

    public Summary createSummary() throws Exception {
        Summary summary = new Summary();
        TranscriptionClient tc = new TranscriptionClient();
        TranscriptionSummary ts = new TranscriptionSummary();

        summary
                .setSummary(ts.textInput(tc.executeTranscription()));

        return summaryRepository.save(summary);
    }
}
