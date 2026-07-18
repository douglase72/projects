

    create table emdb_media.movie (
        id uuid      not null,
        public_id    bigint generated always as identity,
        source       varchar(16) not null,
        source_id    varchar(64) not null,
        release_date date,
        title        varchar(140) not null,
        constraint uq_movie_public_id unique (public_id),
        constraint uq_movie_source unique (source, source_id),
        constraint ck_movie_source check (source IN ('imdb','omdb','tmdb','trakt')),
        primary key (id)
    );    