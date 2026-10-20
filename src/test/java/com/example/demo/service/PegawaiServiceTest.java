package com.example.demo.service;

import com.example.demo.dto.CursorPageResponse;
import com.example.demo.dto.Gender;
import com.example.demo.dto.PegawaiResponse;
import com.example.demo.entity.Pegawai;
import com.example.demo.exception.InvalidCursorException;
import com.example.demo.mapper.PegawaiMapper;
import com.example.demo.repository.PegawaiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PegawaiServiceTest {

    private static final PageRequest FIRST_PAGE = PageRequest.of(0, 20);

    @Mock
    private PegawaiRepository repository;

    @Mock
    private PegawaiMapper mapper;

    @Mock
    private AuditorAware<Long> auditorAware;

    private PegawaiService service;

    @BeforeEach
    void setUp() {
        service = new PegawaiService(repository, mapper, auditorAware);
    }

    @Test
    void searchWithoutCursorUsesFirstPageQuery() {
        Pegawai pegawai = pegawai(10L);
        when(repository.search("%%", FIRST_PAGE))
                .thenReturn(new SliceImpl<>(List.of(pegawai), FIRST_PAGE, false));
        stubMapper(pegawai);

        CursorPageResponse<PegawaiResponse> response = service.search(null, null);

        assertThat(response.content()).hasSize(1);
        assertThat(response.nextCursor()).isNull();
        verify(repository, never()).searchBeforeId(any(), any(), any());
    }

    @Test
    void searchWithCursorResolvesUuidToIdAndReturnsNextCursor() {
        Pegawai cursorRow = pegawai(30L);
        Pegawai first = pegawai(29L);
        Pegawai last = pegawai(20L);
        when(repository.findFirstByUuidOrderByIdAsc(cursorRow.getUuid()))
                .thenReturn(Optional.of(cursorRow));
        when(repository.searchBeforeId("%budi%", 30L, FIRST_PAGE))
                .thenReturn(new SliceImpl<>(List.of(first, last), FIRST_PAGE, true));
        stubMapper(first);
        stubMapper(last);

        CursorPageResponse<PegawaiResponse> response = service.search("budi", cursorRow.getUuid());

        assertThat(response.content()).hasSize(2);
        assertThat(response.nextCursor()).isEqualTo(last.getUuid());
    }

    @Test
    void searchWithUnknownCursorThrowsInvalidCursor() {
        UUID cursor = UUID.randomUUID();
        when(repository.findFirstByUuidOrderByIdAsc(cursor)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.search(null, cursor))
                .isInstanceOf(InvalidCursorException.class);

        verify(repository, never()).searchBeforeId(any(), any(), any());
    }

    @Test
    void searchLowercasesAndTrimsPattern() {
        Pegawai pegawai = pegawai(1L);
        when(repository.search("%budi%", FIRST_PAGE))
                .thenReturn(new SliceImpl<>(List.of(pegawai), FIRST_PAGE, false));
        stubMapper(pegawai);

        service.search("  Budi  ", null);

        verify(repository).search("%budi%", FIRST_PAGE);
    }

    @Test
    void lastPageHasNoNextCursor() {
        Pegawai pegawai = pegawai(1L);
        when(repository.search("%%", FIRST_PAGE))
                .thenReturn(new SliceImpl<>(List.of(pegawai), FIRST_PAGE, false));
        stubMapper(pegawai);

        CursorPageResponse<PegawaiResponse> response = service.search(null, null);

        assertThat(response.nextCursor()).isNull();
    }

    private void stubMapper(Pegawai row) {
        PegawaiResponse mapped = new PegawaiResponse(
                row.getUuid(), row.getNip(), row.getNama(), row.getTanggalLahir(),
                Gender.MALE, row.getEmail(), row.getNoTelepon(), row.getAlamat());
        when(mapper.toResponse(row)).thenReturn(mapped);
    }

    private static Pegawai pegawai(Long id) {
        Pegawai pegawai = new Pegawai();
        pegawai.setId(id);
        pegawai.setUuid(UUID.randomUUID());
        pegawai.setNip("nip-" + id);
        pegawai.setNama("Pegawai " + id);
        pegawai.setIsMale(true);
        return pegawai;
    }
}
