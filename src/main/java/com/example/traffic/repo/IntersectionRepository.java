package com.example.traffic.repo;

import com.example.traffic.entity.Intersection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntersectionRepository extends JpaRepository<Intersection, String> {}
