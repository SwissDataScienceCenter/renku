import logging
from dataclasses import dataclass, field
from typing import Any, List

from psycopg2 import sql

gitlab_oauth_cleanup = sql.SQL(
    "DELETE FROM oauth_applications WHERE uid={client_id}; "
    "INSERT INTO oauth_applications (name, uid, scopes, redirect_uri, secret, trusted) "
    "VALUES ('renku-ui', {client_id}, 'api read_user read_repository read_registry openid', {redirect_uris}, {client_secret}, 'true')"
).format(
    client_id=sql.Placeholder("client_id"),
    redirect_uris=sql.Placeholder("redirect_uris"),
    client_secret=sql.Placeholder("client_secret"),
)


@dataclass
class DatabaseInit:
    username: str
    db_name: str
    password: str = field(repr=False)
    connection: Any
    extensions: List[str] = field(default_factory=lambda: ["pg_trgm"])
    admin_user: str = "postgres"

    def set_connection(self, connection) -> "DatabaseInit":
        self.connection = connection
        return self

    def set_extensions_and_roles(self):
        with self.connection.cursor() as curs:
            logging.info(f"Creating extensions {self.extensions} and adjusting privileges...")
            extensions = [
                sql.SQL("CREATE EXTENSION IF NOT EXISTS {};").format(sql.Identifier(ext)) for ext in self.extensions
            ]
            extensions = sql.SQL(" ").join(extensions)
            query = sql.SQL(
                "{extensions}"
                'REVOKE ALL ON SCHEMA "public" FROM "public"; '
                "GRANT ALL PRIVILEGES ON DATABASE {db_name} TO {username}; "
                "GRANT ALL PRIVILEGES ON DATABASE {db_name} TO {admin_user}; "
                'GRANT ALL PRIVILEGES ON SCHEMA "public" TO {username};'
            ).format(
                username=sql.Identifier(self.username),
                admin_user=sql.Identifier(self.admin_user),
                extensions=extensions,
                db_name=sql.Identifier(self.db_name),
                password=sql.Placeholder("password"),
            )
            curs.execute(query, {"password": self.password})

    def create_database(self):
        with self.connection.cursor() as curs:
            if not self.user_exists:
                logging.info(f"User {self.username} does not exist, creating it...")
                query = sql.SQL("CREATE USER {username} PASSWORD {password};").format(
                    username=sql.Identifier(self.username), password=sql.Placeholder("password")
                )
                curs.execute(query, {"password": self.password})
            else:
                logging.info(f"User {self.username} exists, skipping creation")

            if not self.database_exists:
                logging.info(f"Database {self.db_name} does not exist, creating it...")
                query = sql.SQL("CREATE DATABASE {db_name} WITH OWNER {username};").format(
                    db_name=sql.Identifier(self.db_name), username=sql.Identifier(self.username)
                )
                curs.execute(query, {"password": self.password})
            else:
                logging.info(f"Database {self.db_name} exists, skipping creation")

    @property
    def database_exists(self):
        q = sql.SQL("SELECT * FROM {pg_database} WHERE datname={db_name};").format(
            pg_database=sql.Identifier("pg_database"), db_name=sql.Placeholder("db_name")
        )
        with self.connection.cursor() as curs:
            curs.execute(q, {"db_name": self.db_name})
            if len(curs.fetchall()) == 0:
                return False
            return True

    @property
    def user_exists(self):
        q = sql.SQL("SELECT * FROM {pg_user} where usename = {username};").format(
            pg_user=sql.Identifier("pg_user"), username=sql.Placeholder("username")
        )
        with self.connection.cursor() as curs:
            curs.execute(q, {"username": self.username})
            if len(curs.fetchall()) == 0:
                return False
            return True
