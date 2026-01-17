package com.example.traffic.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class SequenceDto {
    @NotEmpty
    private List<PhaseDto> phases;

    public List<PhaseDto> getPhases() { return phases; }
    public void setPhases(List<PhaseDto> phases) { this.phases = phases; }
}
