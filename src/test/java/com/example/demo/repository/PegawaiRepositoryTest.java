package com.example.demo.repository;

import com.example.demo.entity.Pegawai;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PegawaiRepositoryTest {

    private static final PageRequest FIRST_PAGE = PageRequest.of(0, 20);

    @Autowired
    private PegawaiRepository repository;

    @Test
    void searchReturnsNewestFirstAndCursorPageCompletesTheResult() {
        String marker = "cursor-" + UUID.randomUUID();
        List<Long> insertedIds = Stream.generate(() -> repository.save(pegawai(marker)))
                .limit(25)
                .map(Pegawai::getId)
                .toList();

        Slice<Pegawai> firstPage = repository.search("%" + marker + "%", FIRST_PAGE);

        assertThat(firstPage.getContent()).hasSize(20);
        assertThat(firstPage.hasNext()).isTrue();
        assertThat(ids(firstPage)).isSortedAccordingTo(Comparator.reverseOrder());

        Long cursorId = repository.findFirstByUuidOrderByIdAsc(firstPage.getContent().getLast().getUuid())
                .orElseThrow()
                .getId();

        Slice<Pegawai> secondPage = repository.searchBeforeId("%" + marker + "%", cursorId, FIRST_PAGE);

        assertThat(secondPage.getContent()).hasSize(5);
        assertThat(secondPage.hasNext()).isFalse();
        assertThat(ids(secondPage)).isSortedAccordingTo(Comparator.reverseOrder());
        assertThat(ids(secondPage)).allMatch(id -> id < cursorId);
        assertThat(Stream.concat(ids(firstPage).stream(), ids(secondPage).stream()))
                .containsExactlyInAnyOrderElementsOf(insertedIds);
    }

    @Test
    void searchMatchesNamaOrNipAndHidesSoftDeletedRows() {
        String marker = "filter-" + UUID.randomUUID();
        Pegawai active = repository.save(pegawai(marker + "-active"));
        Pegawai deleted = repository.save(pegawai(marker + "-deleted"));
        deleted.setDeletedAt(java.time.Instant.now());
        repository.saveAndFlush(deleted);

        Slice<Pegawai> result = repository.search("%" + marker + "%", FIRST_PAGE);

        assertThat(ids(result)).containsExactly(active.getId());
    }

    private static List<Long> ids(Slice<Pegawai> slice) {
        return slice.getContent().stream().map(Pegawai::getId).toList();
    }

    private static Pegawai pegawai(String marker) {
        Pegawai pegawai = new Pegawai();
        pegawai.setNip(UUID.randomUUID().toString().substring(0, 30));
        pegawai.setNama("Pegawai " + marker);
        pegawai.setTanggalLahir(LocalDate.of(1990, 1, 1));
        pegawai.setIsMale(true);
        return pegawai;
    }
}
