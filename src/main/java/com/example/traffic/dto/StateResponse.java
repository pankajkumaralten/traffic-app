package com.example.traffic.dto;


import com.example.traffic.enums.Direction;
import com.example.traffic.enums.LightColor;

import java.util.Map;

public class StateResponse {
    private String intersectionId;
    private int phaseIndex;
    private Map<Direction, LightColor> states;
    private int remainingSeconds;

    public StateResponse(String intersectionId, int phaseIndex, Map<Direction, LightColor> states, int remainingSeconds) {
        this.intersectionId = intersectionId;
        this.phaseIndex = phaseIndex;
        this.states = states;
        this.remainingSeconds = remainingSeconds;
    }

    public String getIntersectionId() { return intersectionId; }
    public int getPhaseIndex() { return phaseIndex; }
    public Map<Direction, LightColor> getStates() { return states; }
    public int getRemainingSeconds() { return remainingSeconds; }
}
