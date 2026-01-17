package com.example.traffic.entity;


import com.example.traffic.enums.Direction;
import com.example.traffic.enums.LightColor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.util.EnumMap;
import java.util.Map;

@Entity
@Table(name = "phases")
public class PhaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Column(name = "duration_seconds", nullable = false)
    private int durationSeconds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sequence_id")
    private SequenceEntity sequence;

    // Map<Direction, LightColor> persisted via element collection
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "phase_states", joinColumns = @JoinColumn(name = "phase_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "direction")
    @Enumerated(EnumType.STRING)
    @Column(name = "color")
    private Map<Direction, LightColor> states = new EnumMap<>(Direction.class);

    public Long getId() { return id; }
    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public SequenceEntity getSequence() { return sequence; }
    public void setSequence(SequenceEntity sequence) { this.sequence = sequence; }

    public Map<Direction, LightColor> getStates() { return states; }
    public void setStates(Map<Direction, LightColor> states) { this.states = states; }

    @PrePersist @PreUpdate
    private void validate() {
        if (durationSeconds <= 0) {
            throw new IllegalArgumentException("Phase duration must be positive");
        }
        long greens = states.values().stream().filter(c -> c == LightColor.GREEN).count();
        if (greens > 1) {
            throw new IllegalArgumentException("Conflicting GREEN states in a single phase");
        }
    }
}
