import logging
import os
from dataclasses import dataclass, field

from queries import DatabaseInit
from utils import create_ulid_func, get_db_connection

logging.basicConfig(level=logging.INFO)


@dataclass
class Config:
    db_host: str
    db_admin_username: str
    db_admin_password: str = field(repr=False)
    eventlog_db_username: str
    eventlog_db_name: str
    eventlog_db_password: str = field(repr=False)
    tg_db_username: str
    tg_db_name: str
    tg_db_password: str = field(repr=False)
    tokenrepo_db_username: str
    tokenrepo_db_name: str
    tokenrepo_db_password: str = field(repr=False)
    renku_db_username: str
    renku_db_name: str
    renku_db_password: str = field(repr=False)
    db_port: int = 5432

    @classmethod
    def from_env(cls):
        return cls(
            db_host=os.environ["DB_HOST"],
            db_admin_username=os.environ["DB_ADMIN_USERNAME"],
            db_admin_password=os.environ["DB_ADMIN_PASSWORD"],
            db_port=int(os.environ.get("DB_PORT", 5432)),
            eventlog_db_username=os.environ["EVENTLOG_DB_USERNAME"],
            eventlog_db_password=os.environ["EVENTLOG_DB_PASSWORD"],
            eventlog_db_name=os.environ["EVENTLOG_DB_NAME"],
            tg_db_username=os.environ["TG_DB_USERNAME"],
            tg_db_password=os.environ["TG_DB_PASSWORD"],
            tg_db_name=os.environ["TG_DB_NAME"],
            tokenrepo_db_username=os.environ["TOKENREPO_DB_USERNAME"],
            tokenrepo_db_password=os.environ["TOKENREPO_DB_PASSWORD"],
            tokenrepo_db_name=os.environ["TOKENREPO_DB_NAME"],
            renku_db_username=os.environ["RENKU_DB_USERNAME"],
            renku_db_password=os.environ["RENKU_DB_PASSWORD"],
            renku_db_name=os.environ["RENKU_DB_NAME"],
        )


def main():
    config = Config.from_env()
    logging.info("Waiting for postgres connection...")
    postgres_db_connection = get_db_connection(
        user=config.db_admin_username,
        password=config.db_admin_password,
        host=config.db_host,
        port=config.db_port,
    )
    postgres_db_connection.set_session(autocommit=True)

    logging.info("Creating the knowledge graph triples generator postgres database...")
    db_init = DatabaseInit(
        config.tg_db_username,
        config.tg_db_name,
        config.tg_db_password,
        postgres_db_connection,
        ["pg_trgm"],
        config.db_admin_username,
    )
    db_init.create_database()
    tg_conn = get_db_connection(
        user=config.db_admin_username,
        password=config.db_admin_password,
        host=config.db_host,
        port=config.db_port,
        database=config.tg_db_name,
    )
    # NOTE: Database extensions do not get created in transactions
    tg_conn.set_session(autocommit=True)
    db_init.set_connection(tg_conn)
    db_init.set_extensions_and_roles()

    logging.info("Creating the knowledge graph token repository postgres database...")
    db_init = DatabaseInit(
        config.tokenrepo_db_username,
        config.tokenrepo_db_name,
        config.tokenrepo_db_password,
        postgres_db_connection,
        ["pg_trgm"],
        config.db_admin_username,
    )
    db_init.create_database()
    tokenrepo_conn = get_db_connection(
        user=config.db_admin_username,
        password=config.db_admin_password,
        host=config.db_host,
        port=config.db_port,
        database=config.tokenrepo_db_name,
    )
    # NOTE: Database extensions do not get created in transactions
    tokenrepo_conn.set_session(autocommit=True)
    db_init.set_connection(tokenrepo_conn)
    db_init.set_extensions_and_roles()

    logging.info("Creating the knowledge grpah eventlog postgres database...")
    db_init = DatabaseInit(
        config.eventlog_db_username,
        config.eventlog_db_name,
        config.eventlog_db_password,
        postgres_db_connection,
        ["pg_trgm"],
        config.db_admin_username,
    )
    db_init.create_database()
    eventlog_conn = get_db_connection(
        user=config.db_admin_username,
        password=config.db_admin_password,
        host=config.db_host,
        port=config.db_port,
        database=config.eventlog_db_name,
    )
    # NOTE: Database extensions do not get created in transactions
    eventlog_conn.set_session(autocommit=True)
    db_init.set_connection(eventlog_conn)
    db_init.set_extensions_and_roles()

    logging.info("Creating the renku postgres database...")
    db_init = DatabaseInit(
        config.renku_db_username,
        config.renku_db_name,
        config.renku_db_password,
        postgres_db_connection,
        ["pg_trgm", "pgcrypto"],
        config.db_admin_username,
    )
    db_init.create_database()
    renku_conn = get_db_connection(
        user=config.db_admin_username,
        password=config.db_admin_password,
        host=config.db_host,
        port=config.db_port,
        database=config.renku_db_name,
    )
    # NOTE: Database extensions do not get created in transactions
    renku_conn.set_session(autocommit=True)
    db_init.set_connection(renku_conn)
    db_init.set_extensions_and_roles()
    create_ulid_func(config.db_admin_username, config.db_admin_password, config.renku_db_name, config.db_host, config.db_port)


if __name__ == "__main__":
    main()
