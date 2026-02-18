
    create table emdb_job.Ingest_Jobs (
        id uuid not null,
        current_source varchar(255) not null check ((current_source in ('GATEWAY','MEDIA','SCHEDULER','SCRAPER'))),
        current_status varchar(255) not null check ((current_status in ('SUBMITTED','STARTED','EXTRACTED','COMPLETED','FAILED','HEARTBEAT'))),
        emdb_id bigint,
        history jsonb,
        message varchar(255),
        modified timestamp(6) with time zone not null,
        name varchar(140),
        tmdb_id integer not null,
        type varchar(255) not null check ((type in ('MOVIE','PERSON','SERIES'))),
        primary key (id)
    );
