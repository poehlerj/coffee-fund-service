-- apply changes
alter table user add column password varchar(255);

alter table user add constraint uq_user_name unique  (name);
