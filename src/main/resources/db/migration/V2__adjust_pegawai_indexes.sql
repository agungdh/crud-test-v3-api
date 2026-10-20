DROP INDEX idx_pegawai_nama;

CREATE INDEX idx_pegawai_deleted_at ON pegawai (deleted_at);
