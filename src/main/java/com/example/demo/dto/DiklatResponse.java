package com.example.demo.dto;

import java.time.LocalDate;
import java.util.UUID;

public record DiklatResponse(
        UUID uuid,
        UUID pegawaiUuid,
        UUID jenisPelatihanUuid,
        String nomorSurat,
        String materiPengembangan,
        LocalDate dariTanggalPelaksanaan,
        LocalDate sampaiTanggalPelaksanaan,
        Integer jumlahJamPelatihan
) {
}
