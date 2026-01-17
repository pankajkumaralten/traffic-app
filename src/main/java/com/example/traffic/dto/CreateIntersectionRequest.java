package com.example.traffic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateIntersectionRequest {
    @NotBlank
    private String intersectionId;

    @NotNull
    private SequenceDto sequence;

    public String getIntersectionId() { return intersectionId; }
    public void setIntersectionId(String intersectionId) { this.intersectionId = intersectionId; }
    public SequenceDto getSequence() { return sequence; }
    public void setSequence(SequenceDto sequence) { this.sequence = sequence; }
}
