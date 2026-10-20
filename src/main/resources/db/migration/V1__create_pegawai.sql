CREATE TABLE pegawai
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid          UUID        NOT NULL DEFAULT gen_random_uuid(),
    nip           VARCHAR(30) NOT NULL,
    nama          VARCHAR(150) NOT NULL,
    tanggal_lahir DATE        NOT NULL,
    is_male       BOOLEAN     NOT NULL,
    email         VARCHAR(150),
    no_telepon    VARCHAR(30),
    alamat        TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by    BIGINT,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by    BIGINT,
    deleted_at    TIMESTAMPTZ,
    deleted_by    BIGINT
);

CREATE UNIQUE INDEX uq_pegawai_nip_active ON pegawai (nip) WHERE deleted_at IS NULL;
CREATE INDEX idx_pegawai_nama ON pegawai (nama);
CREATE INDEX idx_pegawai_uuid_hash ON pegawai USING hash (uuid);
