
    create sequence emdb_media.movie_sequence start with 1 increment by 1;

    create table emdb_media.Movies (
        id bigint not null,
        created timestamp(6) with time zone not null,
        modified timestamp(6) with time zone not null,
        uid uuid not null unique,
        backdrop uuid unique,
        homepage varchar(2048),
        originalLanguage varchar(2),
        overview varchar(1024),
        poster uuid unique,
        score float4 check ((score<=10) and (score>=0)),
        status varchar(16) check ((status in ('CANCELED','ENDED','IN_PRODUCTION','PILOT','PLANNED','POST_PRODUCTION','RELEASED','RETURNING_SERIES','RUMORED'))),
        tagline varchar(150),
        title varchar(140) not null,
        tmdb_backdrop varchar(80),
        tmdb_id integer not null unique,
        tmdb_poster varchar(80),
        budget integer,
        release_date date,
        revenue integer,
        runtime integer,
        primary key (id)
    );
