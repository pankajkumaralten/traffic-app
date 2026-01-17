package com.example.traffic.repo;

import com.example.traffic.entity.HistoryEntryEntity;
import com.example.traffic.entity.Intersection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryEntryRepository extends JpaRepository<HistoryEntryEntity, Long> {
    List<HistoryEntryEntity> findByIntersectionOrderByTimestampAsc(Intersection intersection);
}
