package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record BelajarMandiriRequest(
        @NotNull(message = "pegawai_uuid is required")
        UUID pegawaiUuid,

        @NotBlank(message = "materi_pengembangan is required")
        @Size(max = 255, message = "materi_pengembangan must be at most 255 characters")
        String materiPengembangan,

        @NotNull(message = "tanggal_pelaksanaan is required")
        LocalDate tanggalPelaksanaan,

        @NotNull(message = "jumlah_jam is required")
        @Min(value = 0, message = "jumlah_jam must not be negative")
        Integer jumlahJam
) implements PegawaiScopedRequest {
}
