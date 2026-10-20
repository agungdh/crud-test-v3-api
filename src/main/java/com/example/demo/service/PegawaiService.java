package com.example.demo.service;

import com.example.demo.dto.CursorPageResponse;
import com.example.demo.dto.PegawaiRequest;
import com.example.demo.dto.PegawaiResponse;
import com.example.demo.entity.Pegawai;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.InvalidCursorException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.PegawaiMapper;
import com.example.demo.repository.PegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PegawaiService {

    private static final int PAGE_SIZE = 20;

    private final PegawaiRepository repository;
    private final PegawaiMapper mapper;
    private final AuditorAware<Long> auditorAware;

    @Transactional
    public PegawaiResponse create(PegawaiRequest request) {
        if (repository.existsByNip(request.nip())) {
            throw new DuplicateResourceException("NIP already registered: " + request.nip());
        }

        Pegawai entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public PegawaiResponse getByUuid(UUID uuid) {
        return mapper.toResponse(getActive(uuid));
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<PegawaiResponse> search(String search, UUID cursor) {
        String pattern = (search == null || search.isBlank())
                ? "%%"
                : "%" + search.trim().toLowerCase() + "%";
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);

        Slice<Pegawai> slice = (cursor == null)
                ? repository.search(pattern, pageRequest)
                : repository.searchBeforeId(pattern, resolveCursorId(cursor), pageRequest);

        List<Pegawai> rows = slice.getContent();
        List<PegawaiResponse> content = rows.stream()
                .map(mapper::toResponse)
                .toList();
        UUID nextCursor = (slice.hasNext() && !rows.isEmpty())
                ? rows.getLast().getUuid()
                : null;

        return new CursorPageResponse<>(content, nextCursor);
    }

    @Transactional
    public PegawaiResponse update(UUID uuid, PegawaiRequest request) {
        Pegawai entity = getActive(uuid);

        if (!entity.getNip().equals(request.nip())
                && repository.existsByNipAndUuidNot(request.nip(), uuid)) {
            throw new DuplicateResourceException("NIP already registered: " + request.nip());
        }

        mapper.updateEntity(request, entity);
        return mapper.toResponse(entity);
    }

    @Transactional
    public void delete(UUID uuid) {
        Pegawai entity = getActive(uuid);
        entity.setDeletedAt(Instant.now());
        auditorAware.getCurrentAuditor().ifPresent(entity::setDeletedBy);
    }

    private Pegawai getActive(UUID uuid) {
        return repository.findFirstByUuidOrderByIdAsc(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Pegawai not found: " + uuid));
    }

    private Long resolveCursorId(UUID cursor) {
        return repository.findFirstByUuidOrderByIdAsc(cursor)
                .map(Pegawai::getId)
                .orElseThrow(() -> new InvalidCursorException("Invalid cursor: " + cursor));
    }
}
