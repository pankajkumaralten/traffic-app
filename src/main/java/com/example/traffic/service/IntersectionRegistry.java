package com.example.traffic.service;


import com.example.traffic.dto.SequenceDto;
import com.example.traffic.entity.Intersection;
import com.example.traffic.entity.SequenceEntity;
import com.example.traffic.mapper.MapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IntersectionRegistry {
    @Autowired
    private  TrafficLightService controller;
    @Autowired
    private  MapperService mapper;


    public Intersection createOrUpdate(String id, SequenceDto dto) {
        SequenceEntity seq = mapper.toSequenceEntity(dto);
        return controller.createOrUpdate(id, seq);
    }

    public Optional<Intersection> get(String id) { return controller.getIntersection(id); }
}
