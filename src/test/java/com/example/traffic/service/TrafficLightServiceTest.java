package com.example.traffic.service;

import com.example.traffic.entity.HistoryEntryEntity;
import com.example.traffic.entity.Intersection;
import com.example.traffic.entity.PhaseEntity;
import com.example.traffic.entity.SequenceEntity;
import com.example.traffic.enums.Direction;
import com.example.traffic.enums.LightColor;
import com.example.traffic.repo.HistoryEntryRepository;
import com.example.traffic.repo.IntersectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrafficLightServiceTest {

    private IntersectionRepository intersectionRepo;
    private HistoryEntryRepository historyRepo;
    private TrafficLightService service;

    @BeforeEach
    void setup() {
        intersectionRepo = mock(IntersectionRepository.class);
        historyRepo = mock(HistoryEntryRepository.class);
        service = new TrafficLightService();
        service.intersectionRepo = intersectionRepo;
        service.historyRepo = historyRepo;
    }

    private SequenceEntity buildValidSequence() {
        PhaseEntity phase = new PhaseEntity();
        phase.setDurationSeconds(5);
        phase.setStates(Map.of(Direction.NORTH_SOUTH, LightColor.GREEN,
                Direction.EAST_WEST, LightColor.RED));
        SequenceEntity seq = new SequenceEntity();
        seq.setPhases(List.of(phase));
        return seq;
    }

    // --- CREATE OR UPDATE ---
    @Test
    void testCreateOrUpdate_NewIntersection() {
        SequenceEntity seq = buildValidSequence();
        when(intersectionRepo.findById("A1")).thenReturn(Optional.empty());

        Intersection inter = new Intersection("A1", seq);
        inter.setId("A1");
        when(intersectionRepo.save(any(Intersection.class))).thenReturn(inter);

        Intersection result = service.createOrUpdate("A1", seq);

        assertEquals("A1", result.getId());
        verify(intersectionRepo).save(any(Intersection.class));
    }

    // --- GET INTERSECTION ---
    @Test
    void testGetIntersection_Found() {
        Intersection inter = new Intersection("A1", null);
        inter.setId("A1");
        when(intersectionRepo.findById("A1")).thenReturn(Optional.of(inter));

        Optional<Intersection> result = service.getIntersection("A1");

        assertTrue(result.isPresent());
        assertEquals("A1", result.get().getId());
    }

    // --- LIST IDS ---
    @Test
    void testListIds() {
        Intersection inter1 = new Intersection("A1", null); inter1.setId("A1");
        Intersection inter2 = new Intersection("B2", null); inter2.setId("B2");

        when(intersectionRepo.findAll()).thenReturn(List.of(inter1, inter2));

        List<String> ids = service.listIds();

        assertEquals(List.of("A1", "B2"), ids);
    }

    // --- CONTROLLER STATE LIFECYCLE ---
    @Test
    void testStartPauseResumeStopAndGetState() {
        SequenceEntity seq = buildValidSequence();
        Intersection inter = new Intersection("A1", seq); inter.setId("A1");
        when(intersectionRepo.findById("A1")).thenReturn(Optional.of(inter));

        service.createOrUpdate("A1", seq);
        service.start("A1", 0);

        Optional<TrafficLightService.StateView> state = service.getState("A1");
        assertTrue(state.isPresent());
        assertEquals("A1", state.get().intersectionId());

        service.pause("A1");
        service.resume("A1");
        service.stop("A1");

        Optional<TrafficLightService.StateView> stateAfterStop = service.getState("A1");
        assertTrue(stateAfterStop.isPresent());
    }

    // --- HISTORY ---
    @Test
    void testGetHistory_Found() {
        Intersection inter = new Intersection("A1", null); inter.setId("A1");
        when(intersectionRepo.findById("A1")).thenReturn(Optional.of(inter));

        HistoryEntryEntity entry = new HistoryEntryEntity();
        entry.setIntersection(inter);
        entry.setTimestamp(Instant.now());
        entry.setPhaseIndex(0);
        entry.setStates(Map.of(Direction.NORTH_SOUTH, LightColor.RED));

        when(historyRepo.findByIntersectionOrderByTimestampAsc(inter))
                .thenReturn(List.of(entry));

        List<HistoryEntryEntity> history = service.getHistory("A1");

        assertEquals(1, history.size());
        assertEquals(0, history.get(0).getPhaseIndex());
    }

    @Test
    void testGetHistory_NotFound() {
        when(intersectionRepo.findById("X")).thenReturn(Optional.empty());

        List<HistoryEntryEntity> history = service.getHistory("X");

        assertTrue(history.isEmpty());
    }

    // --- RECORD HISTORY ---
    @Test
    void testRecordHistory() {
        SequenceEntity seq = buildValidSequence();
        Intersection inter = new Intersection("A1", seq); inter.setId("A1");
        when(intersectionRepo.findById("A1")).thenReturn(Optional.of(inter));

        service.createOrUpdate("A1", seq);
        service.start("A1", 0);

        ArgumentCaptor<HistoryEntryEntity> captor = ArgumentCaptor.forClass(HistoryEntryEntity.class);
        verify(historyRepo, atLeastOnce()).save(captor.capture());

        HistoryEntryEntity saved = captor.getValue();
        assertEquals("A1", saved.getIntersection().getId());
        assertNotNull(saved.getTimestamp());
    }




}
