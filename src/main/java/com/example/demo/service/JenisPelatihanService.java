package com.example.demo.service;

import com.example.demo.dto.CursorPageResponse;
import com.example.demo.dto.JenisPelatihanRequest;
import com.example.demo.dto.JenisPelatihanResponse;
import com.example.demo.entity.JenisPelatihan;
import com.example.demo.exception.InvalidCursorException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.JenisPelatihanMapper;
import com.example.demo.repository.JenisPelatihanRepository;
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
public class JenisPelatihanService {

    private static final int PAGE_SIZE = 20;

    private final JenisPelatihanRepository repository;
    private final JenisPelatihanMapper mapper;
    private final AuditorAware<Long> auditorAware;

    @Transactional
    public JenisPelatihanResponse create(JenisPelatihanRequest request) {
        JenisPelatihan entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public JenisPelatihanResponse getByUuid(UUID uuid) {
        return mapper.toResponse(getActive(uuid));
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<JenisPelatihanResponse> search(String search, UUID cursor) {
        String pattern = pattern(search);
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);

        Slice<JenisPelatihan> slice = (cursor == null)
                ? repository.search(pattern, pageRequest)
                : repository.searchBeforeId(pattern, resolveCursorId(cursor), pageRequest);

        List<JenisPelatihan> rows = slice.getContent();
        List<JenisPelatihanResponse> content = rows.stream().map(mapper::toResponse).toList();
        UUID nextCursor = (slice.hasNext() && !rows.isEmpty()) ? rows.getLast().getUuid() : null;

        return new CursorPageResponse<>(content, nextCursor);
    }

    @Transactional
    public JenisPelatihanResponse update(UUID uuid, JenisPelatihanRequest request) {
        JenisPelatihan entity = getActive(uuid);
        mapper.updateEntity(request, entity);
        return mapper.toResponse(entity);
    }

    @Transactional
    public void delete(UUID uuid) {
        JenisPelatihan entity = getActive(uuid);
        entity.setDeletedAt(Instant.now());
        auditorAware.getCurrentAuditor().ifPresent(entity::setDeletedBy);
    }

    private JenisPelatihan getActive(UUID uuid) {
        return repository.findFirstByUuidOrderByIdAsc(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Jenis pelatihan not found: " + uuid));
    }

    private Long resolveCursorId(UUID cursor) {
        return repository.findFirstByUuidOrderByIdAsc(cursor)
                .map(JenisPelatihan::getId)
                .orElseThrow(() -> new InvalidCursorException("Invalid cursor: " + cursor));
    }

    private static String pattern(String search) {
        return (search == null || search.isBlank())
                ? "%%"
                : "%" + search.trim().toLowerCase() + "%";
    }
}
