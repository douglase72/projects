create table emdb_ingest.ingest (id uuid not null, ingest_type varchar(16) not null check ((ingest_type in ('MOVIE','PERSON','SERIES'))), message varchar(1000), status varchar(16) not null check ((status in ('SUBMITTED','STARTED','EXTRACTED','LOADED','COMPLETED','FAILED'))), submitted_at timestamp(6) with time zone not null, tmdb_id integer not null, primary key (id));
alter table if exists emdb_ingest.ingest drop constraint if exists uq_ingest_tmdb;
alter table if exists emdb_ingest.ingest add constraint uq_ingest_tmdb unique (tmdb_id, ingest_type);
