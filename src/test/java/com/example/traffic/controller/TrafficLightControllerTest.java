package com.example.traffic.controller;

import com.example.traffic.dto.ControlRequest;
import com.example.traffic.dto.SequenceDto;
import com.example.traffic.dto.UpdateSequenceRequest;
import com.example.traffic.entity.HistoryEntryEntity;
import com.example.traffic.entity.Intersection;
import com.example.traffic.service.IntersectionRegistry;
import com.example.traffic.service.TrafficLightService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrafficLightController.class)
class TrafficLightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IntersectionRegistry registry;

    @MockBean
    private TrafficLightService controller;

    // --- CREATE INTERSECTION ---
    @Test
    void testCreateIntersection() throws Exception {
        Intersection inter = new Intersection("A1");
        inter.setId("A1"); // ensure ID is set

        Mockito.when(registry.createOrUpdate(
                        Mockito.eq("A1"),
                        Mockito.any(SequenceDto.class)))
                .thenReturn(inter);

        String json = """
            {"intersectionId":"A1","sequence":{"phaseIndex":0,"states":["red","green"]}}
            """;

        mockMvc.perform(post("/api/intersections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("created_or_updated"))
                .andExpect(jsonPath("$.intersectionId").value("A1"));
    }

    // --- UPDATE SEQUENCE ---
    @Test
    void testUpdateSequenceNotFound() throws Exception {
        Mockito.when(registry.get("A1")).thenReturn(Optional.empty());

        String json = """
            {"sequence":{"phaseIndex":1,"states":["green","yellow"]}}
            """;

        mockMvc.perform(put("/api/intersections/A1/sequence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateSequenceSuccess() throws Exception {
        Intersection inter = new Intersection("A1");
        inter.setId("A1");

        Mockito.when(registry.get("A1")).thenReturn(Optional.of(inter));
        Mockito.when(registry.createOrUpdate(
                        Mockito.eq("A1"),
                        Mockito.any(SequenceDto.class)))
                .thenReturn(inter);

        String json = """
            {"sequence":{"phaseIndex":1,"states":["green","yellow"]}}
            """;

        mockMvc.perform(put("/api/intersections/A1/sequence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("sequence_updated"))
                .andExpect(jsonPath("$.intersectionId").value("A1"));
    }

    // --- CONTROL ---
    @Test
    void testControlInvalidAction() throws Exception {
        Intersection inter = new Intersection("A1");
        inter.setId("A1");
        Mockito.when(registry.get("A1")).thenReturn(Optional.of(inter));

        String json = """
            {"action":"invalid"}
            """;

        mockMvc.perform(post("/api/intersections/A1/control")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("invalid_action"))
                .andExpect(jsonPath("$.intersectionId").value("invalid"));
    }

    @Test
    void testControlStart() throws Exception {
        Intersection inter = new Intersection("A1");
        inter.setId("A1");
        Mockito.when(registry.get("A1")).thenReturn(Optional.of(inter));

        String json = """
            {"action":"start","phaseIndex":0}
            """;

        mockMvc.perform(post("/api/intersections/A1/control")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("start_ok"))
                .andExpect(jsonPath("$.intersectionId").value("A1"));
    }

    @Test
    void testControlPause() throws Exception {
        Intersection inter = new Intersection("A1");
        inter.setId("A1");
        Mockito.when(registry.get("A1")).thenReturn(Optional.of(inter));

        String json = """
            {"action":"pause"}
            """;

        mockMvc.perform(post("/api/intersections/A1/control")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pause_ok"))
                .andExpect(jsonPath("$.intersectionId").value("A1"));
    }

    @Test
    void testControlResume() throws Exception {
        Intersection inter = new Intersection("A1");
        inter.setId("A1");
        Mockito.when(registry.get("A1")).thenReturn(Optional.of(inter));

        String json = """
            {"action":"resume"}
            """;

        mockMvc.perform(post("/api/intersections/A1/control")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("resume_ok"))
                .andExpect(jsonPath("$.intersectionId").value("A1"));
    }

    @Test
    void testControlStop() throws Exception {
        Intersection inter = new Intersection("A1");
        inter.setId("A1");
        Mockito.when(registry.get("A1")).thenReturn(Optional.of(inter));

        String json = """
            {"action":"stop"}
            """;

        mockMvc.perform(post("/api/intersections/A1/control")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("stop_ok"))
                .andExpect(jsonPath("$.intersectionId").value("A1"));
    }

    // --- GET STATE ---
    @Test
    void testGetStateNotFound() throws Exception {
        Mockito.when(controller.getState("A1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/intersections/A1/state"))
                .andExpect(status().isNotFound());
    }

    // --- GET HISTORY ---
    @Test
    void testGetHistoryNotFound() throws Exception {
        Mockito.when(registry.get("A1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/intersections/A1/history"))
                .andExpect(status().isNotFound());
    }



    // --- LIST INTERSECTIONS ---
    @Test
    void testListIntersections() throws Exception {
        Mockito.when(controller.listIds()).thenReturn(List.of("A1", "B2"));

        mockMvc.perform(get("/api/intersections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intersections[0]").value("A1"))
                .andExpect(jsonPath("$.intersections[1]").value("B2"));
    }
}
