package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "jenis_pelatihan")
public class JenisPelatihan extends BaseEntity {

    @Column(name = "nama", nullable = false, length = 150)
    private String nama;
}
