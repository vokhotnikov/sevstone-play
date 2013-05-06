# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "deposits_places" ("id" SERIAL NOT NULL PRIMARY KEY,"title" VARCHAR(254) NOT NULL,"description" text NOT NULL);
create table "expositions" ("id" SERIAL NOT NULL PRIMARY KEY,"parent_id" BIGINT,"title" VARCHAR(254) NOT NULL,"description" text NOT NULL,"sort_priority" BIGINT DEFAULT 0 NOT NULL);
create table "pick_out_places" ("id" SERIAL NOT NULL PRIMARY KEY,"title" VARCHAR(254) NOT NULL,"description" text NOT NULL);
alter table "expositions" add constraint "ParentFK" foreign key("parent_id") references "expositions"("id") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "expositions" drop constraint "ParentFK";
drop table "deposits_places";
drop table "expositions";
drop table "pick_out_places";

