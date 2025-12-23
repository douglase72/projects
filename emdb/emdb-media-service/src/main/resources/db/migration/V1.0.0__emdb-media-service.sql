
    create sequence emdb_media.credit_sequence start with 1 increment by 50;

    create sequence emdb_media.movie_sequence start with 1 increment by 1;

    create sequence emdb_media.person_sequence start with 1 increment by 50;

    create table emdb_media.Credits (
        id bigint not null,
        created timestamp(6) with time zone not null,
        modified timestamp(6) with time zone not null,
        tmdb_id varchar(255) not null unique,
        credit_order integer,
        credit_type varchar(4) not null check ((credit_type in ('CAST','CREW'))),
        person_id bigint not null,
        primary key (id)
    );

    create table emdb_media.Movie_Credits (
        role varchar(100),
        id bigint not null,
        movie_id bigint not null,
        primary key (id)
    );

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
        primary key (id)
    );

    alter table if exists emdb_media.Credits 
       add constraint FK78ndg1w4qadcmsrfywco5nyi4 
       foreign key (person_id) 
       references emdb_media.People;

    alter table if exists emdb_media.Movie_Credits 
       add constraint FKghpldaaqxi0bbp03vtofeu5g9 
       foreign key (movie_id) 
       references emdb_media.Movies;

    alter table if exists emdb_media.Movie_Credits 
       add constraint FKcdn2u462q83c9aflpli5wjt7 
       foreign key (id) 
       references emdb_media.Credits;
