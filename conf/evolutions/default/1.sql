# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "deposits_places" ("id" SERIAL NOT NULL PRIMARY KEY,"title" VARCHAR(254) NOT NULL,"description" text NOT NULL);
create table "pick_out_places" ("id" SERIAL NOT NULL PRIMARY KEY,"title" VARCHAR(254) NOT NULL,"description" text NOT NULL);

# --- !Downs

drop table "deposits_places";
drop table "pick_out_places";

