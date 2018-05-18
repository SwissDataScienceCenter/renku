-- Create extension for uuid auto-gen support

create extension "uuid-ossp";

revoke all on schema "public" from "public";

-- Create user

create user "storage" password 'storage';
grant all privileges on database "storage" to "storage";
grant all privileges on schema "public" to "storage";
