package com.example.demo.repository;

import com.example.demo.entity.Diklat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DiklatRepository extends JpaRepository<Diklat, Long> {

    @EntityGraph(attributePaths = {"pegawai", "jenisPelatihan"})
    Optional<Diklat> findFirstByUuidOrderByIdAsc(UUID uuid);

    @EntityGraph(attributePaths = {"pegawai", "jenisPelatihan"})
    @Query("""
            select d from Diklat d
            where :pattern = '%%'
               or lower(d.materiPengembangan) like :pattern
               or lower(d.nomorSurat) like :pattern
            order by d.id desc
            """)
    Slice<Diklat> search(@Param("pattern") String pattern, Pageable pageable);

    @EntityGraph(attributePaths = {"pegawai", "jenisPelatihan"})
    @Query("""
            select d from Diklat d
            where (:pattern = '%%'
               or lower(d.materiPengembangan) like :pattern
               or lower(d.nomorSurat) like :pattern)
              and d.id < :cursorId
            order by d.id desc
            """)
    Slice<Diklat> searchBeforeId(@Param("pattern") String pattern,
                                 @Param("cursorId") Long cursorId,
                                 Pageable pageable);

    @EntityGraph(attributePaths = {"pegawai", "jenisPelatihan"})
    @Query("""
            select d from Diklat d
            where (:pattern = '%%'
               or lower(d.materiPengembangan) like :pattern
               or lower(d.nomorSurat) like :pattern)
              and d.pegawai.id = :pegawaiId
            order by d.id desc
            """)
    Slice<Diklat> searchByPegawai(@Param("pattern") String pattern,
                                  @Param("pegawaiId") Long pegawaiId,
                                  Pageable pageable);

    @EntityGraph(attributePaths = {"pegawai", "jenisPelatihan"})
    @Query("""
            select d from Diklat d
            where (:pattern = '%%'
               or lower(d.materiPengembangan) like :pattern
               or lower(d.nomorSurat) like :pattern)
              and d.pegawai.id = :pegawaiId
              and d.id < :cursorId
            order by d.id desc
            """)
    Slice<Diklat> searchByPegawaiBeforeId(@Param("pattern") String pattern,
                                          @Param("pegawaiId") Long pegawaiId,
                                          @Param("cursorId") Long cursorId,
                                          Pageable pageable);
}
