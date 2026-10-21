package com.example.demo.dto;

import java.time.LocalDate;
import java.util.UUID;

public record SeminarResponse(
        UUID uuid,
        UUID pegawaiUuid,
        String materiPengembangan,
        LocalDate tanggalPelaksanaan,
        Integer jumlahJam
) {
}
