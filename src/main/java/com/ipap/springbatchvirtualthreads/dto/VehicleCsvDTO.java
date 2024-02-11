package com.ipap.springbatchvirtualthreads.dto;

public record VehicleCsvDTO(
        Integer id,
        String manufacturer,
        String model,
        String owner
) {
}
