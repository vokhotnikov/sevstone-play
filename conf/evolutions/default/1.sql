# --- !Ups

create table "articles" ("title" VARCHAR(254) NOT NULL,"summary" text NOT NULL,"text" text NOT NULL,"image_id" BIGINT NOT NULL,"added_at" TIMESTAMP NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
create table "categories" ("parent_id" BIGINT,"title" VARCHAR(254) NOT NULL,"is_hidden" BOOLEAN NOT NULL,"sort_priority" BIGINT DEFAULT 0 NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
create table "deposits_places" ("title" VARCHAR(254) NOT NULL,"description" text NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
create table "expositions" ("parent_id" BIGINT,"title" VARCHAR(254) NOT NULL,"description" text NOT NULL,"sort_priority" BIGINT DEFAULT 0 NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
create table "images" ("url" VARCHAR(254) NOT NULL,"added_at" TIMESTAMP NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
create table "pick_out_places" ("title" VARCHAR(254) NOT NULL,"description" text NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
create table "specimen_photos" ("specimen_id" BIGINT NOT NULL,"image_id" BIGINT NOT NULL,"is_main" BOOLEAN NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
create table "specimens" ("name" VARCHAR(254) NOT NULL,"name_latin" VARCHAR(254),"size" VARCHAR(254),"formula" VARCHAR(254),"age" VARCHAR(254),"label" text NOT NULL,"short_description" text NOT NULL,"description" text NOT NULL,"show_on_site" BOOLEAN NOT NULL,"priority" INTEGER NOT NULL,"category_id" BIGINT NOT NULL,"exposition_id" BIGINT NOT NULL,"deposits_place_id" BIGINT NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
create table "testimonials" ("author_name" VARCHAR(254) NOT NULL,"author_email" VARCHAR(254),"text" text NOT NULL,"added_at" TIMESTAMP NOT NULL,"is_approved" BOOLEAN NOT NULL,"id" SERIAL NOT NULL PRIMARY KEY);
alter table "articles" add constraint "Articles_ImageFK" foreign key("image_id") references "images"("id") on update NO ACTION on delete NO ACTION;
alter table "categories" add constraint "Categories_ParentFK" foreign key("parent_id") references "categories"("id") on update NO ACTION on delete NO ACTION;
alter table "expositions" add constraint "Expositions_ParentFK" foreign key("parent_id") references "expositions"("id") on update NO ACTION on delete NO ACTION;
alter table "specimen_photos" add constraint "SpecimenPhotos_ImageFK" foreign key("image_id") references "images"("id") on update NO ACTION on delete NO ACTION;
alter table "specimen_photos" add constraint "SpecimenPhotos_SpecimenFK" foreign key("specimen_id") references "specimens"("id") on update NO ACTION on delete NO ACTION;
alter table "specimens" add constraint "Specimens_CategoryFK" foreign key("category_id") references "categories"("id") on update NO ACTION on delete NO ACTION;
alter table "specimens" add constraint "Specimens_ExpositionFK" foreign key("exposition_id") references "expositions"("id") on update NO ACTION on delete NO ACTION;
alter table "specimens" add constraint "Specimens_DepositsPlaceFK" foreign key("deposits_place_id") references "deposits_places"("id") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "articles" drop constraint "Articles_ImageFK";
alter table "categories" drop constraint "Categories_ParentFK";
alter table "expositions" drop constraint "Expositions_ParentFK";
alter table "specimen_photos" drop constraint "SpecimenPhotos_ImageFK";
alter table "specimen_photos" drop constraint "SpecimenPhotos_SpecimenFK";
alter table "specimens" drop constraint "Specimens_CategoryFK";
alter table "specimens" drop constraint "Specimens_ExpositionFK";
alter table "specimens" drop constraint "Specimens_DepositsPlaceFK";
drop table "articles";
drop table "categories";
drop table "deposits_places";
drop table "expositions";
drop table "images";
drop table "pick_out_places";
drop table "specimen_photos";
drop table "specimens";
drop table "testimonials";

