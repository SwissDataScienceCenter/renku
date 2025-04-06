import logging
import os
from dataclasses import dataclass, field

from queries import DatabaseInit
from utils import create_ulid_func, get_db_connection

logging.basicConfig(level=logging.INFO)


@dataclass
class AdminCredentials:
    username: str
    password: str = field(repr=False)

    @classmethod
    def from_env(cls):
        return cls(
            username=os.environ["DB_ADMIN_USERNAME"],
            password=os.environ["DB_ADMIN_PASSWORD"],
        )


@dataclass
class DBGeneral:
    host: str
    port: int = 5432

    @classmethod
    def from_env(cls):
        return cls(
            host=os.environ["DB_HOST"],
            port=int(os.environ.get("DB_PORT", 5432)),
        )


@dataclass
class DBCredentials:
    username: str
    db_name: str
    password: str = field(repr=False)

    @classmethod
    def from_env(cls, env_prefix: str = ""):
        return cls(
            username=os.environ[f"{env_prefix}USERNAME"],
            password=os.environ[f"{env_prefix}PASSWORD"],
            db_name=os.environ[f"{env_prefix}NAME"],
        )


def main():
    general_config = DBGeneral.from_env()
    admin_config = AdminCredentials.from_env()
    logging.info("Waiting for postgres connection...")
    postgres_db_connection = get_db_connection(
        user=admin_config.username,
        password=admin_config.password,
        host=general_config.host,
        port=general_config.port,
    )
    postgres_db_connection.set_session(autocommit=True)

    try:
        tg_config = DBCredentials.from_env("TG_DB_")
    except KeyError:
        logging.info("Skipping creation of graph triples generator db.")
    else:
        logging.info(
            "Creating the knowledge graph triples generator postgres database..."
        )
        db_init = DatabaseInit(
            tg_config.username,
            tg_config.db_name,
            tg_config.password,
            postgres_db_connection,
            ["pg_trgm"],
            admin_config.username,
        )
        db_init.create_database()
        tg_conn = get_db_connection(
            user=admin_config.username,
            password=admin_config.password,
            host=general_config.host,
            port=general_config.port,
            database=tg_config.db_name,
        )
        # NOTE: Database extensions do not get created in transactions
        tg_conn.set_session(autocommit=True)
        db_init.set_connection(tg_conn)
        db_init.set_extensions_and_roles()

    try:
        tr_config = DBCredentials.from_env("TOKENREPO_DB_")
    except KeyError:
        logging.info("Skipping creation of graph token repository db.")
    else:
        logging.info(
            "Creating the knowledge graph token repository postgres database..."
        )
        db_init = DatabaseInit(
            tr_config.db_username,
            tr_config.db_name,
            tr_config.password,
            postgres_db_connection,
            ["pg_trgm"],
            admin_config.username,
        )
        db_init.create_database()
        tokenrepo_conn = get_db_connection(
            user=admin_config.username,
            password=admin_config.password,
            host=general_config.host,
            port=general_config.port,
            database=tr_config.db_name,
        )
        # NOTE: Database extensions do not get created in transactions
        tokenrepo_conn.set_session(autocommit=True)
        db_init.set_connection(tokenrepo_conn)
        db_init.set_extensions_and_roles()

    try:
        el_config = DBCredentials.from_env("EVENTLOG_DB_")
    except KeyError:
        logging.info("Skipping creation of graph event log db.")
    else:
        logging.info("Creating the knowledge graph eventlog postgres database...")
        db_init = DatabaseInit(
            el_config.username,
            el_config.db_name,
            el_config.password,
            postgres_db_connection,
            ["pg_trgm"],
            admin_config.username,
        )
        db_init.create_database()
        eventlog_conn = get_db_connection(
            user=admin_config.username,
            password=admin_config.password,
            host=general_config.host,
            port=general_config.port,
            database=el_config.db_name,
        )
        # NOTE: Database extensions do not get created in transactions
        eventlog_conn.set_session(autocommit=True)
        db_init.set_connection(eventlog_conn)
        db_init.set_extensions_and_roles()

    logging.info("Creating the renku postgres database...")
    renku_config = DBCredentials.from_env("RENKU_DB_")
    db_init = DatabaseInit(
        renku_config.username,
        renku_config.db_name,
        renku_config.password,
        postgres_db_connection,
        ["pg_trgm", "pgcrypto"],
        admin_config.username,
    )
    db_init.create_database()
    renku_conn = get_db_connection(
        user=admin_config.username,
        password=admin_config.password,
        host=general_config.host,
        port=general_config.port,
        database=renku_config.db_name,
    )
    # NOTE: Database extensions do not get created in transactions
    renku_conn.set_session(autocommit=True)
    db_init.set_connection(renku_conn)
    db_init.set_extensions_and_roles()
    create_ulid_func(
        admin_config.username,
        admin_config.password,
        renku_config.db_name,
        general_config.host,
        general_config.port,
    )


if __name__ == "__main__":
    main()
