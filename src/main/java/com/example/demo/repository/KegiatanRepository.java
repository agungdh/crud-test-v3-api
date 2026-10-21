package com.example.demo.repository;

import com.example.demo.entity.KegiatanBase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface KegiatanRepository<T extends KegiatanBase> extends JpaRepository<T, Long> {

    @EntityGraph(attributePaths = "pegawai")
    Optional<T> findFirstByUuidOrderByIdAsc(UUID uuid);

    @EntityGraph(attributePaths = "pegawai")
    @Query("""
            select e from #{#entityName} e
            where :pattern = '%%'
               or lower(e.materiPengembangan) like :pattern
            order by e.id desc
            """)
    Slice<T> search(@Param("pattern") String pattern, Pageable pageable);

    @EntityGraph(attributePaths = "pegawai")
    @Query("""
            select e from #{#entityName} e
            where (:pattern = '%%'
               or lower(e.materiPengembangan) like :pattern)
              and e.id < :cursorId
            order by e.id desc
            """)
    Slice<T> searchBeforeId(@Param("pattern") String pattern,
                            @Param("cursorId") Long cursorId,
                            Pageable pageable);

    @EntityGraph(attributePaths = "pegawai")
    @Query("""
            select e from #{#entityName} e
            where (:pattern = '%%'
               or lower(e.materiPengembangan) like :pattern)
              and e.pegawai.id = :pegawaiId
            order by e.id desc
            """)
    Slice<T> searchByPegawai(@Param("pattern") String pattern,
                             @Param("pegawaiId") Long pegawaiId,
                             Pageable pageable);

    @EntityGraph(attributePaths = "pegawai")
    @Query("""
            select e from #{#entityName} e
            where (:pattern = '%%'
               or lower(e.materiPengembangan) like :pattern)
              and e.pegawai.id = :pegawaiId
              and e.id < :cursorId
            order by e.id desc
            """)
    Slice<T> searchByPegawaiBeforeId(@Param("pattern") String pattern,
                                     @Param("pegawaiId") Long pegawaiId,
                                     @Param("cursorId") Long cursorId,
                                     Pageable pageable);
}
