package com.example.traffic.service;

import com.example.traffic.dto.SequenceDto;
import com.example.traffic.entity.Intersection;
import com.example.traffic.entity.SequenceEntity;
import com.example.traffic.mapper.MapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IntersectionRegistryTest {

    private TrafficLightService controller;
    private MapperService mapper;
    private IntersectionRegistry registry;

    @BeforeEach
    void setUp() {
        controller = Mockito.mock(TrafficLightService.class);
        mapper = Mockito.mock(MapperService.class);

        registry = new IntersectionRegistry();
        // inject mocks manually since we’re not using Spring here
        registry.controller = controller;
        registry.mapper = mapper;
    }

    @Test
    void testCreateOrUpdate() {
        SequenceDto dto = new SequenceDto(); // fill with test data if needed
        SequenceEntity seqEntity = new SequenceEntity();
        Intersection intersection = new Intersection("A1");

        when(mapper.toSequenceEntity(dto)).thenReturn(seqEntity);
        when(controller.createOrUpdate("A1", seqEntity)).thenReturn(intersection);

        Intersection result = registry.createOrUpdate("A1", dto);

        assertNotNull(result);
        assertEquals("A1", result.getId());

        verify(mapper, times(1)).toSequenceEntity(dto);
        verify(controller, times(1)).createOrUpdate("A1", seqEntity);
    }

    @Test
    void testGetIntersectionFound() {
        Intersection intersection = new Intersection("B2");
        when(controller.getIntersection("B2")).thenReturn(Optional.of(intersection));

        Optional<Intersection> result = registry.get("B2");

        assertTrue(result.isPresent());
        assertEquals("B2", result.get().getId());
        verify(controller, times(1)).getIntersection("B2");
    }

    @Test
    void testGetIntersectionNotFound() {
        when(controller.getIntersection("X1")).thenReturn(Optional.empty());

        Optional<Intersection> result = registry.get("X1");

        assertFalse(result.isPresent());
        verify(controller, times(1)).getIntersection("X1");
    }
}
