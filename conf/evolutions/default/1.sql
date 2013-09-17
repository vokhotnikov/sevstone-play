# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "articles" ("title" VARCHAR(254) NOT NULL,"summary" text NOT NULL,"text" text NOT NULL,"image_id" BIGINT NOT NULL,"added_at" DATE NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
create table "categories" ("parent_id" BIGINT,"title" VARCHAR(254) NOT NULL,"is_hidden" BOOLEAN NOT NULL,"sort_priority" BIGINT DEFAULT 0 NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
create table "deposits_places" ("title" VARCHAR(254) NOT NULL,"description" text NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
create table "expositions" ("parent_id" BIGINT,"title" VARCHAR(254) NOT NULL,"description" text NOT NULL,"sort_priority" BIGINT DEFAULT 0 NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
create table "images" ("url" VARCHAR(254) NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
create table "pick_out_places" ("title" VARCHAR(254) NOT NULL,"description" text NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
create table "testimonials" ("author_name" VARCHAR(254) NOT NULL,"author_email" VARCHAR(254),"text" text NOT NULL,"added_at" DATE NOT NULL,"is_approved" BOOLEAN NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
alter table "articles" add constraint "Articles_ImageFK" foreign key("image_id") references "images"("id") on update NO ACTION on delete NO ACTION;
alter table "categories" add constraint "Categories_ParentFK" foreign key("parent_id") references "categories"("id") on update NO ACTION on delete NO ACTION;
alter table "expositions" add constraint "Expositions_ParentFK" foreign key("parent_id") references "expositions"("id") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "articles" drop constraint "Articles_ImageFK";
alter table "categories" drop constraint "Categories_ParentFK";
alter table "expositions" drop constraint "Expositions_ParentFK";
drop table "articles";
drop table "categories";
drop table "deposits_places";
drop table "expositions";
drop table "images";
drop table "pick_out_places";
drop table "testimonials";

