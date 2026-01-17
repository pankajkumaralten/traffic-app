package com.example.traffic.dto;

import com.example.traffic.enums.Direction;
import com.example.traffic.enums.LightColor;

import java.util.Map;

public record StateView(
        String intersectionId,
        int phaseIndex,
        Map<Direction, LightColor> states,
        int remainingSeconds
) {}
