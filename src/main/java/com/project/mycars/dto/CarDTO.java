package com.project.mycars.dto;

public record CarDTO(
        Integer year,
        String licensePlate,
        String model,
        String color
) {
}
