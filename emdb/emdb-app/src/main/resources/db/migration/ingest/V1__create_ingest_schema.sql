create table emdb_ingest.ingest (id uuid not null, ingest_type varchar(16) not null check ((ingest_type in ('MOVIE','PERSON','SERIES'))), message varchar(1000), source varchar(16) not null, source_id varchar(64) not null, status varchar(16) not null check ((status in ('SUBMITTED','STARTED','EXTRACTED','LOADED','COMPLETED','FAILED'))), submitted_at timestamp(6) with time zone not null, primary key (id));
alter table if exists emdb_ingest.ingest drop constraint if exists uq_ingest_source;
alter table if exists emdb_ingest.ingest add constraint uq_ingest_source unique (source, source_id);
