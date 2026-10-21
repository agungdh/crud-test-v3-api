package com.example.demo.repository;

import com.example.demo.entity.JenisPelatihan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JenisPelatihanRepository extends JpaRepository<JenisPelatihan, Long> {

    Optional<JenisPelatihan> findFirstByUuidOrderByIdAsc(UUID uuid);

    boolean existsByNama(String nama);

    @Query("""
            select j from JenisPelatihan j
            where :pattern = '%%'
               or lower(j.nama) like :pattern
            order by j.id desc
            """)
    Slice<JenisPelatihan> search(@Param("pattern") String pattern, Pageable pageable);

    @Query("""
            select j from JenisPelatihan j
            where (:pattern = '%%'
               or lower(j.nama) like :pattern)
              and j.id < :cursorId
            order by j.id desc
            """)
    Slice<JenisPelatihan> searchBeforeId(@Param("pattern") String pattern,
                                        @Param("cursorId") Long cursorId,
                                        Pageable pageable);
}
