package com.example.demo.service;

import com.example.demo.dto.MentoringRequest;
import com.example.demo.dto.MentoringResponse;
import com.example.demo.entity.Mentoring;
import com.example.demo.mapper.MentoringMapper;
import com.example.demo.repository.MentoringRepository;
import com.example.demo.repository.PegawaiRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;

@Service
public class MentoringService extends KegiatanCrudService<Mentoring, MentoringRequest, MentoringResponse> {

    private final MentoringMapper mapper;

    public MentoringService(MentoringRepository repository,
                            PegawaiRepository pegawaiRepository,
                            AuditorAware<Long> auditorAware,
                            MentoringMapper mapper) {
        super(repository, pegawaiRepository, auditorAware);
        this.mapper = mapper;
    }

    @Override
    protected Mentoring toEntity(MentoringRequest request) {
        return mapper.toEntity(request);
    }

    @Override
    protected void updateEntity(MentoringRequest request, Mentoring entity) {
        mapper.updateEntity(request, entity);
    }

    @Override
    protected MentoringResponse toResponse(Mentoring entity) {
        return mapper.toResponse(entity);
    }
}
