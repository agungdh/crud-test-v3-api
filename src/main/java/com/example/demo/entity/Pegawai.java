package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "pegawai")
@SQLRestriction("deleted_at is null")
public class Pegawai extends BaseEntity {

    @Column(name = "nip", nullable = false, length = 30)
    private String nip;

    @Column(name = "nama", nullable = false, length = 150)
    private String nama;

    @Column(name = "tanggal_lahir", nullable = false)
    private LocalDate tanggalLahir;

    @Column(name = "is_male", nullable = false)
    private Boolean isMale;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "no_telepon", length = 30)
    private String noTelepon;

    @Column(name = "alamat", columnDefinition = "text")
    private String alamat;
}
