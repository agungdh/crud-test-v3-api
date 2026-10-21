package com.example.demo;

import com.example.demo.dto.BelajarMandiriRequest;
import com.example.demo.dto.CoachingRequest;
import com.example.demo.dto.CursorPageResponse;
import com.example.demo.dto.DiklatRequest;
import com.example.demo.dto.DiklatResponse;
import com.example.demo.dto.JenisPelatihanRequest;
import com.example.demo.dto.JenisPelatihanResponse;
import com.example.demo.dto.LcRequest;
import com.example.demo.dto.MentoringRequest;
import com.example.demo.dto.PpmRequest;
import com.example.demo.dto.PpmResponse;
import com.example.demo.dto.SeminarRequest;
import com.example.demo.dto.SeminarResponse;
import com.example.demo.dto.WebinarRequest;
import com.example.demo.dto.WorkshopRequest;
import com.example.demo.entity.JenisPelatihan;
import com.example.demo.entity.Pegawai;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.InvalidCursorException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.JenisPelatihanRepository;
import com.example.demo.repository.PegawaiRepository;
import com.example.demo.service.BelajarMandiriService;
import com.example.demo.service.CoachingService;
import com.example.demo.service.DiklatService;
import com.example.demo.service.JenisPelatihanService;
import com.example.demo.service.LcService;
import com.example.demo.service.MentoringService;
import com.example.demo.service.PpmService;
import com.example.demo.service.SeminarService;
import com.example.demo.service.WebinarService;
import com.example.demo.service.WorkshopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Exercises the full CRUD stack (controller-level DTOs, mappers, services, repositories and
 * Flyway schema) for every pegawai-scoped kegiatan resource plus the jenis_pelatihan lookup.
 *
 * <p>The test runs against the real Postgres instance and rolls back afterwards.
 */
@SpringBootTest
@Transactional
class CrudKegiatanIntegrationTest {

    @Autowired
    private PegawaiRepository pegawaiRepository;

    @Autowired
    private JenisPelatihanRepository jenisPelatihanRepository;

    @Autowired
    private JenisPelatihanService jenisPelatihanService;

    @Autowired
    private DiklatService diklatService;

    @Autowired
    private PpmService ppmService;

    @Autowired
    private SeminarService seminarService;

    @Autowired
    private WebinarService webinarService;

    @Autowired
    private LcService lcService;

    @Autowired
    private BelajarMandiriService belajarMandiriService;

    @Autowired
    private MentoringService mentoringService;

    @Autowired
    private CoachingService coachingService;

    @Autowired
    private WorkshopService workshopService;

    private Pegawai pegawaiA;
    private Pegawai pegawaiB;
    private JenisPelatihan jenisPelatihan;

    @BeforeEach
    void setUp() {
        pegawaiA = pegawaiRepository.save(pegawai("a"));
        pegawaiB = pegawaiRepository.save(pegawai("b"));
        jenisPelatihan = jenisPelatihanRepository.save(jenisPelatihan());
    }

    @Test
    void crudWorksForEveryResource() {
        for (Resource resource : resources()) {
            String marker = marker("crud", resource.name());

            UUID uuid = resource.create(marker, pegawaiA.getUuid(), jenisPelatihan.getUuid());

            Object created = resource.get(uuid);
            if (resource.hasPegawai()) {
                assertThat(resource.pegawaiUuidOf(created)).isEqualTo(pegawaiA.getUuid());
            }
            assertThat(resource.search(marker, null, null).content()).hasSize(1);

            String updatedMarker = marker + "-updated";
            UUID updatedUuid = resource.update(uuid, updatedMarker, pegawaiB.getUuid(), jenisPelatihan.getUuid());

            assertThat(updatedUuid).isEqualTo(uuid);
            if (resource.hasPegawai()) {
                assertThat(resource.pegawaiUuidOf(resource.get(uuid))).isEqualTo(pegawaiB.getUuid());
            }
            assertThat(resource.search(updatedMarker, null, null).content()).hasSize(1);

            resource.delete(uuid);

            assertThatThrownBy(() -> resource.get(uuid)).isInstanceOf(ResourceNotFoundException.class);
            assertThat(resource.search(updatedMarker, null, null).content()).isEmpty();
        }
    }

    @Test
    void cursorPaginationWorksForEveryResource() {
        for (Resource resource : resources()) {
            String marker = marker("page", resource.name());
            for (int i = 0; i < 21; i++) {
                resource.create(marker, pegawaiA.getUuid(), jenisPelatihan.getUuid());
            }

            CursorPageResponse<?> firstPage = resource.search(marker, null, null);
            assertThat(firstPage.content()).hasSize(20);
            assertThat(firstPage.nextCursor()).isNotNull();

            CursorPageResponse<?> secondPage = resource.search(marker, null, firstPage.nextCursor());
            assertThat(secondPage.content()).hasSize(1);
            assertThat(secondPage.nextCursor()).isNull();
        }
    }

    @Test
    void searchFiltersByPegawaiForEveryPegawaiScopedResource() {
        for (Resource resource : resources()) {
            if (!resource.hasPegawai()) {
                continue;
            }

            String marker = marker("filter", resource.name());
            resource.create(marker, pegawaiA.getUuid(), jenisPelatihan.getUuid());
            resource.create(marker, pegawaiB.getUuid(), jenisPelatihan.getUuid());

            assertThat(resource.search(marker, null, null).content()).hasSize(2);
            assertThat(resource.search(marker, pegawaiA.getUuid(), null).content()).hasSize(1);
            assertThat(resource.search(marker, pegawaiB.getUuid(), null).content()).hasSize(1);
        }
    }

    @Test
    void creatingWithUnknownPegawaiUuidFailsWithNotFound() {
        for (Resource resource : resources()) {
            if (!resource.hasPegawai()) {
                continue;
            }

            assertThatThrownBy(() -> resource.create(marker("missing", resource.name()), UUID.randomUUID(),
                    jenisPelatihan.getUuid()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    void searchingWithUnknownCursorFailsWithInvalidCursor() {
        assertThatThrownBy(() -> seminarService.search(null, null, UUID.randomUUID()))
                .isInstanceOf(InvalidCursorException.class);
    }

    @Test
    void diklatValidatesDateRangeAndRelations() {
        assertThatThrownBy(() -> diklatService.create(new DiklatRequest(
                pegawaiA.getUuid(),
                jenisPelatihan.getUuid(),
                "SRT-1",
                "materi",
                LocalDate.of(2026, 2, 2),
                LocalDate.of(2026, 2, 1),
                4
        ))).isInstanceOf(BadRequestException.class);

        assertThatThrownBy(() -> diklatService.create(new DiklatRequest(
                pegawaiA.getUuid(),
                UUID.randomUUID(),
                "SRT-2",
                "materi",
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 2),
                4
        ))).isInstanceOf(ResourceNotFoundException.class);
    }

    private List<Resource> resources() {
        return List.of(
                new DiklatResource(),
                new PpmResource(),
                new SeminarResource(),
                new WebinarResource(),
                new LcResource(),
                new BelajarMandiriResource(),
                new MentoringResource(),
                new CoachingResource(),
                new WorkshopResource(),
                new JenisPelatihanResource()
        );
    }

    private static String marker(String prefix, String name) {
        return (prefix + "-" + name + "-" + UUID.randomUUID()).toLowerCase();
    }

    private static Pegawai pegawai(String suffix) {
        Pegawai pegawai = new Pegawai();
        pegawai.setNip(UUID.randomUUID().toString().substring(0, 30));
        pegawai.setNama("Pegawai " + suffix + " " + UUID.randomUUID());
        pegawai.setTanggalLahir(LocalDate.of(1990, 1, 1));
        pegawai.setIsMale(true);
        return pegawai;
    }

    private static JenisPelatihan jenisPelatihan() {
        JenisPelatihan entity = new JenisPelatihan();
        entity.setNama("Jenis " + UUID.randomUUID());
        return entity;
    }

    private static DiklatRequest diklatRequest(String marker, UUID pegawaiUuid, UUID jenisUuid) {
        return new DiklatRequest(
                pegawaiUuid,
                jenisUuid,
                "SRT-" + marker,
                marker,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 2),
                8
        );
    }

    private interface Resource {

        String name();

        UUID create(String marker, UUID pegawaiUuid, UUID jenisPelatihanUuid);

        Object get(UUID uuid);

        UUID update(UUID uuid, String marker, UUID pegawaiUuid, UUID jenisPelatihanUuid);

        CursorPageResponse<?> search(String search, UUID pegawaiUuid, UUID cursor);

        UUID pegawaiUuidOf(Object response);

        void delete(UUID uuid);

        default boolean hasPegawai() {
            return true;
        }
    }

    private final class DiklatResource implements Resource {

        @Override
        public String name() {
            return "diklat";
        }

        @Override
        public UUID create(String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return diklatService.create(diklatRequest(marker, pegawaiUuid, jenisUuid)).uuid();
        }

        @Override
        public Object get(UUID uuid) {
            return diklatService.getByUuid(uuid);
        }

        @Override
        public UUID update(UUID uuid, String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return diklatService.update(uuid, diklatRequest(marker, pegawaiUuid, jenisUuid)).uuid();
        }

        @Override
        public CursorPageResponse<?> search(String search, UUID pegawaiUuid, UUID cursor) {
            return diklatService.search(search, pegawaiUuid, cursor);
        }

        @Override
        public UUID pegawaiUuidOf(Object response) {
            return ((DiklatResponse) response).pegawaiUuid();
        }

        @Override
        public void delete(UUID uuid) {
            diklatService.delete(uuid);
        }
    }

    private final class PpmResource implements Resource {

        @Override
        public String name() {
            return "ppm";
        }

        @Override
        public UUID create(String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return ppmService.create(new PpmRequest(pegawaiUuid, "SRT-" + marker, marker,
                    LocalDate.of(2026, 1, 1), 8)).uuid();
        }

        @Override
        public Object get(UUID uuid) {
            return ppmService.getByUuid(uuid);
        }

        @Override
        public UUID update(UUID uuid, String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return ppmService.update(uuid, new PpmRequest(pegawaiUuid, "SRT-" + marker, marker,
                    LocalDate.of(2026, 1, 2), 4)).uuid();
        }

        @Override
        public CursorPageResponse<?> search(String search, UUID pegawaiUuid, UUID cursor) {
            return ppmService.search(search, pegawaiUuid, cursor);
        }

        @Override
        public UUID pegawaiUuidOf(Object response) {
            return ((PpmResponse) response).pegawaiUuid();
        }

        @Override
        public void delete(UUID uuid) {
            ppmService.delete(uuid);
        }
    }

    private final class SeminarResource implements Resource {

        @Override
        public String name() {
            return "seminar";
        }

        @Override
        public UUID create(String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return seminarService.create(new SeminarRequest(pegawaiUuid, marker,
                    LocalDate.of(2026, 1, 1), 8)).uuid();
        }

        @Override
        public Object get(UUID uuid) {
            return seminarService.getByUuid(uuid);
        }

        @Override
        public UUID update(UUID uuid, String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return seminarService.update(uuid, new SeminarRequest(pegawaiUuid, marker,
                    LocalDate.of(2026, 1, 2), 4)).uuid();
        }

        @Override
        public CursorPageResponse<?> search(String search, UUID pegawaiUuid, UUID cursor) {
            return seminarService.search(search, pegawaiUuid, cursor);
        }

        @Override
        public UUID pegawaiUuidOf(Object response) {
            return ((SeminarResponse) response).pegawaiUuid();
        }

        @Override
        public void delete(UUID uuid) {
            seminarService.delete(uuid);
        }
    }

    private final class WebinarResource implements Resource {

        @Override
        public String name() {
            return "webinar";
        }

        @Override
        public UUID create(String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return webinarService.create(new WebinarRequest(pegawaiUuid, marker,
                    LocalDate.of(2026, 1, 1), 8)).uuid();
        }

        @Override
        public Object get(UUID uuid) {
            return webinarService.getByUuid(uuid);
        }

        @Override
        public UUID update(UUID uuid, String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return webinarService.update(uuid, new WebinarRequest(pegawaiUuid, marker,
                    LocalDate.of(2026, 1, 2), 4)).uuid();
        }

        @Override
        public CursorPageResponse<?> search(String search, UUID pegawaiUuid, UUID cursor) {
            return webinarService.search(search, pegawaiUuid, cursor);
        }

        @Override
        public UUID pegawaiUuidOf(Object response) {
            return ((com.example.demo.dto.WebinarResponse) response).pegawaiUuid();
        }

        @Override
        public void delete(UUID uuid) {
            webinarService.delete(uuid);
        }
    }

    private final class LcResource implements Resource {

        @Override
        public String name() {
            return "lc";
        }

        @Override
        public UUID create(String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return lcService.create(new LcRequest(pegawaiUuid, marker,
                    LocalDate.of(2026, 1, 1), 8)).uuid();
        }

        @Override
        public Object get(UUID uuid) {
            return lcService.getByUuid(uuid);
        }

        @Override
        public UUID update(UUID uuid, String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return lcService.update(uuid, new LcRequest(pegawaiUuid, marker,
                    LocalDate.of(2026, 1, 2), 4)).uuid();
        }

        @Override
        public CursorPageResponse<?> search(String search, UUID pegawaiUuid, UUID cursor) {
            return lcService.search(search, pegawaiUuid, cursor);
        }

        @Override
        public UUID pegawaiUuidOf(Object response) {
            return ((com.example.demo.dto.LcResponse) response).pegawaiUuid();
        }

        @Override
        public void delete(UUID uuid) {
            lcService.delete(uuid);
        }
    }

    private final class BelajarMandiriResource implements Resource {

        @Override
        public String name() {
            return "belajar-mandiri";
        }

        @Override
        public UUID create(String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return belajarMandiriService.create(new BelajarMandiriRequest(pegawaiUuid, marker,
                    LocalDate.of(2026, 1, 1), 8)).uuid();
        }

        @Override
        public Object get(UUID uuid) {
            return belajarMandiriService.getByUuid(uuid);
        }

        @Override
        public UUID update(UUID uuid, String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return belajarMandiriService.update(uuid, new BelajarMandiriRequest(pegawaiUuid, marker,
                    LocalDate.of(2026, 1, 2), 4)).uuid();
        }

        @Override
        public CursorPageResponse<?> search(String search, UUID pegawaiUuid, UUID cursor) {
            return belajarMandiriService.search(search, pegawaiUuid, cursor);
        }

        @Override
        public UUID pegawaiUuidOf(Object response) {
            return ((com.example.demo.dto.BelajarMandiriResponse) response).pegawaiUuid();
        }

        @Override
        public void delete(UUID uuid) {
            belajarMandiriService.delete(uuid);
        }
    }

    private final class MentoringResource implements Resource {

        @Override
        public String name() {
            return "mentoring";
        }

        @Override
        public UUID create(String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return mentoringService.create(new MentoringRequest(pegawaiUuid, marker,
                    LocalDate.of(2026, 1, 1), 8)).uuid();
        }

        @Override
        public Object get(UUID uuid) {
            return mentoringService.getByUuid(uuid);
        }

        @Override
        public UUID update(UUID uuid, String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return mentoringService.update(uuid, new MentoringRequest(pegawaiUuid, marker,
                    LocalDate.of(2026, 1, 2), 4)).uuid();
        }

        @Override
        public CursorPageResponse<?> search(String search, UUID pegawaiUuid, UUID cursor) {
            return mentoringService.search(search, pegawaiUuid, cursor);
        }

        @Override
        public UUID pegawaiUuidOf(Object response) {
            return ((com.example.demo.dto.MentoringResponse) response).pegawaiUuid();
        }

        @Override
        public void delete(UUID uuid) {
            mentoringService.delete(uuid);
        }
    }

    private final class CoachingResource implements Resource {

        @Override
        public String name() {
            return "coaching";
        }

        @Override
        public UUID create(String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return coachingService.create(new CoachingRequest(pegawaiUuid, marker,
                    LocalDate.of(2026, 1, 1), 8)).uuid();
        }

        @Override
        public Object get(UUID uuid) {
            return coachingService.getByUuid(uuid);
        }

        @Override
        public UUID update(UUID uuid, String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return coachingService.update(uuid, new CoachingRequest(pegawaiUuid, marker,
                    LocalDate.of(2026, 1, 2), 4)).uuid();
        }

        @Override
        public CursorPageResponse<?> search(String search, UUID pegawaiUuid, UUID cursor) {
            return coachingService.search(search, pegawaiUuid, cursor);
        }

        @Override
        public UUID pegawaiUuidOf(Object response) {
            return ((com.example.demo.dto.CoachingResponse) response).pegawaiUuid();
        }

        @Override
        public void delete(UUID uuid) {
            coachingService.delete(uuid);
        }
    }

    private final class WorkshopResource implements Resource {

        @Override
        public String name() {
            return "workshop";
        }

        @Override
        public UUID create(String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return workshopService.create(new WorkshopRequest(pegawaiUuid, marker,
                    LocalDate.of(2026, 1, 1), 8)).uuid();
        }

        @Override
        public Object get(UUID uuid) {
            return workshopService.getByUuid(uuid);
        }

        @Override
        public UUID update(UUID uuid, String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return workshopService.update(uuid, new WorkshopRequest(pegawaiUuid, marker,
                    LocalDate.of(2026, 1, 2), 4)).uuid();
        }

        @Override
        public CursorPageResponse<?> search(String search, UUID pegawaiUuid, UUID cursor) {
            return workshopService.search(search, pegawaiUuid, cursor);
        }

        @Override
        public UUID pegawaiUuidOf(Object response) {
            return ((com.example.demo.dto.WorkshopResponse) response).pegawaiUuid();
        }

        @Override
        public void delete(UUID uuid) {
            workshopService.delete(uuid);
        }
    }

    private final class JenisPelatihanResource implements Resource {

        @Override
        public String name() {
            return "jenis-pelatihan";
        }

        @Override
        public boolean hasPegawai() {
            return false;
        }

        @Override
        public UUID create(String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return jenisPelatihanService.create(new JenisPelatihanRequest(marker)).uuid();
        }

        @Override
        public Object get(UUID uuid) {
            return jenisPelatihanService.getByUuid(uuid);
        }

        @Override
        public UUID update(UUID uuid, String marker, UUID pegawaiUuid, UUID jenisUuid) {
            return jenisPelatihanService.update(uuid, new JenisPelatihanRequest(marker)).uuid();
        }

        @Override
        public CursorPageResponse<?> search(String search, UUID pegawaiUuid, UUID cursor) {
            return jenisPelatihanService.search(search, cursor);
        }

        @Override
        public UUID pegawaiUuidOf(Object response) {
            JenisPelatihanResponse ignored = (JenisPelatihanResponse) response;
            return null;
        }

        @Override
        public void delete(UUID uuid) {
            jenisPelatihanService.delete(uuid);
        }
    }
}
