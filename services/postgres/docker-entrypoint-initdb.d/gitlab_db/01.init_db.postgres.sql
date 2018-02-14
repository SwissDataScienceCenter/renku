
revoke all on schema "public" from "public";

-- Create user

create user "gitlab" password 'gitlab';
grant all privileges on database "gitlab" to "gitlab";
grant all privileges on schema "public" to "gitlab";
