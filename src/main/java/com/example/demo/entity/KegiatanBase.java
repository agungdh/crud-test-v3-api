package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Shared shape for the simple pegawai-scoped kegiatan rows (seminar, webinar, lc, ...).
 *
 * <p>Entities that need extra columns (diklat, ppm) extend {@link BaseEntity} directly.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class KegiatanBase extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pegawai_id", nullable = false)
    private Pegawai pegawai;

    @Column(name = "materi_pengembangan", nullable = false)
    private String materiPengembangan;

    @Column(name = "tanggal_pelaksanaan", nullable = false)
    private LocalDate tanggalPelaksanaan;

    @Column(name = "jumlah_jam", nullable = false)
    private Integer jumlahJam;
}
