package org.example.traffic.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "intersections")
public class Intersection {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sequence_id")
    private SequenceEntity sequence;

    public Intersection() {}
    public Intersection(String id, SequenceEntity sequence) {
        this.id = id;
        this.sequence = sequence;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public SequenceEntity getSequence() { return sequence; }
    public void setSequence(SequenceEntity sequence) { this.sequence = sequence; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Intersection)) return false;
        Intersection that = (Intersection) o;
        return Objects.equals(id, that.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
