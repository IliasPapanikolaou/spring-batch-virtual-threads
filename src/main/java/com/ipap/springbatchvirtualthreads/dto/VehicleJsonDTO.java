package com.ipap.springbatchvirtualthreads.dto;

public record VehicleJsonDTO(
        Integer id,
        String manufacturer,
        String model,
        Integer year,
        String owner,
        String price
) {
}
