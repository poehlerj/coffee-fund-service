-- apply changes
create table product (
  id                            uuid not null,
  name                          varchar(255) not null,
  price                         double not null,
  constraint pk_product primary key (id)
);

