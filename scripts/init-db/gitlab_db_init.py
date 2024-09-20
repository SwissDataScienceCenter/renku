import logging
import os
from dataclasses import dataclass, field

from queries import DatabaseInit, GitlabOauthDatabaseCleanup
from utils import get_db_connection, gitlab_is_online

logging.basicConfig(level=logging.INFO)


@dataclass
class Config:
    db_host: str
    db_admin_username: str
    db_admin_password: str = field(repr=False)
    gitlab_db_username: str
    gitlab_db_name: str
    gitlab_db_password: str = field(repr=False)
    gitlab_url: str
    renku_url: str
    gitlab_oauth_client_secret: str = field(repr=False)
    gitlab_oauth_client_id: str = "renku-ui"
    db_port: int = 5432

    def __post_init__(self):
        self.renku_url = self.renku_url.rstrip("/")
        self.gitlab_url = self.gitlab_url.rstrip("/")

    @classmethod
    def from_env(cls):
        return cls(
            db_host=os.environ["DB_HOST"],
            db_admin_username=os.environ["DB_ADMIN_USERNAME"],
            db_admin_password=os.environ["DB_ADMIN_PASSWORD"],
            db_port=int(os.environ.get("DB_PORT", 5432)),
            gitlab_oauth_client_secret=os.environ["GITLAB_OAUTH_CLIENT_SECRET"],
            gitlab_oauth_client_id=os.environ["GITLAB_OAUTH_CLIENT_ID"],
            gitlab_db_password=os.environ["GITLAB_DB_PASSWORD"],
            gitlab_db_username=os.environ["GITLAB_DB_USERNAME"],
            gitlab_db_name=os.environ["GITLAB_DB_NAME"],
            gitlab_url=os.environ["GITLAB_URL"],
            renku_url=os.environ["RENKU_URL"],
        )


def main():
    config = Config.from_env()
    # Wait for services to come online
    postgres_db_connection = get_db_connection(
        user=config.db_admin_username,
        password=config.db_admin_password,
        host=config.db_host,
        port=config.db_port,
    )
    postgres_db_connection.set_session(autocommit=True)

    # Run queries
    logging.info("Creating the gitlab postgres database...")
    db_init = DatabaseInit(
        config.gitlab_db_username,
        config.gitlab_db_name,
        config.gitlab_db_password,
        postgres_db_connection,
        ["pg_trgm", "btree_gist"],
        config.db_admin_username,
    )
    db_init.create_database()
    gitlab_db_connection = get_db_connection(
        user=config.db_admin_username,
        password=config.db_admin_password,
        host=config.db_host,
        port=config.db_port,
        database=config.gitlab_db_name,
    )
    # NOTE: Database extensions do not get created in transactions
    gitlab_db_connection.set_session(autocommit=True)
    db_init.set_connection(gitlab_db_connection)
    db_init.set_extensions_and_roles()
    # NOTE: Querying for the base url switches the request to port 443 and the service does not
    # expose port 443.
    gitlab_is_online(config.gitlab_url + "/help")
    GitlabOauthDatabaseCleanup(
        config.gitlab_oauth_client_id,
        config.gitlab_oauth_client_secret,
        [
            f"{config.renku_url}/api/auth/callback"
            f"{config.renku_url}/api/auth/gitlab/token",
        ],
        gitlab_db_connection,
    ).run()


if __name__ == "__main__":
    main()
