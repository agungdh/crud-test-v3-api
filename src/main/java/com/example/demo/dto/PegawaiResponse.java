package com.example.demo.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PegawaiResponse(
        UUID uuid,
        String nip,
        String nama,
        LocalDate tanggalLahir,
        Gender gender,
        String email,
        String noTelepon,
        String alamat
) {
}
