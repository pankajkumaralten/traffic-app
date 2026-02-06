package com.example.traffic.controller;

import com.example.traffic.dto.*;
import com.example.traffic.entity.HistoryEntryEntity;
import com.example.traffic.entity.Intersection;
import com.example.traffic.service.IntersectionRegistry;
import com.example.traffic.service.TrafficLightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/intersections")
@Validated
public class TrafficLightController {
    @Autowired
    private  IntersectionRegistry registry;
    @Autowired
    private  TrafficLightService controller;


    @PostMapping
    public ResponseEntity<?> createIntersection(@RequestBody CreateIntersectionRequest req) {
        Intersection inter = registry.createOrUpdate(req.getIntersectionId(), req.getSequence());
        return ResponseEntity.ok(new Message("created_or_updated", inter.getId()));
    }

    @PutMapping("/{id}/sequence")
    public ResponseEntity<?> updateSequence(@PathVariable String id, @RequestBody UpdateSequenceRequest req) {
        if (registry.get(id).isEmpty()) return ResponseEntity.notFound().build();
        Intersection inter = registry.createOrUpdate(id, req.getSequence());
        return ResponseEntity.ok(new Message("sequence_updated", inter.getId()));
    }

    @PostMapping("/{id}/control")
    public ResponseEntity<?> control(@PathVariable String id, @RequestBody ControlRequest req) {
        if (registry.get(id).isEmpty()) return ResponseEntity.notFound().build();
        String action = req.getAction() == null ? "" : req.getAction().toLowerCase();
        switch (action) {
            case "start" -> controller.start(id, req.getPhaseIndex());
            case "pause" -> controller.pause(id);
            case "resume" -> controller.resume(id);
            case "stop" -> controller.stop(id);
            default -> { return ResponseEntity.badRequest().body(new Message("invalid_action", action)); }
        }
        return ResponseEntity.ok(new Message(action + "_ok", id));
    }

    @GetMapping("/{id}/state")
    public ResponseEntity<StateResponse> getState(@PathVariable String id) {
        return controller.getState(id)
                .map(v -> ResponseEntity.ok(new StateResponse(v.intersectionId(), v.phaseIndex(), v.states(), v.remainingSeconds())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<HistoryResponse> getHistory(@PathVariable String id) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_INSTANT;
       return registry.get(id)
                .map(inter -> controller.getHistory(id))
                .map(list -> list.stream().limit(100)
                        .map(h -> new HistoryEntryDto(fmt.format(h.getTimestamp()), h.getPhaseIndex(), h.getStates()))
                        .toList())
                .map(entries -> ResponseEntity.ok(new HistoryResponse(id, entries)))
                .orElseGet(() -> ResponseEntity.notFound().build());


    }

    @GetMapping
    public ResponseEntity<?> listIntersections() {
        return ResponseEntity.ok(new Intersections(controller.listIds()));
    }

    static class Message {
        private final String message;
        private final String intersectionId;
        public Message(String message, String intersectionId) { this.message = message; this.intersectionId = intersectionId; }
        public String getMessage() { return message; }
        public String getIntersectionId() { return intersectionId; }
    }

    static class Intersections {
        private final List<String> intersections;
        public Intersections(List<String> intersections) { this.intersections = intersections; }
        public List<String> getIntersections() { return intersections; }
    }
}
