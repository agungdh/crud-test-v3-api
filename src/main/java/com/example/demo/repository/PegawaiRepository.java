package com.example.demo.repository;

import com.example.demo.entity.Pegawai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PegawaiRepository extends JpaRepository<Pegawai, Long> {

    Optional<Pegawai> findFirstByUuidAndDeletedAtIsNullOrderByIdAsc(UUID uuid);

    boolean existsByNipAndDeletedAtIsNull(String nip);

    boolean existsByNipAndDeletedAtIsNullAndUuidNot(String nip, UUID uuid);

    @Query("""
            select p from Pegawai p
            where p.deletedAt is null
              and (:pattern = '%%'
                   or lower(p.nama) like :pattern
                   or p.nip like :pattern)
            """)
    Page<Pegawai> search(@Param("pattern") String pattern, Pageable pageable);
}
