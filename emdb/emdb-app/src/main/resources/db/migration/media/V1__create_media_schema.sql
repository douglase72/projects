create table emdb_media.movie (public_id bigint not null, id uuid not null, original_language varchar(2) not null, release_date date, source varchar(16) not null, source_id varchar(64) not null, title varchar(140) not null, version bigint not null, primary key (public_id));
alter table if exists emdb_media.movie drop constraint if exists uq_movie_source;
alter table if exists emdb_media.movie add constraint uq_movie_source unique (source, source_id);
alter table if exists emdb_media.movie drop constraint if exists uq_movie_uuid;
alter table if exists emdb_media.movie add constraint uq_movie_uuid unique (id);
create sequence emdb_media.movie_sequence start with 1 increment by 1;

