-- Create tables

create table "REQUEST_LOG" (
	"UUID" UUID default uuid_generate_v4() not null,
	"EVENT" TEXT not null,
	"CREATED" BIGINT default (extract(epoch from date_trunc('milliseconds', now() at time zone 'utc')) * 1000) not null,
	primary key ("UUID")
);

create table "RESPONSE_LOG" (
	"UUID" UUID not null,
	"EVENT" TEXT not null,
	"CREATED" BIGINT default (extract(epoch from date_trunc('milliseconds', now() at time zone 'utc')) * 1000) not null,
	primary key ("UUID")
);

alter table "RESPONSE_LOG"
add constraint "RESPONSE_FK_REQUEST"
foreign key("UUID")
references "REQUEST_LOG"("UUID")
on update NO action
on delete NO action;

-- Grant access to user "graph-wal"

grant select on "REQUEST_LOG" to "graph-wal";
grant insert ("EVENT") on "REQUEST_LOG" to "graph-wal"; -- allow insert only on column "EVENT"

grant select on "RESPONSE_LOG" to "graph-wal";
grant insert ("UUID", "EVENT") on "RESPONSE_LOG" to "graph-wal";
