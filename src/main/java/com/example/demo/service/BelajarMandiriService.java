package com.example.demo.service;

import com.example.demo.dto.BelajarMandiriRequest;
import com.example.demo.dto.BelajarMandiriResponse;
import com.example.demo.entity.BelajarMandiri;
import com.example.demo.mapper.BelajarMandiriMapper;
import com.example.demo.repository.BelajarMandiriRepository;
import com.example.demo.repository.PegawaiRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

@Service
public class BelajarMandiriService
        extends KegiatanCrudService<BelajarMandiri, BelajarMandiriRequest, BelajarMandiriResponse> {

    private final BelajarMandiriMapper mapper;

    public BelajarMandiriService(BelajarMandiriRepository repository,
                                 PegawaiRepository pegawaiRepository,
                                 AuditorAware<Long> auditorAware,
                                 BelajarMandiriMapper mapper) {
        super(repository, pegawaiRepository, auditorAware);
        this.mapper = mapper;
    }

    @Override
    protected BelajarMandiri toEntity(BelajarMandiriRequest request) {
        return mapper.toEntity(request);
    }

    @Override
    protected void updateEntity(BelajarMandiriRequest request, BelajarMandiri entity) {
        mapper.updateEntity(request, entity);
    }

    @Override
    protected BelajarMandiriResponse toResponse(BelajarMandiri entity) {
        return mapper.toResponse(entity);
    }
}
