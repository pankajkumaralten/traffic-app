package com.example.traffic.service;


import com.example.traffic.entity.HistoryEntryEntity;
import com.example.traffic.entity.Intersection;
import com.example.traffic.entity.PhaseEntity;
import com.example.traffic.entity.SequenceEntity;
import com.example.traffic.enums.Direction;
import com.example.traffic.enums.LightColor;
import com.example.traffic.repo.HistoryEntryRepository;
import com.example.traffic.repo.IntersectionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TrafficLightService {
    @Autowired
    IntersectionRepository intersectionRepo;
    @Autowired
    HistoryEntryRepository historyRepo;

    private final Map<String, ControllerState> controllers = new ConcurrentHashMap<>();

    @Transactional
    public Intersection createOrUpdate(String id, SequenceEntity sequence) {
        Intersection inter = intersectionRepo.findById(id).orElse(new Intersection(id, sequence));
        inter.setSequence(sequence);
        intersectionRepo.save(inter);
        // Reset runtime controller state
        controllers.compute(id, (k, st) -> {
            ControllerState s = st != null ? st : new ControllerState(id);
            s.reset(sequence);
            return s;
        });
        return inter;
    }

    public Optional<Intersection> getIntersection(String id) {
        return intersectionRepo.findById(id);
    }

    public List<String> listIds() {
        return intersectionRepo.findAll().stream().map(Intersection::getId).toList();
    }

    public void start1(String id, Integer phaseIndex) {
        ControllerState st = controllers.computeIfAbsent(id, ControllerState::new);
        st.start(phaseIndex, this::recordHistory);
    }

    public void start(String id, Integer phaseIndex) {
        ControllerState st = controllers.computeIfAbsent(id, ControllerState::new);

        // Concise lambda expression
        st.start(phaseIndex, (i_id, p_idx, states) -> this.recordHistory(i_id, p_idx, states));
    }

    public void pause(String id) {
        ControllerState st = controllers.get(id);
        if (st != null) st.pause();
    }

    public void resume(String id) {
        ControllerState st = controllers.get(id);
        if (st != null) st.resume();
    }

    public void stop(String id) {
        ControllerState st = controllers.get(id);
        if (st != null) st.stop();
    }

    public Optional<StateView> getState(String id) {
        ControllerState st = controllers.get(id);
        if (st == null) return Optional.empty();
        return Optional.of(st.view());
    }

    public List<HistoryEntryEntity> getHistory(String id) {
        return intersectionRepo.findById(id)
                .map(historyRepo::findByIntersectionOrderByTimestampAsc)
                .orElseGet(List::of);
    }

    private void recordHistory(String id, int phaseIndex, Map<Direction, LightColor> states) {
        intersectionRepo.findById(id).ifPresent(inter -> {
            HistoryEntryEntity h = new HistoryEntryEntity();
            h.setIntersection(inter);
            h.setTimestamp(Instant.now());
            h.setPhaseIndex(phaseIndex);
            h.setStates(states);
            historyRepo.save(h);
        });
    }

    // Runtime controller state per intersection
    static class ControllerState {
        private final String id;
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        private final ReentrantLock lock = new ReentrantLock();

        private ScheduledFuture<?> task;
        private boolean paused = false;
        private boolean stopped = true;

        private SequenceEntity sequence;
        private int phaseIndex = 0;
        private int remainingSeconds = 0;
        private Map<Direction, LightColor> currentStates = new EnumMap<>(Direction.class);

        ControllerState(String id) { this.id = id; }

        void reset(SequenceEntity seq) {
            lock.lock();
            try {
                this.sequence = seq;
                this.phaseIndex = 0;
                applyPhase(0);
            } finally { lock.unlock(); }
        }

        void start(Integer startPhaseIndex, HistoryRecorder recorder) {
            lock.lock();
            try {
                if (task != null && !task.isCancelled() && !task.isDone()) return;
                stopped = false; paused = false;
                if (startPhaseIndex != null) {
                    phaseIndex = Math.max(0, Math.min(startPhaseIndex, sequence.getPhases().size() - 1));
                    applyPhase(phaseIndex);
                }
                recorder.record(id, phaseIndex, currentStates);
                task = scheduler.scheduleAtFixedRate(() -> tick(recorder), 1, 1, TimeUnit.SECONDS);
            } finally { lock.unlock(); }
        }

        void pause() { paused = true; }
        void resume() { paused = false; }
        void stop() {
            lock.lock();
            try {
                stopped = true; paused = false;
                if (task != null) task.cancel(true);
            } finally { lock.unlock(); }
        }

        void applyPhase(int idx) {
            PhaseEntity p = sequence.getPhases().get(idx);
            currentStates = new EnumMap<>(p.getStates());
            remainingSeconds = p.getDurationSeconds();
        }

        void tick(HistoryRecorder recorder) {
            if (stopped || paused) return;
            lock.lock();
            try {
                remainingSeconds--;
                if (remainingSeconds <= 0) {
                    phaseIndex = (phaseIndex + 1) % sequence.getPhases().size();
                    applyPhase(phaseIndex);
                    recorder.record(id, phaseIndex, currentStates);
                }
            } finally { lock.unlock(); }
        }

        StateView view() {
            return new StateView(id, phaseIndex, currentStates, remainingSeconds);
        }
    }

    @FunctionalInterface
    interface HistoryRecorder {
        void record(String id, int phaseIndex, Map<Direction, LightColor> states);
    }

    public record StateView(String intersectionId, int phaseIndex, Map<Direction, LightColor> states, int remainingSeconds) {}
}
