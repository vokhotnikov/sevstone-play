# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "categories" ("id" SERIAL NOT NULL PRIMARY KEY,"parent_id" BIGINT,"title" VARCHAR(254) NOT NULL,"is_hidden" BOOLEAN NOT NULL,"sort_priority" BIGINT DEFAULT 0 NOT NULL);
create table "deposits_places" ("id" SERIAL NOT NULL PRIMARY KEY,"title" VARCHAR(254) NOT NULL,"description" text NOT NULL);
create table "expositions" ("id" SERIAL NOT NULL PRIMARY KEY,"parent_id" BIGINT,"title" VARCHAR(254) NOT NULL,"description" text NOT NULL,"sort_priority" BIGINT DEFAULT 0 NOT NULL);
create table "pick_out_places" ("id" SERIAL NOT NULL PRIMARY KEY,"title" VARCHAR(254) NOT NULL,"description" text NOT NULL);
alter table "categories" add constraint "Categories_ParentFK" foreign key("parent_id") references "categories"("id") on update NO ACTION on delete NO ACTION;
alter table "expositions" add constraint "Expositions_ParentFK" foreign key("parent_id") references "expositions"("id") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "categories" drop constraint "Categories_ParentFK";
alter table "expositions" drop constraint "Expositions_ParentFK";
drop table "categories";
drop table "deposits_places";
drop table "expositions";
drop table "pick_out_places";

