package com.example.traffic.dto;

import java.util.List;

public class HistoryResponse {
    private String intersectionId;
    private List<HistoryEntryDto> history;

    public HistoryResponse(String intersectionId, List<HistoryEntryDto> history) {
        this.intersectionId = intersectionId;
        this.history = history;
    }

    public String getIntersectionId() { return intersectionId; }
    public List<HistoryEntryDto> getHistory() { return history; }
}
