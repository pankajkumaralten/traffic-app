package com.example.traffic.dto;


import com.example.traffic.enums.Direction;
import com.example.traffic.enums.LightColor;

import java.util.Map;

public class HistoryEntryDto {
    private String timestamp;
    private int phaseIndex;
    private Map<Direction, LightColor> states;

    public HistoryEntryDto(String timestamp, int phaseIndex, Map<Direction, LightColor> states) {
        this.timestamp = timestamp;
        this.phaseIndex = phaseIndex;
        this.states = states;
    }

    public String getTimestamp() { return timestamp; }
    public int getPhaseIndex() { return phaseIndex; }
    public Map<Direction, LightColor> getStates() { return states; }
}
