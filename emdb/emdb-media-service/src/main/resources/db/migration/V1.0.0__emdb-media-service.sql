
    create sequence emdb_media.movie_sequence start with 1 increment by 1;

    create sequence emdb_media.person_sequence start with 1 increment by 50;

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
        score float4 check ((score<=10) and (score>=0)),
        status varchar(16) not null check ((status in ('CANCELED','ENDED','IN_PRODUCTION','PILOT','PLANNED','POST_PRODUCTION','RELEASED','RETURNING_SERIES','RUMORED'))),
        tagline varchar(150),
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
        tmdb_id integer not null unique,
        biography varchar(3900),
        birth_date date,
        birth_place varchar(80),
        death_date date,
        gender varchar(10) not null check ((gender in ('UNKNOWN','FEMALE','MALE','NON_BINARY'))),
        name varchar(80) not null,
        profile varchar(37),
        deleted boolean not null,
        primary key (id)
    );

    comment on column emdb_media.People.deleted is
        'Soft-delete indicator';
