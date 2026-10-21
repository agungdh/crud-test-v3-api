package com.example.demo.service;

import com.example.demo.dto.SeminarRequest;
import com.example.demo.dto.SeminarResponse;
import com.example.demo.entity.Seminar;
import com.example.demo.mapper.SeminarMapper;
import com.example.demo.repository.PegawaiRepository;
import com.example.demo.repository.SeminarRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

@Service
public class SeminarService extends KegiatanCrudService<Seminar, SeminarRequest, SeminarResponse> {

    private final SeminarMapper mapper;

    public SeminarService(SeminarRepository repository,
                          PegawaiRepository pegawaiRepository,
                          AuditorAware<Long> auditorAware,
                          SeminarMapper mapper) {
        super(repository, pegawaiRepository, auditorAware);
        this.mapper = mapper;
    }

    @Override
    protected Seminar toEntity(SeminarRequest request) {
        return mapper.toEntity(request);
    }

    @Override
    protected void updateEntity(SeminarRequest request, Seminar entity) {
        mapper.updateEntity(request, entity);
    }

    @Override
    protected SeminarResponse toResponse(Seminar entity) {
        return mapper.toResponse(entity);
    }
}
