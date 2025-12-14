
    create sequence emdb_audit.job_sequence start with 1 increment by 50;

    create table emdb_audit.Job_History (
        id bigint not null,
        latency bigint,
        message varchar(255) not null,
        percent_complete integer not null check ((percent_complete<=100) and (percent_complete>=0)),
        source varchar(255) not null check ((source in ('GATEWAY','MEDIA','SCHEDULER','SCRAPER','USER'))),
        timestamp timestamp(6) with time zone not null,
        tmdb_id integer not null,
        trace_id varchar(255) not null,
        type varchar(255) not null check ((type in ('SUBMITTED','STARTED','PROGRESS','COMPLETED','FAILED'))),
        primary key (id)
    );
