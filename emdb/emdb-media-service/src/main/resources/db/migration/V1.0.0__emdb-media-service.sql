
    create sequence emdb_media.movie_sequence start with 1 increment by 1;

    create sequence emdb_media.person_sequence start with 1 increment by 50;

    create sequence emdb_media.series_sequence start with 1 increment by 1;

    create table emdb_media.Credits (
        DTYPE varchar(31) not null check ((DTYPE in ('Credit','SeriesCredit','MovieCredit'))),
        id uuid not null,
        created timestamp(6) with time zone not null,
        modified timestamp(6) with time zone not null,
        credit_order integer,
        credit_type varchar(4) not null check ((credit_type in ('CAST','CREW'))),
        total_episodes integer,
        role varchar(100),
        person_id bigint not null,
        series_id bigint,
        movie_id bigint,
        primary key (id)
    );

    create table emdb_media.Movies (
        id bigint not null,
        created timestamp(6) with time zone not null,
        modified timestamp(6) with time zone not null,
        tmdb_id integer not null unique,
        backdrop uuid unique,
        homepage varchar(2048),
        originalLanguage varchar(2),
        overview varchar(1024),
        poster uuid unique,
        score float4 check ((score>=0) and (score<=10)),
        status varchar(16) check ((status in ('CANCELED','ENDED','IN_PRODUCTION','PILOT','PLANNED','POST_PRODUCTION','RELEASED','RETURNING_SERIES','RUMORED'))),
        tagline varchar(150),
        title varchar(140) not null,
        tmdb_backdrop varchar(80),
        tmdb_poster varchar(80),
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
        birth_place varchar(120),
        death_date date,
        gender varchar(10) check ((gender in ('UNKNOWN','FEMALE','MALE','NON_BINARY'))),
        name varchar(80) not null,
        profile uuid unique,
        tmdb_profile varchar(80),
        primary key (id)
    );

    create table emdb_media.Roles (
        id uuid not null,
        episode_count integer not null,
        role varchar(100),
        seriesCredit_id uuid not null,
        primary key (id)
    );

    create table emdb_media.Series (
        id bigint not null,
        created timestamp(6) with time zone not null,
        modified timestamp(6) with time zone not null,
        tmdb_id integer not null unique,
        backdrop uuid unique,
        homepage varchar(2048),
        originalLanguage varchar(2),
        overview varchar(1024),
        poster uuid unique,
        score float4 check ((score>=0) and (score<=10)),
        status varchar(16) check ((status in ('CANCELED','ENDED','IN_PRODUCTION','PILOT','PLANNED','POST_PRODUCTION','RELEASED','RETURNING_SERIES','RUMORED'))),
        tagline varchar(150),
        title varchar(140) not null,
        tmdb_backdrop varchar(80),
        tmdb_poster varchar(80),
        first_air_date date,
        type varchar(11) check ((type in ('SCRIPTED','REALITY','DOCUMENTARY','NEWS','TALK_SHOW','MINISERIES','VIDEO'))),
        primary key (id)
    );

    alter table if exists emdb_media.Credits 
       add constraint FK78ndg1w4qadcmsrfywco5nyi4 
       foreign key (person_id) 
       references emdb_media.People;

    alter table if exists emdb_media.Credits 
       add constraint FKq379b62lvkufu5iwadc4lr38e 
       foreign key (series_id) 
       references emdb_media.Series;

    alter table if exists emdb_media.Credits 
       add constraint FKsalqaewfk7tbamki5csq55yam 
       foreign key (movie_id) 
       references emdb_media.Movies;

    alter table if exists emdb_media.Roles 
       add constraint FK4mmioy1r9g05wf886lg3roku1 
       foreign key (seriesCredit_id) 
       references emdb_media.Credits;
