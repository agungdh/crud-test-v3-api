package com.example.demo.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PpmResponse(
        UUID uuid,
        UUID pegawaiUuid,
        String nomorSurat,
        String materiPengembangan,
        LocalDate tanggalPelaksanaan,
        Integer jumlahJamPelatihan
) {
}
