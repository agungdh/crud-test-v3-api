package com.example.demo.seeder;

import com.example.demo.dto.Gender;
import com.example.demo.dto.PegawaiRequest;
import com.example.demo.entity.Pegawai;
import com.example.demo.mapper.PegawaiMapper;
import com.example.demo.repository.PegawaiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Development-only seeder that inserts a fixed number of randomly generated pegawai.
 *
 * <p>Activated by the {@code seeder} Spring profile, so it never runs during normal
 * startup or tests. Run it with:
 *
 * <pre>{@code ./gradlew bootRun --args='--spring.profiles.active=seeder'}</pre>
 *
 * <p>Rows are written in batches inside their own transaction to keep the insert cheap.
 * Existing rows are left untouched; the run is skipped when its own generated NIPs are
 * already present, so running it twice will not duplicate data.
 */
@Slf4j
@Component
@Profile("seeder")
@RequiredArgsConstructor
public class PegawaiSeeder implements ApplicationRunner {

    private static final int BATCH_SIZE = 100;

    private static final String[] MALE_FIRST_NAMES = {
            "Budi", "Agus", "Slamet", "Joko", "Andi", "Rizky", "Dedi", "Hendra", "Fajar", "Eko",
            "Wahyu", "Yusuf", "Iman", "Bayu", "Arif", "Gilang", "Rahmat", "Taufik", "Surya", "Adi",
            "Bagus", "Dimas", "Fikri", "Reza", "Umar", "Hasan", "Ilham", "Krisna", "Lukman", "Rudi"
    };

    private static final String[] FEMALE_FIRST_NAMES = {
            "Siti", "Dewi", "Rina", "Ayu", "Ratna", "Sri", "Indah", "Putri", "Maya", "Nurul",
            "Fitri", "Wulan", "Ani", "Yuni", "Lestari", "Cahaya", "Bella", "Tika", "Sari", "Mega",
            "Lia", "Nadia", "Rahma", "Vina", "Ika", "Dian", "Novi", "Sinta", "Ayu Lestari", "Meisya"
    };

    private static final String[] LAST_NAMES = {
            "Santoso", "Wijaya", "Pratama", "Kusuma", "Nugroho", "Setiawan", "Halim", "Saputra",
            "Hidayat", "Maulana", "Ramadhan", "Firmansyah", "Gunawan", "Susanto", "Hartono",
            "Permana", "Siregar", "Simanjuntak", "Nainggolan", "Hutapea", "Wibowo", "Utama",
            "Prasetyo", "Kurniawan", "Iskandar", "Rahman", "Syahputra", "Simbolon", "Purnama", "Suryana"
    };

    private static final String[] STREETS = {
            "Jl. Merdeka", "Jl. Sudirman", "Jl. Thamrin", "Jl. Gatot Subroto", "Jl. Diponegoro",
            "Jl. Ahmad Yani", "Jl. Kartini", "Jl. Pahlawan", "Jl. Cempaka", "Jl. Melati",
            "Jl. Mawar", "Jl. Kenanga", "Jl. Anggrek", "Jl. Mangga", "Jl. Rambutan",
            "Jl. Durian", "Jl. Pisang", "Jl. Salak", "Jl. Semangka", "Jl. Jambu"
    };

    private static final String[] CITIES = {
            "Jakarta", "Bandung", "Surabaya", "Medan", "Semarang", "Yogyakarta", "Makassar",
            "Denpasar", "Palembang", "Malang", "Bogor", "Depok", "Bekasi", "Tangerang",
            "Sleman", "Badung", "Batam", "Pekanbaru", "Balikpapan", "Samarinda"
    };

    private final PegawaiRepository repository;
    private final PegawaiMapper mapper;
    private final TransactionTemplate transactionTemplate;

    @Value("${app.seeder.total:1000}")
    private int total;

    @Override
    public void run(ApplicationArguments args) {
        // Fixed seed keeps generated data reproducible, which also makes the run idempotent:
        // the first generated NIP acts as a marker for "this seeder already ran".
        Random random = new Random(20_261_020L);

        PegawaiRequest first = randomRequest(1, random);
        if (repository.existsByNip(first.nip())) {
            log.warn("Seeder skipped: generated NIP {} already exists", first.nip());
            return;
        }

        int inserted = 0;
        List<Pegawai> batch = new ArrayList<>(BATCH_SIZE);
        batch.add(mapper.toEntity(first));

        for (int sequence = 2; sequence <= total; sequence++) {
            batch.add(mapper.toEntity(randomRequest(sequence, random)));
            if (batch.size() == BATCH_SIZE) {
                inserted += flush(batch);
            }
        }

        if (!batch.isEmpty()) {
            inserted += flush(batch);
        }

        log.info("Seeder inserted {} pegawai", inserted);
    }

    /**
     * Writes the pending batch in its own transaction and clears it.
     *
     * @return the number of rows written
     */
    private int flush(List<Pegawai> batch) {
        List<Pegawai> pending = List.copyOf(batch);
        transactionTemplate.executeWithoutResult(status -> repository.saveAll(pending));
        batch.clear();
        return pending.size();
    }

    private PegawaiRequest randomRequest(int sequence, Random random) {
        boolean male = random.nextBoolean();
        String firstName = pick(male ? MALE_FIRST_NAMES : FEMALE_FIRST_NAMES, random);
        String lastName = pick(LAST_NAMES, random);
        String nama = firstName + " " + lastName;

        LocalDate tanggalLahir = LocalDate.of(
                random.nextInt(1960, 2001),
                random.nextInt(1, 13),
                random.nextInt(1, 29)
        );

        // NIP mirrors the real format: yyyyMMdd of birth date plus a zero-padded sequence.
        String nip = tanggalLahir.toString().replace("-", "") + String.format("%06d", sequence);

        String email = nama.toLowerCase()
                .replace(" ", ".")
                .replaceAll("[^a-z.]", "") + sequence + "@example.com";

        String noTelepon = "08" + String.format("%010d", random.nextLong(10_000_000_000L));

        String alamat = "%s No. %d, %s, %s".formatted(
                pick(STREETS, random),
                random.nextInt(1, 200),
                pick(CITIES, random),
                "Indonesia"
        );

        return new PegawaiRequest(
                nip,
                nama,
                tanggalLahir,
                male ? Gender.MALE : Gender.FEMALE,
                email,
                noTelepon,
                alamat
        );
    }

    private String pick(String[] values, Random random) {
        return values[random.nextInt(values.length)];
    }
}
