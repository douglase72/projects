
    create table emdb_user.Users (
        id uuid not null,
        email varchar(255) unique,
        firstName varchar(255),
        lastName varchar(255),
        theme varchar(255) not null check ((theme in ('DARK','LIGHT'))),
        username varchar(255) not null unique,
        primary key (id)
    );
