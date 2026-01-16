package com.example.traffic.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateSequenceRequest {
    @NotNull
    private SequenceDto sequence;

    public SequenceDto getSequence() { return sequence; }
    public void setSequence(SequenceDto sequence) { this.sequence = sequence; }
}
