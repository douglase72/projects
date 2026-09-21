
    create table ingest.ingest_job (
        id uuid not null,
        ingest_type varchar(16) not null check ((ingest_type in ('MOVIE','PERSON','SERIES'))),
        stage varchar(16) not null check ((stage in ('SUBMITTED','STARTED','EXTRACTED','COMPLETED','FAILED'))),
        submitted_at timestamp(6) with time zone not null,
        tmdb_id integer not null,
        version bigint not null,
        primary key (id)
    );
