package com.example.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "summaries")
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long summaryId;

    private String summary;

    @CreationTimestamp
    @Column(name = "date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp date;

    public Summary() { }

    public Long getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(Long summaryId) {
        this.summaryId = summaryId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Summary summary1 = (Summary) o;
        return Objects.equals(summaryId, summary1.summaryId) && Objects.equals(summary, summary1.summary) && Objects.equals(date, summary1.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summaryId, summary, date);
    }

    @Override
    public String toString() {
        String formattedDate = date
                .toLocalDateTime()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return String.format(
                "{\"summaryId\": \"%d\", \"summary\": \"%s\", \"date\": \"%s\"}",
                summaryId,
                summary,
                formattedDate
        );
    }
}
