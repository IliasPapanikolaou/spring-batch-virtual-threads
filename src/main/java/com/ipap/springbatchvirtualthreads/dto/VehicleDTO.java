package com.ipap.springbatchvirtualthreads.dto;

public record VehicleDTO(
        Integer id,
        String manufacturer,
        String model,
        String owner
) {
}
