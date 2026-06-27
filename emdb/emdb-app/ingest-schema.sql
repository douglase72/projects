create table emdb_ingest.movies (id integer not null, emdb_backdrop uuid unique, emdb_poster uuid unique, tmdb_backdrop varchar(255) unique, tmdb_poster varchar(255) unique, primary key (id));
create table emdb_ingest.people (id integer not null, emdb_profile uuid unique, tmdb_profile varchar(255) unique, primary key (id));
create table emdb_ingest.series (id integer not null, emdb_backdrop uuid unique, emdb_poster uuid unique, tmdb_backdrop varchar(255) unique, tmdb_poster varchar(255) unique, primary key (id));
