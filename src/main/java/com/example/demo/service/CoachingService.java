package com.example.demo.service;

import com.example.demo.dto.CoachingRequest;
import com.example.demo.dto.CoachingResponse;
import com.example.demo.entity.Coaching;
import com.example.demo.mapper.CoachingMapper;
import com.example.demo.repository.CoachingRepository;
import com.example.demo.repository.PegawaiRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

@Service
public class CoachingService extends KegiatanCrudService<Coaching, CoachingRequest, CoachingResponse> {

    private final CoachingMapper mapper;

    public CoachingService(CoachingRepository repository,
                           PegawaiRepository pegawaiRepository,
                           AuditorAware<Long> auditorAware,
                           CoachingMapper mapper) {
        super(repository, pegawaiRepository, auditorAware);
        this.mapper = mapper;
    }

    @Override
    protected Coaching toEntity(CoachingRequest request) {
        return mapper.toEntity(request);
    }

    @Override
    protected void updateEntity(CoachingRequest request, Coaching entity) {
        mapper.updateEntity(request, entity);
    }

    @Override
    protected CoachingResponse toResponse(Coaching entity) {
        return mapper.toResponse(entity);
    }
}
