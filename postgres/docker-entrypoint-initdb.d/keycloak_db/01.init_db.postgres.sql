
revoke all on schema "public" from "public";

-- Create user

create user "keycloak" password 'keycloak';
grant all privileges on database "keycloak" to "keycloak";
grant all privileges on schema "public" to "keycloak";
