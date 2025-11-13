
    create sequence emdb_media.movie_sequence start with 1 increment by 1;

    create table emdb_media.Movies (
        id bigint not null,
        created timestamp(6) with time zone not null,
        modified timestamp(6) with time zone not null,
        tmdb_id integer not null unique,
        backdrop varchar(37),
        homepage varchar(2048),
        name varchar(140) not null,
        originalLanguage varchar(2),
        overview varchar(1024),
        poster varchar(37),
        score float4 check ((score>=0) and (score<=10)),
        status varchar(16) not null check ((status in ('CANCELED','ENDED','IN_PRODUCTION','PILOT','PLANNED','POST_PRODUCTION','RELEASED','RETURNING_SERIES','RUMORED'))),
        tagline varchar(150),
        budget integer,
        release_date date,
        revenue integer,
        runtime integer,
        primary key (id)
    );
