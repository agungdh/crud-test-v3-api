package com.example.demo.dto;

import java.util.UUID;

/**
 * Request payload that points at a pegawai through its public uuid.
 */
public interface PegawaiScopedRequest {

    UUID pegawaiUuid();
}
