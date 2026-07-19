
    create sequence emdb_media.movie_sequence start with 1 increment by 1;

    create table emdb_media.movie (
        public_id bigint not null,
        id uuid not null,
        original_language varchar(2) not null,
        release_date date,
        source varchar(16) not null,
        source_id varchar(64) not null,
        title varchar(140) not null,
        primary key (public_id),
        constraint uq_movie_source unique (source, source_id),
        constraint uq_movie_uuid unique (id)
    );
