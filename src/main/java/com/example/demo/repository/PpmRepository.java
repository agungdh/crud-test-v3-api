package com.example.demo.repository;

import com.example.demo.entity.Ppm;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PpmRepository extends JpaRepository<Ppm, Long> {

    @EntityGraph(attributePaths = "pegawai")
    Optional<Ppm> findFirstByUuidOrderByIdAsc(UUID uuid);

    @EntityGraph(attributePaths = "pegawai")
    @Query("""
            select p from Ppm p
            where :pattern = '%%'
               or lower(p.materiPengembangan) like :pattern
               or lower(p.nomorSurat) like :pattern
            order by p.id desc
            """)
    Slice<Ppm> search(@Param("pattern") String pattern, Pageable pageable);

    @EntityGraph(attributePaths = "pegawai")
    @Query("""
            select p from Ppm p
            where (:pattern = '%%'
               or lower(p.materiPengembangan) like :pattern
               or lower(p.nomorSurat) like :pattern)
              and p.id < :cursorId
            order by p.id desc
            """)
    Slice<Ppm> searchBeforeId(@Param("pattern") String pattern,
                              @Param("cursorId") Long cursorId,
                              Pageable pageable);

    @EntityGraph(attributePaths = "pegawai")
    @Query("""
            select p from Ppm p
            where (:pattern = '%%'
               or lower(p.materiPengembangan) like :pattern
               or lower(p.nomorSurat) like :pattern)
              and p.pegawai.id = :pegawaiId
            order by p.id desc
            """)
    Slice<Ppm> searchByPegawai(@Param("pattern") String pattern,
                               @Param("pegawaiId") Long pegawaiId,
                               Pageable pageable);

    @EntityGraph(attributePaths = "pegawai")
    @Query("""
            select p from Ppm p
            where (:pattern = '%%'
               or lower(p.materiPengembangan) like :pattern
               or lower(p.nomorSurat) like :pattern)
              and p.pegawai.id = :pegawaiId
              and p.id < :cursorId
            order by p.id desc
            """)
    Slice<Ppm> searchByPegawaiBeforeId(@Param("pattern") String pattern,
                                       @Param("pegawaiId") Long pegawaiId,
                                       @Param("cursorId") Long cursorId,
                                       Pageable pageable);
}
