package com.example.traffic.dto;

public class ControlRequest {
    private String action;     // start, pause, resume, stop
    private Integer phaseIndex;

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Integer getPhaseIndex() { return phaseIndex; }
    public void setPhaseIndex(Integer phaseIndex) { this.phaseIndex = phaseIndex; }
}
