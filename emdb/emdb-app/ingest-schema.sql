create table emdb_ingest.Tmdb_Movies (id integer not null, emdb_backdrop uuid unique, emdb_poster uuid unique, tmdb_backdrop varchar(255) unique, tmdb_poster varchar(255) unique, primary key (id));
