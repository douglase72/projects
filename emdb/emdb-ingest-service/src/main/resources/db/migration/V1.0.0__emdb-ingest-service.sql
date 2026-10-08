
    create table ingest.ingest_event (
        id uuid not null,
        ingest_id uuid not null,
        occurred_at timestamp(6) with time zone not null,
        published boolean not null,
        ingest_status varchar(16) not null check ((ingest_status in ('SUBMITTED','STARTED','EXTRACTED','COMPLETED','FAILED'))),
        primary key (id)
    );

    create table ingest.ingest_job (
        id uuid not null,
        media_type varchar(16) not null check ((media_type in ('MOVIE','PERSON','SERIES'))),
        status varchar(16) not null check ((status in ('SUBMITTED','STARTED','EXTRACTED','COMPLETED','FAILED'))),
        submitted_at timestamp(6) with time zone not null,
        tmdb_id integer not null,
        version bigint not null,
        primary key (id)
    );
