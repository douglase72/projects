
    create table emdb_job.Ingest_Jobs (
        id uuid not null,
        emdb_id bigint,
        name varchar(140),
        source varchar(255) not null check ((source in ('GATEWAY','MEDIA','SCHEDULER','SCRAPER','USER'))),
        status varchar(255) not null check ((status in ('SUBMITTED','STARTED','EXTRACTED','COMPLETED','FAILED','HEARTBEAT'))),
        timestamp timestamp(6) with time zone not null,
        tmdb_id integer not null,
        type varchar(255) not null check ((type in ('MOVIE','PERSON','SERIES'))),
        primary key (id)
    );
