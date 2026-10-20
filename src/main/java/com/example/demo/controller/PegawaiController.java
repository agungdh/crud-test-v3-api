package com.example.demo.controller;

import com.example.demo.dto.PageResponse;
import com.example.demo.dto.PegawaiRequest;
import com.example.demo.dto.PegawaiResponse;
import com.example.demo.service.PegawaiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/pegawai")
@RequiredArgsConstructor
public class PegawaiController {

    private final PegawaiService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PegawaiResponse create(@Valid @RequestBody PegawaiRequest request) {
        return service.create(request);
    }

    @GetMapping
    public PageResponse<PegawaiResponse> search(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "nama") Pageable pageable
    ) {
        return service.search(search, pageable);
    }

    @GetMapping("/{uuid}")
    public PegawaiResponse getByUuid(@PathVariable UUID uuid) {
        return service.getByUuid(uuid);
    }

    @PutMapping("/{uuid}")
    public PegawaiResponse update(@PathVariable UUID uuid, @Valid @RequestBody PegawaiRequest request) {
        return service.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        service.delete(uuid);
    }
}
