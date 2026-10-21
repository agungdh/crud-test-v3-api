package com.example.demo.controller;

import com.example.demo.dto.CursorPageResponse;
import com.example.demo.dto.JenisPelatihanRequest;
import com.example.demo.dto.JenisPelatihanResponse;
import com.example.demo.service.JenisPelatihanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/jenis-pelatihan")
@RequiredArgsConstructor
public class JenisPelatihanController {

    private final JenisPelatihanService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JenisPelatihanResponse create(@Valid @RequestBody JenisPelatihanRequest request) {
        return service.create(request);
    }

    @GetMapping
    public CursorPageResponse<JenisPelatihanResponse> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID cursor
    ) {
        return service.search(search, cursor);
    }

    @GetMapping("/{uuid}")
    public JenisPelatihanResponse getByUuid(@PathVariable UUID uuid) {
        return service.getByUuid(uuid);
    }

    @PutMapping("/{uuid}")
    public JenisPelatihanResponse update(@PathVariable UUID uuid,
                                         @Valid @RequestBody JenisPelatihanRequest request) {
        return service.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        service.delete(uuid);
    }
}
