package com.example.traffic.dto;


import com.example.traffic.enums.Direction;
import com.example.traffic.enums.LightColor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

public class PhaseDto {
    @NotEmpty
    private Map<Direction, LightColor> states;

    @Min(1)
    private int durationSeconds;

    public Map<Direction, LightColor> getStates() { return states; }
    public void setStates(Map<Direction, LightColor> states) { this.states = states; }
    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
}
