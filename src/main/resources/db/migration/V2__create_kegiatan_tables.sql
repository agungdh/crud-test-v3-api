CREATE TABLE jenis_pelatihan
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid       UUID         NOT NULL DEFAULT gen_random_uuid(),
    nama       VARCHAR(150) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by BIGINT,
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by BIGINT,
    deleted_at TIMESTAMPTZ,
    deleted_by BIGINT
);

CREATE INDEX idx_jenis_pelatihan_uuid_hash ON jenis_pelatihan USING hash (uuid);
CREATE INDEX idx_jenis_pelatihan_deleted_at ON jenis_pelatihan (deleted_at);

CREATE TABLE diklat
(
    id                         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid                       UUID         NOT NULL DEFAULT gen_random_uuid(),
    pegawai_id                 BIGINT       NOT NULL,
    jenis_pelatihan_id         BIGINT       NOT NULL,
    nomor_surat                VARCHAR(150) NOT NULL,
    materi_pengembangan        VARCHAR(255) NOT NULL,
    dari_tanggal_pelaksanaan   DATE         NOT NULL,
    sampai_tanggal_pelaksanaan DATE         NOT NULL,
    jumlah_jam_pelatihan       INTEGER      NOT NULL,
    created_at                 TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by                 BIGINT,
    updated_at                 TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by                 BIGINT,
    deleted_at                 TIMESTAMPTZ,
    deleted_by                 BIGINT,
    CONSTRAINT fk_diklat_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai (id),
    CONSTRAINT fk_diklat_jenis_pelatihan FOREIGN KEY (jenis_pelatihan_id) REFERENCES jenis_pelatihan (id)
);

CREATE INDEX idx_diklat_uuid_hash ON diklat USING hash (uuid);
CREATE INDEX idx_diklat_deleted_at ON diklat (deleted_at);
CREATE INDEX idx_diklat_pegawai_id ON diklat (pegawai_id);
CREATE INDEX idx_diklat_nomor_surat ON diklat (nomor_surat);
CREATE INDEX idx_diklat_dari_tanggal_pelaksanaan ON diklat (dari_tanggal_pelaksanaan);
CREATE INDEX idx_diklat_sampai_tanggal_pelaksanaan ON diklat (sampai_tanggal_pelaksanaan);

CREATE TABLE ppm
(
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid                 UUID         NOT NULL DEFAULT gen_random_uuid(),
    pegawai_id           BIGINT       NOT NULL,
    nomor_surat          VARCHAR(150) NOT NULL,
    materi_pengembangan  VARCHAR(255) NOT NULL,
    tanggal_pelaksanaan  DATE         NOT NULL,
    jumlah_jam_pelatihan INTEGER      NOT NULL,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by           BIGINT,
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by           BIGINT,
    deleted_at           TIMESTAMPTZ,
    deleted_by           BIGINT,
    CONSTRAINT fk_ppm_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai (id)
);

CREATE INDEX idx_ppm_uuid_hash ON ppm USING hash (uuid);
CREATE INDEX idx_ppm_deleted_at ON ppm (deleted_at);
CREATE INDEX idx_ppm_pegawai_id ON ppm (pegawai_id);
CREATE INDEX idx_ppm_nomor_surat ON ppm (nomor_surat);
CREATE INDEX idx_ppm_tanggal_pelaksanaan ON ppm (tanggal_pelaksanaan);

CREATE TABLE seminar
(
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid                UUID         NOT NULL DEFAULT gen_random_uuid(),
    pegawai_id          BIGINT       NOT NULL,
    materi_pengembangan VARCHAR(255) NOT NULL,
    tanggal_pelaksanaan DATE         NOT NULL,
    jumlah_jam          INTEGER      NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by          BIGINT,
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,
    deleted_by          BIGINT,
    CONSTRAINT fk_seminar_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai (id)
);

CREATE INDEX idx_seminar_uuid_hash ON seminar USING hash (uuid);
CREATE INDEX idx_seminar_deleted_at ON seminar (deleted_at);
CREATE INDEX idx_seminar_pegawai_id ON seminar (pegawai_id);
CREATE INDEX idx_seminar_tanggal_pelaksanaan ON seminar (tanggal_pelaksanaan);

CREATE TABLE webinar
(
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid                UUID         NOT NULL DEFAULT gen_random_uuid(),
    pegawai_id          BIGINT       NOT NULL,
    materi_pengembangan VARCHAR(255) NOT NULL,
    tanggal_pelaksanaan DATE         NOT NULL,
    jumlah_jam          INTEGER      NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by          BIGINT,
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,
    deleted_by          BIGINT,
    CONSTRAINT fk_webinar_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai (id)
);

CREATE INDEX idx_webinar_uuid_hash ON webinar USING hash (uuid);
CREATE INDEX idx_webinar_deleted_at ON webinar (deleted_at);
CREATE INDEX idx_webinar_pegawai_id ON webinar (pegawai_id);
CREATE INDEX idx_webinar_tanggal_pelaksanaan ON webinar (tanggal_pelaksanaan);

CREATE TABLE lc
(
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid                UUID         NOT NULL DEFAULT gen_random_uuid(),
    pegawai_id          BIGINT       NOT NULL,
    materi_pengembangan VARCHAR(255) NOT NULL,
    tanggal_pelaksanaan DATE         NOT NULL,
    jumlah_jam          INTEGER      NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by          BIGINT,
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,
    deleted_by          BIGINT,
    CONSTRAINT fk_lc_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai (id)
);

CREATE INDEX idx_lc_uuid_hash ON lc USING hash (uuid);
CREATE INDEX idx_lc_deleted_at ON lc (deleted_at);
CREATE INDEX idx_lc_pegawai_id ON lc (pegawai_id);
CREATE INDEX idx_lc_tanggal_pelaksanaan ON lc (tanggal_pelaksanaan);

CREATE TABLE belajar_mandiri
(
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid                UUID         NOT NULL DEFAULT gen_random_uuid(),
    pegawai_id          BIGINT       NOT NULL,
    materi_pengembangan VARCHAR(255) NOT NULL,
    tanggal_pelaksanaan DATE         NOT NULL,
    jumlah_jam          INTEGER      NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by          BIGINT,
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,
    deleted_by          BIGINT,
    CONSTRAINT fk_belajar_mandiri_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai (id)
);

CREATE INDEX idx_belajar_mandiri_uuid_hash ON belajar_mandiri USING hash (uuid);
CREATE INDEX idx_belajar_mandiri_deleted_at ON belajar_mandiri (deleted_at);
CREATE INDEX idx_belajar_mandiri_pegawai_id ON belajar_mandiri (pegawai_id);
CREATE INDEX idx_belajar_mandiri_tanggal_pelaksanaan ON belajar_mandiri (tanggal_pelaksanaan);

CREATE TABLE mentoring
(
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid                UUID         NOT NULL DEFAULT gen_random_uuid(),
    pegawai_id          BIGINT       NOT NULL,
    materi_pengembangan VARCHAR(255) NOT NULL,
    tanggal_pelaksanaan DATE         NOT NULL,
    jumlah_jam          INTEGER      NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by          BIGINT,
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,
    deleted_by          BIGINT,
    CONSTRAINT fk_mentoring_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai (id)
);

CREATE INDEX idx_mentoring_uuid_hash ON mentoring USING hash (uuid);
CREATE INDEX idx_mentoring_deleted_at ON mentoring (deleted_at);
CREATE INDEX idx_mentoring_pegawai_id ON mentoring (pegawai_id);
CREATE INDEX idx_mentoring_tanggal_pelaksanaan ON mentoring (tanggal_pelaksanaan);

CREATE TABLE coaching
(
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid                UUID         NOT NULL DEFAULT gen_random_uuid(),
    pegawai_id          BIGINT       NOT NULL,
    materi_pengembangan VARCHAR(255) NOT NULL,
    tanggal_pelaksanaan DATE         NOT NULL,
    jumlah_jam          INTEGER      NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by          BIGINT,
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,
    deleted_by          BIGINT,
    CONSTRAINT fk_coaching_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai (id)
);

CREATE INDEX idx_coaching_uuid_hash ON coaching USING hash (uuid);
CREATE INDEX idx_coaching_deleted_at ON coaching (deleted_at);
CREATE INDEX idx_coaching_pegawai_id ON coaching (pegawai_id);
CREATE INDEX idx_coaching_tanggal_pelaksanaan ON coaching (tanggal_pelaksanaan);

CREATE TABLE workshop
(
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid                UUID         NOT NULL DEFAULT gen_random_uuid(),
    pegawai_id          BIGINT       NOT NULL,
    materi_pengembangan VARCHAR(255) NOT NULL,
    tanggal_pelaksanaan DATE         NOT NULL,
    jumlah_jam          INTEGER      NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by          BIGINT,
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,
    deleted_by          BIGINT,
    CONSTRAINT fk_workshop_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai (id)
);

CREATE INDEX idx_workshop_uuid_hash ON workshop USING hash (uuid);
CREATE INDEX idx_workshop_deleted_at ON workshop (deleted_at);
CREATE INDEX idx_workshop_pegawai_id ON workshop (pegawai_id);
CREATE INDEX idx_workshop_tanggal_pelaksanaan ON workshop (tanggal_pelaksanaan);
