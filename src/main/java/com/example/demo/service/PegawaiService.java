package com.example.demo.service;

import com.example.demo.dto.PageResponse;
import com.example.demo.dto.PegawaiRequest;
import com.example.demo.dto.PegawaiResponse;
import com.example.demo.entity.Pegawai;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.PegawaiMapper;
import com.example.demo.repository.PegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PegawaiService {

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
    public PageResponse<PegawaiResponse> search(String search, Pageable pageable) {
        String pattern = (search == null || search.isBlank())
                ? "%%"
                : "%" + search.trim().toLowerCase() + "%";

        Page<PegawaiResponse> page = repository.search(pattern, pageable)
                .map(mapper::toResponse);
        return PageResponse.from(page);
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
}
