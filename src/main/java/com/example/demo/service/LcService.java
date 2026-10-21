package com.example.demo.service;

import com.example.demo.dto.LcRequest;
import com.example.demo.dto.LcResponse;
import com.example.demo.entity.Lc;
import com.example.demo.mapper.LcMapper;
import com.example.demo.repository.LcRepository;
import com.example.demo.repository.PegawaiRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

@Service
public class LcService extends KegiatanCrudService<Lc, LcRequest, LcResponse> {

    private final LcMapper mapper;

    public LcService(LcRepository repository,
                     PegawaiRepository pegawaiRepository,
                     AuditorAware<Long> auditorAware,
                     LcMapper mapper) {
        super(repository, pegawaiRepository, auditorAware);
        this.mapper = mapper;
    }

    @Override
    protected Lc toEntity(LcRequest request) {
        return mapper.toEntity(request);
    }

    @Override
    protected void updateEntity(LcRequest request, Lc entity) {
        mapper.updateEntity(request, entity);
    }

    @Override
    protected LcResponse toResponse(Lc entity) {
        return mapper.toResponse(entity);
    }
}
