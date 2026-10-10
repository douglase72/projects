
    create table media.movie_outbox (
        id uuid not null,
        correlation_id uuid not null,
        media_id uuid not null,
        tmdb_id integer not null,
        primary key (id)
    );
