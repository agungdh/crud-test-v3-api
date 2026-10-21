package com.example.demo.service;

import com.example.demo.dto.CursorPageResponse;
import com.example.demo.dto.PegawaiScopedRequest;
import com.example.demo.entity.KegiatanBase;
import com.example.demo.entity.Pegawai;
import com.example.demo.exception.InvalidCursorException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.KegiatanRepository;
import com.example.demo.repository.PegawaiRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Shared CRUD and cursor paging for the simple pegawai-scoped kegiatan entities.
 *
 * <p>Concrete subclasses only wire the repository/mapper and implement the three
 * mapping hooks; transaction boundaries live here.
 */
public abstract class KegiatanCrudService<T extends KegiatanBase, RQ extends PegawaiScopedRequest, RS> {

    private static final int PAGE_SIZE = 20;

    private final KegiatanRepository<T> repository;
    private final PegawaiRepository pegawaiRepository;
    private final AuditorAware<Long> auditorAware;

    protected KegiatanCrudService(KegiatanRepository<T> repository,
                                  PegawaiRepository pegawaiRepository,
                                  AuditorAware<Long> auditorAware) {
        this.repository = repository;
        this.pegawaiRepository = pegawaiRepository;
        this.auditorAware = auditorAware;
    }

    protected abstract T toEntity(RQ request);

    protected abstract void updateEntity(RQ request, T entity);

    protected abstract RS toResponse(T entity);

    @Transactional
    public RS create(RQ request) {
        T entity = toEntity(request);
        entity.setPegawai(getPegawai(request.pegawaiUuid()));
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public RS getByUuid(UUID uuid) {
        return toResponse(getActive(uuid));
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<RS> search(String search, UUID pegawaiUuid, UUID cursor) {
        String pattern = pattern(search);
        Long pegawaiId = resolvePegawaiId(pegawaiUuid);
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);

        Slice<T> slice;
        if (pegawaiId == null) {
            slice = (cursor == null)
                    ? repository.search(pattern, pageRequest)
                    : repository.searchBeforeId(pattern, resolveCursorId(cursor), pageRequest);
        } else {
            slice = (cursor == null)
                    ? repository.searchByPegawai(pattern, pegawaiId, pageRequest)
                    : repository.searchByPegawaiBeforeId(pattern, pegawaiId, resolveCursorId(cursor), pageRequest);
        }

        List<T> rows = slice.getContent();
        List<RS> content = rows.stream().map(this::toResponse).toList();
        UUID nextCursor = (slice.hasNext() && !rows.isEmpty()) ? rows.getLast().getUuid() : null;

        return new CursorPageResponse<>(content, nextCursor);
    }

    @Transactional
    public RS update(UUID uuid, RQ request) {
        T entity = getActive(uuid);
        updateEntity(request, entity);
        entity.setPegawai(getPegawai(request.pegawaiUuid()));
        return toResponse(entity);
    }

    @Transactional
    public void delete(UUID uuid) {
        T entity = getActive(uuid);
        entity.setDeletedAt(Instant.now());
        auditorAware.getCurrentAuditor().ifPresent(entity::setDeletedBy);
    }

    private T getActive(UUID uuid) {
        return repository.findFirstByUuidOrderByIdAsc(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found: " + uuid));
    }

    private Long resolveCursorId(UUID cursor) {
        return repository.findFirstByUuidOrderByIdAsc(cursor)
                .map(KegiatanBase::getId)
                .orElseThrow(() -> new InvalidCursorException("Invalid cursor: " + cursor));
    }

    private Long resolvePegawaiId(UUID pegawaiUuid) {
        return (pegawaiUuid == null) ? null : getPegawai(pegawaiUuid).getId();
    }

    private Pegawai getPegawai(UUID uuid) {
        return pegawaiRepository.findFirstByUuidOrderByIdAsc(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Pegawai not found: " + uuid));
    }

    private static String pattern(String search) {
        return (search == null || search.isBlank())
                ? "%%"
                : "%" + search.trim().toLowerCase() + "%";
    }
}
