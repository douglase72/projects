
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

    create table media.movie_credit (
        id uuid not null,
        credit_type varchar(8) not null check ((credit_type in ('CAST','CREW'))),
        department varchar(255),
        name varchar(255) not null,
        credit_order integer,
        person_id uuid not null,
        role varchar(250),
        tmdb_id varchar(255) not null,
        movie_id uuid not null,
        primary key (id),
        unique (movie_id, tmdb_id)
    );

    create table media.person (
        id uuid not null,
        biography varchar(4000),
        birth_date date,
        death_date date,
        gender varchar(10) check ((gender in ('FEMALE','MALE','NON_BINARY'))),
        name varchar(80) not null,
        tmdb_id integer not null,
        version bigint not null,
        primary key (id),
        constraint uq_person_tmdb_id unique (tmdb_id)
    );

    create table media.person_outbox (
        id uuid not null,
        created_at timestamp(6) with time zone not null,
        name varchar(80) not null,
        tmdb_id integer not null,
        primary key (id)
    );

    alter table if exists media.movie_credit 
       add constraint FKnhguhhky9w4rsrxfcttovvey9 
       foreign key (movie_id) 
       references media.movie;
