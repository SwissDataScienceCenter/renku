-- Create extension for uuid auto-gen support

create extension "uuid-ossp";

revoke all on schema "public" from "public";

-- Create user

create user "graph-wal" password 'graph-wal';
grant connect on database "graph-wal" to "graph-wal";
grant usage on schema "public" to "graph-wal";
