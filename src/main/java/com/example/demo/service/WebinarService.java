package com.example.demo.service;

import com.example.demo.dto.WebinarRequest;
import com.example.demo.dto.WebinarResponse;
import com.example.demo.entity.Webinar;
import com.example.demo.mapper.WebinarMapper;
import com.example.demo.repository.PegawaiRepository;
import com.example.demo.repository.WebinarRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

@Service
public class WebinarService extends KegiatanCrudService<Webinar, WebinarRequest, WebinarResponse> {

    private final WebinarMapper mapper;

    public WebinarService(WebinarRepository repository,
                          PegawaiRepository pegawaiRepository,
                          AuditorAware<Long> auditorAware,
                          WebinarMapper mapper) {
        super(repository, pegawaiRepository, auditorAware);
        this.mapper = mapper;
    }

    @Override
    protected Webinar toEntity(WebinarRequest request) {
        return mapper.toEntity(request);
    }

    @Override
    protected void updateEntity(WebinarRequest request, Webinar entity) {
        mapper.updateEntity(request, entity);
    }

    @Override
    protected WebinarResponse toResponse(Webinar entity) {
        return mapper.toResponse(entity);
    }
}
