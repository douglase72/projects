
    create sequence emdb_media.movie_sequence start with 1 increment by 1;

    create sequence emdb_media.person_sequence start with 1 increment by 50;

    create table emdb_media.Movies (
        id bigint not null,
        createdAt timestamp(6) with time zone not null,
        modifiedAt timestamp(6) with time zone not null,
        tmdb_id integer not null unique,
        backdrop uuid unique,
        homepage varchar(2048),
        original_language varchar(2) not null,
        overview varchar(1024),
        poster uuid unique,
        score float4 not null check ((score<=10) and (score>=0)),
        status varchar(16) not null check ((status in ('CANCELED','ENDED','IN_PRODUCTION','PILOT','PLANNED','POST_PRODUCTION','RELEASED','RETURNING_SERIES','RUMORED'))),
        tagline varchar(150),
        title varchar(140) not null,
        tmdb_backdrop varchar(80) unique,
        tmdb_poster varchar(80) unique,
        budget bigint,
        release_date date,
        revenue bigint,
        runtime integer,
        primary key (id),
        constraint uk_movies_title_release_date unique (title, release_date)
    );

    create table emdb_media.People (
        id bigint not null,
        createdAt timestamp(6) with time zone not null,
        modifiedAt timestamp(6) with time zone not null,
        tmdb_id integer not null unique,
        biography varchar(3900),
        birth_date date,
        birth_place varchar(120),
        death_date date,
        gender varchar(10) not null check ((gender in ('UNKNOWN','FEMALE','MALE','NON_BINARY'))),
        homepage varchar(2048),
        name varchar(80) not null,
        profile uuid unique,
        tmdb_profile varchar(80) unique,
        primary key (id)
    );
