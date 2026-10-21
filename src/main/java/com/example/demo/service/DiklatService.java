package com.example.demo.service;

import com.example.demo.dto.CursorPageResponse;
import com.example.demo.dto.DiklatRequest;
import com.example.demo.dto.DiklatResponse;
import com.example.demo.entity.Diklat;
import com.example.demo.entity.JenisPelatihan;
import com.example.demo.entity.Pegawai;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.InvalidCursorException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.DiklatMapper;
import com.example.demo.repository.DiklatRepository;
import com.example.demo.repository.JenisPelatihanRepository;
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
public class DiklatService {

    private static final int PAGE_SIZE = 20;

    private final DiklatRepository repository;
    private final PegawaiRepository pegawaiRepository;
    private final JenisPelatihanRepository jenisPelatihanRepository;
    private final DiklatMapper mapper;
    private final AuditorAware<Long> auditorAware;

    @Transactional
    public DiklatResponse create(DiklatRequest request) {
        validateDateRange(request);

        Diklat entity = mapper.toEntity(request);
        entity.setPegawai(getPegawai(request.pegawaiUuid()));
        entity.setJenisPelatihan(getJenisPelatihan(request.jenisPelatihanUuid()));

        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public DiklatResponse getByUuid(UUID uuid) {
        return mapper.toResponse(getActive(uuid));
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<DiklatResponse> search(String search, UUID pegawaiUuid, UUID cursor) {
        String pattern = pattern(search);
        Long pegawaiId = resolvePegawaiId(pegawaiUuid);
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);

        Slice<Diklat> slice;
        if (pegawaiId == null) {
            slice = (cursor == null)
                    ? repository.search(pattern, pageRequest)
                    : repository.searchBeforeId(pattern, resolveCursorId(cursor), pageRequest);
        } else {
            slice = (cursor == null)
                    ? repository.searchByPegawai(pattern, pegawaiId, pageRequest)
                    : repository.searchByPegawaiBeforeId(pattern, pegawaiId, resolveCursorId(cursor), pageRequest);
        }

        List<Diklat> rows = slice.getContent();
        List<DiklatResponse> content = rows.stream().map(mapper::toResponse).toList();
        UUID nextCursor = (slice.hasNext() && !rows.isEmpty()) ? rows.getLast().getUuid() : null;

        return new CursorPageResponse<>(content, nextCursor);
    }

    @Transactional
    public DiklatResponse update(UUID uuid, DiklatRequest request) {
        validateDateRange(request);

        Diklat entity = getActive(uuid);
        mapper.updateEntity(request, entity);
        entity.setPegawai(getPegawai(request.pegawaiUuid()));
        entity.setJenisPelatihan(getJenisPelatihan(request.jenisPelatihanUuid()));

        return mapper.toResponse(entity);
    }

    @Transactional
    public void delete(UUID uuid) {
        Diklat entity = getActive(uuid);
        entity.setDeletedAt(Instant.now());
        auditorAware.getCurrentAuditor().ifPresent(entity::setDeletedBy);
    }

    private Diklat getActive(UUID uuid) {
        return repository.findFirstByUuidOrderByIdAsc(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Diklat not found: " + uuid));
    }

    private Long resolveCursorId(UUID cursor) {
        return repository.findFirstByUuidOrderByIdAsc(cursor)
                .map(Diklat::getId)
                .orElseThrow(() -> new InvalidCursorException("Invalid cursor: " + cursor));
    }

    private Long resolvePegawaiId(UUID pegawaiUuid) {
        return (pegawaiUuid == null) ? null : getPegawai(pegawaiUuid).getId();
    }

    private Pegawai getPegawai(UUID uuid) {
        return pegawaiRepository.findFirstByUuidOrderByIdAsc(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Pegawai not found: " + uuid));
    }

    private JenisPelatihan getJenisPelatihan(UUID uuid) {
        return jenisPelatihanRepository.findFirstByUuidOrderByIdAsc(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Jenis pelatihan not found: " + uuid));
    }

    private static void validateDateRange(DiklatRequest request) {
        if (request.dariTanggalPelaksanaan().isAfter(request.sampaiTanggalPelaksanaan())) {
            throw new BadRequestException(
                    "dari_tanggal_pelaksanaan must be before or equal to sampai_tanggal_pelaksanaan");
        }
    }

    private static String pattern(String search) {
        return (search == null || search.isBlank())
                ? "%%"
                : "%" + search.trim().toLowerCase() + "%";
    }
}
