package com.example.demo.seeder;

import com.example.demo.entity.JenisPelatihan;
import com.example.demo.repository.JenisPelatihanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds the {@code jenis_pelatihan} lookup rows used by diklat.
 *
 * <p>Activated by the {@code seeder} profile and idempotent: rows that already exist
 * (matched by name) are left untouched.
 */
@Slf4j
@Component
@Profile("seeder")
@RequiredArgsConstructor
public class JenisPelatihanSeeder implements ApplicationRunner {

    private static final List<String> NAMES = List.of(
            "Penjenjangan",
            "MOOC",
            "Sertifikasi",
            "Teknis Substansif",
            "Pelatihan Kepemimpinan"
    );

    private final JenisPelatihanRepository repository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        int inserted = 0;
        for (String nama : NAMES) {
            if (repository.existsByNama(nama)) {
                continue;
            }

            JenisPelatihan entity = new JenisPelatihan();
            entity.setNama(nama);
            repository.save(entity);
            inserted++;
        }

        log.info("Jenis pelatihan seed finished: {} inserted, {} already present", inserted, NAMES.size() - inserted);
    }
}
