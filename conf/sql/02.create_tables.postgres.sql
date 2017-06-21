-- Create tables

create table "ENTITIES" (
    "UUID" UUID default uuid_generate_v4() not null,
    "ENTITY_TYPE" TEXT not null,
    primary key ("UUID")
);

create table "GRAPH_DOMAINS" (
    "UUID" UUID not null,
    "NAMESPACE" TEXT not null,
    primary key ("UUID")
);

create unique index "IDX_GRAPH_DOMAINS_NAMESPACE"
on "GRAPH_DOMAINS" ("NAMESPACE");

alter table "GRAPH_DOMAINS"
add constraint "GRAPH_DOMAINS_FK_ENTITIES"
foreign key("UUID")
references "ENTITIES"("UUID")
on update NO ACTION
on delete NO ACTION;

create table "PROPERTY_KEYS" (
    "UUID" UUID not null,
    "GRAPH_DOMAIN_ID" UUID not null,
    "NAME" TEXT not null,
    "DATA_TYPE" TEXT not null,
    "CARDINALITY" TEXT not null,
    primary key ("UUID")
);

create unique index "IDX_PROPERTY_KEYS_GRAPH_DOMAIN_ID_NAME"
on "PROPERTY_KEYS" ("GRAPH_DOMAIN_ID","NAME");

alter table "PROPERTY_KEYS"
add constraint "PROPERTY_KEYS_FK_ENTITIES"
foreign key("UUID")
references "ENTITIES"("UUID")
on update NO ACTION
on delete NO ACTION;

alter table "PROPERTY_KEYS"
add constraint "PROPERTY_KEYS_FK_GRAPH_DOMAINS"
foreign key("GRAPH_DOMAIN_ID")
references "GRAPH_DOMAINS"("UUID")
on update NO ACTION
on delete NO ACTION;

create table "SYSTEM_PROPERTY_KEYS" (
    "UUID" UUID not null,
    "NAME" TEXT not null,
    "DATA_TYPE" TEXT not null,
    "CARDINALITY" TEXT not null,
    primary key ("UUID")
);

create unique index "IDX_SYSTEM_PROPERTY_KEYS_NAME"
on "SYSTEM_PROPERTY_KEYS" ("NAME");

alter table "SYSTEM_PROPERTY_KEYS"
add constraint "SYSTEM_PROPERTY_KEYS_FK_ENTITIES"
foreign key("UUID")
references "ENTITIES"("UUID")
on update NO ACTION
on delete NO ACTION;

create table "EDGE_LABELS" (
    "UUID" UUID not null,
    "GRAPH_DOMAIN_ID" UUID not null,
    "NAME" TEXT not null,
    "MULTIPLICITY" TEXT not null,
    primary key ("UUID")
);

create unique index "IDX_EDGE_LABELS_GRAPH_DOMAIN_ID_NAME"
on "EDGE_LABELS" ("GRAPH_DOMAIN_ID","NAME");

alter table "EDGE_LABELS"
add constraint "EDGE_LABELS_FK_ENTITIES"
foreign key("UUID")
references "ENTITIES"("UUID")
on update NO ACTION
on delete NO ACTION;

alter table "EDGE_LABELS"
add constraint "EDGE_LABELS_FK_GRAPH_DOMAINS"
foreign key("GRAPH_DOMAIN_ID")
references "GRAPH_DOMAINS"("UUID")
on update NO ACTION
on delete NO ACTION;

create table "NAMED_TYPES" (
    "UUID" UUID not null,
    "GRAPH_DOMAIN_ID" UUID not null,
    "NAME" TEXT not null,
    primary key ("UUID")
);

create unique index "IDX_NAMED_TYPES_GRAPH_DOMAIN_ID_NAME"
on "NAMED_TYPES" ("GRAPH_DOMAIN_ID","NAME");

alter table "NAMED_TYPES"
add constraint "NAMED_TYPES_FK_ENTITIES"
foreign key("UUID")
references "ENTITIES"("UUID")
on update NO ACTION
on delete NO ACTION;

alter table "NAMED_TYPES"
add constraint "NAMED_TYPES_FK_GRAPH_DOMAINS"
foreign key("GRAPH_DOMAIN_ID")
references "GRAPH_DOMAINS"("UUID")
on update NO ACTION
on delete NO ACTION;

create table "NAMED_TYPES_HIERARCHY" (
    "NAMED_TYPE_ID" UUID not null,
    "PARENT_NAMED_TYPE_ID" UUID not null,
    primary key("NAMED_TYPE_ID","PARENT_NAMED_TYPE_ID")
);

alter table "NAMED_TYPES_HIERARCHY"
add constraint "NAMED_TYPES_HIERARCHY_FK_NAMED_TYPES"
foreign key("NAMED_TYPE_ID")
references "NAMED_TYPES"("UUID")
on update NO ACTION
on delete NO ACTION;

alter table "NAMED_TYPES_HIERARCHY"
add constraint "NAMED_TYPES_HIERARCHY_PARENT_FK_NAMED_TYPES"
foreign key("PARENT_NAMED_TYPE_ID")
references "NAMED_TYPES"("UUID")
on update NO ACTION
on delete NO ACTION;

create table "NAMED_TYPES_PROPERTIES" (
    "NAMED_TYPE_ID" UUID not null,
    "PROPERTY_KEY_ID" UUID not null,
    primary key("NAMED_TYPE_ID","PROPERTY_KEY_ID")
);

alter table "NAMED_TYPES_PROPERTIES"
add constraint "NAMED_TYPES_PROPERTIES_FK_NAMED_TYPES"
foreign key("NAMED_TYPE_ID")
references "NAMED_TYPES"("UUID")
on update NO ACTION
on delete NO ACTION;

alter table "NAMED_TYPES_PROPERTIES"
add constraint "NAMED_TYPES_PROPERTIES_FK_PROPERTY_KEYS"
foreign key("PROPERTY_KEY_ID")
references "PROPERTY_KEYS"("UUID")
on update NO ACTION
on delete NO ACTION;

create table "STATES" (
    "ID" BIGSERIAL not null,
    "ENTITY_UUID" UUID not null,
    "STATE" TEXT not null,
    "STATE_TIMESTAMP" BIGINT not null,
    primary key("ID")
);

create index "IDX_STATES_ENTITY_UUID"
on "STATES" ("ENTITY_UUID");

alter table "STATES"
add constraint "STATES_FK_ENTITIES"
foreign key("ENTITY_UUID")
references "ENTITIES"("UUID")
on update NO ACTION
on delete NO ACTION;

create table "TRANSITIONS" (
    "ENTITY_UUID" UUID not null,
    "FROM" BIGINT not null,
    "TO_STATE" TEXT not null,
    "TO_TIMESTAMP" BIGINT not null,
    primary key("ENTITY_UUID","FROM")
);

alter table "TRANSITIONS"
add constraint "TRANSITIONS_FK_ENTITIES"
foreign key("ENTITY_UUID")
references "ENTITIES"("UUID")
on update NO ACTION
on delete NO action;

alter table "TRANSITIONS"
add constraint "TRANSITIONS_FK_STATES"
foreign key("FROM")
references "STATES"("ID")
on update NO ACTION
on delete NO ACTION;

-- Grant access to user "graph-wal"

grant all privileges on table "ENTITIES" to "graph-types";
grant all privileges on table "GRAPH_DOMAINS" to "graph-types";
grant all privileges on table "PROPERTY_KEYS" to "graph-types";
grant all privileges on table "SYSTEM_PROPERTY_KEYS" to "graph-types";
grant all privileges on table "EDGE_LABELS" to "graph-types";
grant all privileges on table "NAMED_TYPES" to "graph-types";
grant all privileges on table "NAMED_TYPES_HIERARCHY" to "graph-types";
grant all privileges on table "NAMED_TYPES_PROPERTIES" to "graph-types";
grant all privileges on table "STATES" to "graph-types";
grant usage, select on SEQUENCE "STATES_ID_seq" to "graph-types";
grant all privileges on table "TRANSITIONS" to "graph-types";
