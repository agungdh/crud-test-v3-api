package com.example.demo.repository;

import com.example.demo.entity.Pegawai;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PegawaiRepository extends JpaRepository<Pegawai, Long> {

    Optional<Pegawai> findFirstByUuidOrderByIdAsc(UUID uuid);

    boolean existsByNip(String nip);

    boolean existsByNipAndUuidNot(String nip, UUID uuid);

    @Query("""
            select p from Pegawai p
            where :pattern = '%%'
               or lower(p.nama) like :pattern
               or p.nip like :pattern
            order by p.id desc
            """)
    Slice<Pegawai> search(@Param("pattern") String pattern, Pageable pageable);

    @Query("""
            select p from Pegawai p
            where (:pattern = '%%'
               or lower(p.nama) like :pattern
               or p.nip like :pattern)
              and p.id < :cursorId
            order by p.id desc
            """)
    Slice<Pegawai> searchBeforeId(@Param("pattern") String pattern,
                                  @Param("cursorId") Long cursorId,
                                  Pageable pageable);
}
