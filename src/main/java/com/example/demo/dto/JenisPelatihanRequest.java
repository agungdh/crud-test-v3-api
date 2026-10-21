package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JenisPelatihanRequest(
        @NotBlank(message = "nama is required")
        @Size(max = 150, message = "nama must be at most 150 characters")
        String nama
) {
}
