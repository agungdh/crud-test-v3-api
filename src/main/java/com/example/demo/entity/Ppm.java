package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "ppm")
public class Ppm extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pegawai_id", nullable = false)
    private Pegawai pegawai;

    @Column(name = "nomor_surat", nullable = false, length = 150)
    private String nomorSurat;

    @Column(name = "materi_pengembangan", nullable = false)
    private String materiPengembangan;

    @Column(name = "tanggal_pelaksanaan", nullable = false)
    private LocalDate tanggalPelaksanaan;

    @Column(name = "jumlah_jam_pelatihan", nullable = false)
    private Integer jumlahJamPelatihan;
}
