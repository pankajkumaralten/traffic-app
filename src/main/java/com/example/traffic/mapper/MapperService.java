package com.example.traffic.mapper;

import com.example.traffic.dto.PhaseDto;
import com.example.traffic.dto.SequenceDto;
import com.example.traffic.entity.PhaseEntity;
import com.example.traffic.entity.SequenceEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MapperService {
    public SequenceEntity toSequenceEntity(SequenceDto dto) {
        SequenceEntity seq = new SequenceEntity();
        List<PhaseEntity> phases = dto.getPhases().stream().map(this::toPhaseEntity).toList();
        seq.setPhases(phases);
        return seq;
    }

    public PhaseEntity toPhaseEntity(PhaseDto dto) {
        PhaseEntity p = new PhaseEntity();
        p.setDurationSeconds(dto.getDurationSeconds());
        p.setStates(dto.getStates());
        return p;
    }
}
