package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record PpmRequest(
        @NotNull(message = "pegawai_uuid is required")
        UUID pegawaiUuid,

        @NotBlank(message = "nomor_surat is required")
        @Size(max = 150, message = "nomor_surat must be at most 150 characters")
        String nomorSurat,

        @NotBlank(message = "materi_pengembangan is required")
        @Size(max = 255, message = "materi_pengembangan must be at most 255 characters")
        String materiPengembangan,

        @NotNull(message = "tanggal_pelaksanaan is required")
        LocalDate tanggalPelaksanaan,

        @NotNull(message = "jumlah_jam_pelatihan is required")
        @Min(value = 0, message = "jumlah_jam_pelatihan must not be negative")
        Integer jumlahJamPelatihan
) {
}
