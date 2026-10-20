package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PegawaiRequest(
        @NotBlank(message = "nip is required")
        @Size(max = 30, message = "nip must be at most 30 characters")
        String nip,

        @NotBlank(message = "nama is required")
        @Size(max = 150, message = "nama must be at most 150 characters")
        String nama,

        @NotNull(message = "tanggalLahir is required")
        @Past(message = "tanggalLahir must be in the past")
        LocalDate tanggalLahir,

        @NotNull(message = "gender is required")
        Gender gender,

        @Email(message = "email is not valid")
        @Size(max = 150, message = "email must be at most 150 characters")
        String email,

        @Size(max = 30, message = "noTelepon must be at most 30 characters")
        String noTelepon,

        @Size(max = 2000, message = "alamat must be at most 2000 characters")
        String alamat
) {
}
