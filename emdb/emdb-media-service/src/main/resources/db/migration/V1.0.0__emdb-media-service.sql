
    create sequence emdb_media.movie_sequence start with 1 increment by 1;

    create sequence emdb_media.person_sequence start with 1 increment by 50;

    create table emdb_media.Movies (
        id bigint not null,
        created timestamp(6) with time zone not null,
        modified timestamp(6) with time zone not null,
        uid uuid not null unique,
        backdrop uuid unique,
        homepage varchar(2048),
        original_language varchar(2),
        overview varchar(1024),
        poster uuid unique,
        score float4 check ((score>=0) and (score<=10)),
        status varchar(16) check ((status in ('CANCELED','ENDED','IN_PRODUCTION','PILOT','PLANNED','POST_PRODUCTION','RELEASED','RETURNING_SERIES','RUMORED'))),
        tagline varchar(150),
        title varchar(140) not null,
        tmdb_id integer not null unique,
        budget integer,
        release_date date,
        revenue integer,
        runtime integer,
        primary key (id)
    );

    create table emdb_media.People (
        id bigint not null,
        created timestamp(6) with time zone not null,
        modified timestamp(6) with time zone not null,
        uid uuid not null unique,
        biography varchar(3900),
        birth_date date,
        birth_place varchar(120),
        death_date date,
        gender varchar(10) check ((gender in ('UNKNOWN','FEMALE','MALE','NON_BINARY'))),
        name varchar(80) not null,
        profile uuid unique,
        tmdb_id integer not null unique,
        primary key (id)
    );
