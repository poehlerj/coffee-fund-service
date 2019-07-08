-- apply changes
create table product (
  id                            uuid not null,
  name                          varchar(255) not null,
  price                         double not null,
  constraint uq_product_name unique (name),
  constraint pk_product primary key (id)
);

create table purchase (
  id                            uuid not null,
  user_id                       uuid not null,
  product_id                    uuid not null,
  quantity                      integer not null,
  when_purchased                timestamp not null,
  constraint pk_purchase primary key (id)
);

create table user (
  id                            uuid not null,
  name                          varchar(255) not null,
  authentication_type           varchar(8) not null,
  password                      varchar(255),
  constraint ck_user_authentication_type check ( authentication_type in ('INTERNAL','LDAP')),
  constraint uq_user_name unique (name),
  constraint pk_user primary key (id)
);

create index ix_purchase_user_id on purchase (user_id);
alter table purchase add constraint fk_purchase_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;

create index ix_purchase_product_id on purchase (product_id);
alter table purchase add constraint fk_purchase_product_id foreign key (product_id) references product (id) on delete restrict on update restrict;

