package org.example.traffic.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sequences")
public class SequenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ordered phases
    @OneToMany(mappedBy = "sequence", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "order_index")
    private List<PhaseEntity> phases = new ArrayList<>();

    public Long getId() { return id; }
    public List<PhaseEntity> getPhases() { return phases; }
    public void setPhases(List<PhaseEntity> phases) {
        this.phases.clear();
        if (phases != null) {
            phases.forEach(p -> p.setSequence(this));
            this.phases.addAll(phases);
        }
    }
}
