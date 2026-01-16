package org.example.traffic.entity;

import com.example.traffic.domain.Direction;
import com.example.traffic.domain.LightColor;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

@Entity
@Table(name = "history_entries")
public class HistoryEntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant timestamp;

    private int phaseIndex;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "history_states", joinColumns = @JoinColumn(name = "history_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "direction")
    @Enumerated(EnumType.STRING)
    @Column(name = "color")
    private Map<Direction, LightColor> states = new EnumMap<>(Direction.class);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intersection_id")
    private Intersection intersection;

    public Long getId() { return id; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public int getPhaseIndex() { return phaseIndex; }
    public void setPhaseIndex(int phaseIndex) { this.phaseIndex = phaseIndex; }
    public Map<Direction, LightColor> getStates() { return states; }
    public void setStates(Map<Direction, LightColor> states) { this.states = states; }
    public Intersection getIntersection() { return intersection; }
    public void setIntersection(Intersection intersection) { this.intersection = intersection; }
}
