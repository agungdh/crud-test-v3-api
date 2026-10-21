package com.example.demo.service;

import com.example.demo.dto.WorkshopRequest;
import com.example.demo.dto.WorkshopResponse;
import com.example.demo.entity.Workshop;
import com.example.demo.mapper.WorkshopMapper;
import com.example.demo.repository.PegawaiRepository;
import com.example.demo.repository.WorkshopRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

@Service
public class WorkshopService extends KegiatanCrudService<Workshop, WorkshopRequest, WorkshopResponse> {

    private final WorkshopMapper mapper;

    public WorkshopService(WorkshopRepository repository,
                           PegawaiRepository pegawaiRepository,
                           AuditorAware<Long> auditorAware,
                           WorkshopMapper mapper) {
        super(repository, pegawaiRepository, auditorAware);
        this.mapper = mapper;
    }

    @Override
    protected Workshop toEntity(WorkshopRequest request) {
        return mapper.toEntity(request);
    }

    @Override
    protected void updateEntity(WorkshopRequest request, Workshop entity) {
        mapper.updateEntity(request, entity);
    }

    @Override
    protected WorkshopResponse toResponse(Workshop entity) {
        return mapper.toResponse(entity);
    }
}
