package com.example.demo.controller;

import com.example.demo.dto.CursorPageResponse;
import com.example.demo.dto.WebinarRequest;
import com.example.demo.dto.WebinarResponse;
import com.example.demo.service.WebinarService;
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
@RequestMapping("/webinar")
@RequiredArgsConstructor
public class WebinarController {

    private final WebinarService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WebinarResponse create(@Valid @RequestBody WebinarRequest request) {
        return service.create(request);
    }

    @GetMapping
    public CursorPageResponse<WebinarResponse> search(
            @RequestParam(required = false) String search,
            @RequestParam(name = "pegawai_uuid", required = false) UUID pegawaiUuid,
            @RequestParam(required = false) UUID cursor
    ) {
        return service.search(search, pegawaiUuid, cursor);
    }

    @GetMapping("/{uuid}")
    public WebinarResponse getByUuid(@PathVariable UUID uuid) {
        return service.getByUuid(uuid);
    }

    @PutMapping("/{uuid}")
    public WebinarResponse update(@PathVariable UUID uuid, @Valid @RequestBody WebinarRequest request) {
        return service.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        service.delete(uuid);
    }
}
