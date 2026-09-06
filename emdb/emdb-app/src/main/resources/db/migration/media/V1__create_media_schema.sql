create table emdb_media.movie (id bigint not null, original_language varchar(2), overview varchar(1000), public_id uuid not null, release_date date, score numeric(5,3), title varchar(140) not null, tmdb_id integer not null, version bigint not null, primary key (id));
alter table if exists emdb_media.movie drop constraint if exists uq_movie_public_id;
alter table if exists emdb_media.movie add constraint uq_movie_public_id unique (public_id);
alter table if exists emdb_media.movie drop constraint if exists uq_movie_tmdb_id;
alter table if exists emdb_media.movie add constraint uq_movie_tmdb_id unique (tmdb_id);
create sequence emdb_media.movie_seq start with 1 increment by 1;
