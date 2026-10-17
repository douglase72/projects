
    create table media.movie (
        id uuid not null,
        original_language varchar(2),
        overview varchar(1000),
        release_date date,
        score numeric(5,3),
        title varchar(140) not null,
        tmdb_id integer not null,
        version bigint not null,
        primary key (id),
        constraint uq_movie_tmdb_id unique (tmdb_id)
    );

    create table media.movie_outbox (
        id uuid not null,
        correlation_id uuid not null,
        media_id uuid not null,
        tmdb_id integer not null,
        primary key (id)
    );
