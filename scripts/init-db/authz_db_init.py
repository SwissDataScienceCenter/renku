import logging
import os
from dataclasses import dataclass, field

from queries import DatabaseInit
from utils import get_db_connection

logging.basicConfig(level=logging.INFO)


@dataclass
class Config:
    db_host: str
    db_admin_username: str
    db_admin_password: str = field(repr=False)
    authz_db_username: str
    authz_db_password: str = field(repr=False)
    authz_db_name: str
    db_port: int = 5432

    @classmethod
    def from_env(cls):
        return cls(
            db_host=os.environ["DB_HOST"],
            db_admin_username=os.environ["DB_ADMIN_USERNAME"],
            db_admin_password=os.environ["DB_ADMIN_PASSWORD"],
            db_port=int(os.environ.get("DB_PORT", 5432)),
            authz_db_username=os.environ["AUTHZ_DB_USERNAME"],
            authz_db_name=os.environ["AUTHZ_DB_NAME"],
            authz_db_password=os.environ["AUTHZ_DB_PASSWORD"],
        )


def main():
    config = Config.from_env()
    postgres_db_connection = get_db_connection(
        user=config.db_admin_username,
        password=config.db_admin_password,
        host=config.db_host,
        port=config.db_port,
    )
    postgres_db_connection.set_session(autocommit=True)
    logging.info("Creating the authz postgres database...")
    db_init = DatabaseInit(
        config.authz_db_username,
        config.authz_db_name,
        config.authz_db_password,
        postgres_db_connection,
        [],
        config.db_admin_username,
    )
    db_init.create_database()
    authz_db_connection = get_db_connection(
        user=config.db_admin_username,
        password=config.db_admin_password,
        host=config.db_host,
        port=config.db_port,
        database=config.authz_db_name,
    )
    # NOTE: Database extensions do not get created in transactions
    authz_db_connection.set_session(autocommit=True)
    db_init.set_connection(authz_db_connection)
    db_init.set_extensions_and_roles()


if __name__ == "__main__":
    main()
