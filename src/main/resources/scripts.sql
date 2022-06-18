create extension hstore;
create table if not exists "Movie" ("movie_id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"release_date" DATE NOT NULL,"length_in_min" INTEGER NOT NULL);
create table if not exists "Actor" ("actor_id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL);
create table if not exists "MovieActorMapping" ("movie_actor_id" BIGSERIAL NOT NULL PRIMARY KEY,"movie_id" BIGINT NOT NULL,"actor_id" BIGINT NOT NULL);
create table if not exists "StreamingProviderMapping" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"movie_id" BIGINT NOT NULL,"streaming_provider" VARCHAR NOT NULL);
create table if not exists "MovieLocations" ("movie_location_id" BIGSERIAL NOT NULL PRIMARY KEY,"movie_id" BIGINT NOT NULL,"locations" text [] NOT NULL);
create table if not exists "MovieProperties" ("id" bigserial NOT NULL PRIMARY KEY,"movie_id" BIGINT NOT NULL,"properties" hstore NOT NULL);
create table if not exists "ActorDetails" ("id" bigserial NOT NULL PRIMARY KEY,"actor_id" BIGINT NOT NULL,"personal_info" jsonb NOT NULL);