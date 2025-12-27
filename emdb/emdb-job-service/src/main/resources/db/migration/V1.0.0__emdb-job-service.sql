
    create sequence emdb_job.job_sequence start with 1 increment by 50;

    create table emdb_job.Job_History (
        id bigint not null,
        content varchar(255) not null,
        job_id uuid not null,
        progress integer not null check ((progress<=100) and (progress>=0)),
        source varchar(255) not null check ((source in ('GATEWAY','MEDIA','SCHEDULER','SCRAPER','USER'))),
        status varchar(255) not null check ((status in ('SUBMITTED','STARTED','PROGRESS','COMPLETED','FAILED'))),
        timestamp timestamp(6) with time zone not null,
        tmdb_id integer not null,
        primary key (id)
    );
