package com.example.demo.service;

import com.example.demo.dto.CursorPageResponse;
import com.example.demo.dto.PpmRequest;
import com.example.demo.dto.PpmResponse;
import com.example.demo.entity.Pegawai;
import com.example.demo.entity.Ppm;
import com.example.demo.exception.InvalidCursorException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.PpmMapper;
import com.example.demo.repository.PegawaiRepository;
import com.example.demo.repository.PpmRepository;
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
public class PpmService {

    private static final int PAGE_SIZE = 20;

    private final PpmRepository repository;
    private final PegawaiRepository pegawaiRepository;
    private final PpmMapper mapper;
    private final AuditorAware<Long> auditorAware;

    @Transactional
    public PpmResponse create(PpmRequest request) {
        Ppm entity = mapper.toEntity(request);
        entity.setPegawai(getPegawai(request.pegawaiUuid()));
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public PpmResponse getByUuid(UUID uuid) {
        return mapper.toResponse(getActive(uuid));
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<PpmResponse> search(String search, UUID pegawaiUuid, UUID cursor) {
        String pattern = pattern(search);
        Long pegawaiId = resolvePegawaiId(pegawaiUuid);
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);

        Slice<Ppm> slice;
        if (pegawaiId == null) {
            slice = (cursor == null)
                    ? repository.search(pattern, pageRequest)
                    : repository.searchBeforeId(pattern, resolveCursorId(cursor), pageRequest);
        } else {
            slice = (cursor == null)
                    ? repository.searchByPegawai(pattern, pegawaiId, pageRequest)
                    : repository.searchByPegawaiBeforeId(pattern, pegawaiId, resolveCursorId(cursor), pageRequest);
        }

        List<Ppm> rows = slice.getContent();
        List<PpmResponse> content = rows.stream().map(mapper::toResponse).toList();
        UUID nextCursor = (slice.hasNext() && !rows.isEmpty()) ? rows.getLast().getUuid() : null;

        return new CursorPageResponse<>(content, nextCursor);
    }

    @Transactional
    public PpmResponse update(UUID uuid, PpmRequest request) {
        Ppm entity = getActive(uuid);
        mapper.updateEntity(request, entity);
        entity.setPegawai(getPegawai(request.pegawaiUuid()));
        return mapper.toResponse(entity);
    }

    @Transactional
    public void delete(UUID uuid) {
        Ppm entity = getActive(uuid);
        entity.setDeletedAt(Instant.now());
        auditorAware.getCurrentAuditor().ifPresent(entity::setDeletedBy);
    }

    private Ppm getActive(UUID uuid) {
        return repository.findFirstByUuidOrderByIdAsc(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Ppm not found: " + uuid));
    }

    private Long resolveCursorId(UUID cursor) {
        return repository.findFirstByUuidOrderByIdAsc(cursor)
                .map(Ppm::getId)
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
