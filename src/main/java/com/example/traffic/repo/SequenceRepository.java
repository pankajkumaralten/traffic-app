package com.example.traffic.repo;

import com.example.traffic.entity.SequenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SequenceRepository extends JpaRepository<SequenceEntity, Long> {}
