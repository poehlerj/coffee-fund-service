-- apply changes
create table user (
  id                            uuid not null,
  name                          varchar(255) not null,
  authentication_type           varchar(8) not null,
  constraint ck_user_authentication_type check ( authentication_type in ('INTERNAL','LDAP')),
  constraint pk_user primary key (id)
);

