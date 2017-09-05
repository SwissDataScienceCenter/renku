-- Create extension for uuid auto-gen support

create extension "uuid-ossp";

revoke all on schema "public" from "public";

-- Create user

create user "graph-types" password 'graph-types';
grant connect on database "graph-types" to "graph-types";
grant usage on schema "public" to "graph-types";
