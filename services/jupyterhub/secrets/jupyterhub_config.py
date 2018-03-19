# Configuration file for jupyterhub.

#------------------------------------------------------------------------------
# Application(SingletonConfigurable) configuration
#------------------------------------------------------------------------------

## This is an application.

## The date format used by logging formatters for %(asctime)s
#c.Application.log_datefmt = '%Y-%m-%d %H:%M:%S'

## The Logging format template
#c.Application.log_format = '[%(name)s]%(highlevel)s %(message)s'

## Set the log level by value or name.
#c.Application.log_level = 30

#------------------------------------------------------------------------------
# JupyterHub(Application) configuration
#------------------------------------------------------------------------------

## An Application for starting a Multi-User Jupyter Notebook server.

## Maximum number of concurrent servers that can be active at a time.
#  
#  Setting this can limit the total resources your users can consume.
#  
#  An active server is any server that's not fully stopped. It is considered
#  active from the time it has been requested until the time that it has
#  completely stopped.
#  
#  If this many user servers are active, users will not be able to launch new
#  servers until a server is shutdown. Spawn requests will be rejected with a 429
#  error asking them to try again.
#  
#  If set to 0, no limit is enforced.
#c.JupyterHub.active_server_limit = 0

## Grant admin users permission to access single-user servers.
#  
#  Users should be properly informed if this is enabled.
#c.JupyterHub.admin_access = False

## DEPRECATED since version 0.7.2, use Authenticator.admin_users instead.
#c.JupyterHub.admin_users = set()

## Allow named single-user servers per user
#c.JupyterHub.allow_named_servers = False

## Answer yes to any questions (e.g. confirm overwrite)
#c.JupyterHub.answer_yes = False

## PENDING DEPRECATION: consider using service_tokens
#  
#  Dict of token:username to be loaded into the database.
#  
#  Allows ahead-of-time generation of API tokens for use by externally managed
#  services, which authenticate as JupyterHub users.
#  
#  Consider using service_tokens for general services that talk to the JupyterHub
#  API.
#c.JupyterHub.api_tokens = {}

## Class for authenticating users.
#  
#  This should be a class with the following form:
#  
#  - constructor takes one kwarg: `config`, the IPython config object.
#  
#  - is a tornado.gen.coroutine
#  - returns username on success, None on failure
#  - takes two arguments: (handler, data),
#    where `handler` is the calling web.RequestHandler,
#    and `data` is the POST form data from the login page.
#c.JupyterHub.authenticator_class = 'jupyterhub.auth.PAMAuthenticator'

## The base URL of the entire application
#c.JupyterHub.base_url = '/'

## Whether to shutdown the proxy when the Hub shuts down.
#  
#  Disable if you want to be able to teardown the Hub while leaving the proxy
#  running.
#  
#  Only valid if the proxy was starting by the Hub process.
#  
#  If both this and cleanup_servers are False, sending SIGINT to the Hub will
#  only shutdown the Hub, leaving everything else running.
#  
#  The Hub should be able to resume from database state.
#c.JupyterHub.cleanup_proxy = True

## Whether to shutdown single-user servers when the Hub shuts down.
#  
#  Disable if you want to be able to teardown the Hub while leaving the single-
#  user servers running.
#  
#  If both this and cleanup_proxy are False, sending SIGINT to the Hub will only
#  shutdown the Hub, leaving everything else running.
#  
#  The Hub should be able to resume from database state.
#c.JupyterHub.cleanup_servers = True

## Maximum number of concurrent users that can be spawning at a time.
#  
#  Spawning lots of servers at the same time can cause performance problems for
#  the Hub or the underlying spawning system. Set this limit to prevent bursts of
#  logins from attempting to spawn too many servers at the same time.
#  
#  This does not limit the number of total running servers. See
#  active_server_limit for that.
#  
#  If more than this many users attempt to spawn at a time, their requests will
#  be rejected with a 429 error asking them to try again. Users will have to wait
#  for some of the spawning services to finish starting before they can start
#  their own.
#  
#  If set to 0, no limit is enforced.
#c.JupyterHub.concurrent_spawn_limit = 100

## The config file to load
#c.JupyterHub.config_file = 'jupyterhub_config.py'

## DEPRECATED: does nothing
#c.JupyterHub.confirm_no_ssl = False

## Number of days for a login cookie to be valid. Default is two weeks.
#c.JupyterHub.cookie_max_age_days = 14

## The cookie secret to use to encrypt cookies.
#  
#  Loaded from the JPY_COOKIE_SECRET env variable by default.
#  
#  Should be exactly 256 bits (32 bytes).
#c.JupyterHub.cookie_secret = b''

## File in which to store the cookie secret.
#c.JupyterHub.cookie_secret_file = 'jupyterhub_cookie_secret'

## The location of jupyterhub data files (e.g. /usr/local/share/jupyter/hub)
#c.JupyterHub.data_files_path = '/Users/rok/.virtualenvs/renga/share/jupyter/hub'

## Include any kwargs to pass to the database connection. See
#  sqlalchemy.create_engine for details.
#c.JupyterHub.db_kwargs = {}

## url for the database. e.g. `sqlite:///jupyterhub.sqlite`
#c.JupyterHub.db_url = 'sqlite:///jupyterhub.sqlite'

## log all database transactions. This has A LOT of output
#c.JupyterHub.debug_db = False

## DEPRECATED since version 0.8: Use ConfigurableHTTPProxy.debug
#c.JupyterHub.debug_proxy = False

## Send JupyterHub's logs to this file.
#  
#  This will *only* include the logs of the Hub itself, not the logs of the proxy
#  or any single-user servers.
#c.JupyterHub.extra_log_file = ''

## Extra log handlers to set on JupyterHub logger
#c.JupyterHub.extra_log_handlers = []

## Generate default config file
#c.JupyterHub.generate_config = False

## The ip or hostname for proxies and spawners to use for connecting to the Hub.
#  
#  Use when the bind address (`hub_ip`) is 0.0.0.0 or otherwise different from
#  the connect address.
#  
#  Default: when `hub_ip` is 0.0.0.0, use `socket.gethostname()`, otherwise use
#  `hub_ip`.
#  
#  .. versionadded:: 0.8
#c.JupyterHub.hub_connect_ip = ''

## The port for proxies & spawners to connect to the hub on.
#  
#  Used alongside `hub_connect_ip`
#  
#  .. versionadded:: 0.8
#c.JupyterHub.hub_connect_port = 0

## The ip address for the Hub process to *bind* to.
#  
#  See `hub_connect_ip` for cases where the bind and connect address should
#  differ.
#c.JupyterHub.hub_ip = '127.0.0.1'

## The port for the Hub process
#c.JupyterHub.hub_port = 8081

## The public facing ip of the whole application (the proxy)
#c.JupyterHub.ip = ''

## Supply extra arguments that will be passed to Jinja environment.
#c.JupyterHub.jinja_environment_options = {}

## Interval (in seconds) at which to update last-activity timestamps.
#c.JupyterHub.last_activity_interval = 300

## Dict of 'group': ['usernames'] to load at startup.
#  
#  This strictly *adds* groups and users to groups.
#  
#  Loading one set of groups, then starting JupyterHub again with a different set
#  will not remove users or groups from previous launches. That must be done
#  through the API.
#c.JupyterHub.load_groups = {}

## Specify path to a logo image to override the Jupyter logo in the banner.
#c.JupyterHub.logo_file = ''

## File to write PID Useful for daemonizing jupyterhub.
#c.JupyterHub.pid_file = ''

## The public facing port of the proxy
#c.JupyterHub.port = 8000

## DEPRECATED since version 0.8 : Use ConfigurableHTTPProxy.api_url
#c.JupyterHub.proxy_api_ip = ''

## DEPRECATED since version 0.8 : Use ConfigurableHTTPProxy.api_url
#c.JupyterHub.proxy_api_port = 0

## DEPRECATED since version 0.8: Use ConfigurableHTTPProxy.auth_token
#c.JupyterHub.proxy_auth_token = ''

## Interval (in seconds) at which to check if the proxy is running.
#c.JupyterHub.proxy_check_interval = 30

## Select the Proxy API implementation.
#c.JupyterHub.proxy_class = 'jupyterhub.proxy.ConfigurableHTTPProxy'

## DEPRECATED since version 0.8. Use ConfigurableHTTPProxy.command
#c.JupyterHub.proxy_cmd = []

## Purge and reset the database.
#c.JupyterHub.reset_db = False

## Interval (in seconds) at which to check connectivity of services with web
#  endpoints.
#c.JupyterHub.service_check_interval = 60

## Dict of token:servicename to be loaded into the database.
#  
#  Allows ahead-of-time generation of API tokens for use by externally managed
#  services.
#c.JupyterHub.service_tokens = {}

## List of service specification dictionaries.
#  
#  A service
#  
#  For instance::
#  
#      services = [
#          {
#              'name': 'cull_idle',
#              'command': ['/path/to/cull_idle_servers.py'],
#          },
#          {
#              'name': 'formgrader',
#              'url': 'http://127.0.0.1:1234',
#              'api_token': 'super-secret',
#              'environment':
#          }
#      ]
#c.JupyterHub.services = []

## The class to use for spawning single-user servers.
#  
#  Should be a subclass of Spawner.
#c.JupyterHub.spawner_class = 'jupyterhub.spawner.LocalProcessSpawner'

## Path to SSL certificate file for the public facing interface of the proxy
#  
#  When setting this, you should also set ssl_key
#c.JupyterHub.ssl_cert = ''

## Path to SSL key file for the public facing interface of the proxy
#  
#  When setting this, you should also set ssl_cert
#c.JupyterHub.ssl_key = ''

## Host to send statsd metrics to
#c.JupyterHub.statsd_host = ''

## Port on which to send statsd metrics about the hub
#c.JupyterHub.statsd_port = 8125

## Prefix to use for all metrics sent by jupyterhub to statsd
#c.JupyterHub.statsd_prefix = 'jupyterhub'

## Run single-user servers on subdomains of this host.
#  
#  This should be the full `https://hub.domain.tld[:port]`.
#  
#  Provides additional cross-site protections for javascript served by single-
#  user servers.
#  
#  Requires `<username>.hub.domain.tld` to resolve to the same host as
#  `hub.domain.tld`.
#  
#  In general, this is most easily achieved with wildcard DNS.
#  
#  When using SSL (i.e. always) this also requires a wildcard SSL certificate.
#c.JupyterHub.subdomain_host = ''

## Paths to search for jinja templates.
#c.JupyterHub.template_paths = []

## Extra settings overrides to pass to the tornado application.
#c.JupyterHub.tornado_settings = {}

## Trust user-provided tokens (via JupyterHub.service_tokens) to have good
#  entropy.
#  
#  If you are not inserting additional tokens via configuration file, this flag
#  has no effect.
#  
#  In JupyterHub 0.8, internally generated tokens do not pass through additional
#  hashing because the hashing is costly and does not increase the entropy of
#  already-good UUIDs.
#  
#  User-provided tokens, on the other hand, are not trusted to have good entropy
#  by default, and are passed through many rounds of hashing to stretch the
#  entropy of the key (i.e. user-provided tokens are treated as passwords instead
#  of random keys). These keys are more costly to check.
#  
#  If your inserted tokens are generated by a good-quality mechanism, e.g.
#  `openssl rand -hex 32`, then you can set this flag to True to reduce the cost
#  of checking authentication tokens.
#c.JupyterHub.trust_user_provided_tokens = False

## Upgrade the database automatically on start.
#  
#  Only safe if database is regularly backed up. Only SQLite databases will be
#  backed up to a local file automatically.
#c.JupyterHub.upgrade_db = False

#------------------------------------------------------------------------------
# Spawner(LoggingConfigurable) configuration
#------------------------------------------------------------------------------

## Base class for spawning single-user notebook servers.
#  
#  Subclass this, and override the following methods:
#  
#  - load_state - get_state - start - stop - poll
#  
#  As JupyterHub supports multiple users, an instance of the Spawner subclass is
#  created for each user. If there are 20 JupyterHub users, there will be 20
#  instances of the subclass.

## Extra arguments to be passed to the single-user server.
#  
#  Some spawners allow shell-style expansion here, allowing you to use
#  environment variables here. Most, including the default, do not. Consult the
#  documentation for your spawner to verify!
#c.Spawner.args = []

## The command used for starting the single-user server.
#  
#  Provide either a string or a list containing the path to the startup script
#  command. Extra arguments, other than this path, should be provided via `args`.
#  
#  This is usually set if you want to start the single-user server in a different
#  python environment (with virtualenv/conda) than JupyterHub itself.
#  
#  Some spawners allow shell-style expansion here, allowing you to use
#  environment variables. Most, including the default, do not. Consult the
#  documentation for your spawner to verify!
#c.Spawner.cmd = ['jupyterhub-singleuser']

## Minimum number of cpu-cores a single-user notebook server is guaranteed to
#  have available.
#  
#  If this value is set to 0.5, allows use of 50% of one CPU. If this value is
#  set to 2, allows use of up to 2 CPUs.
#  
#  Note that this needs to be supported by your spawner for it to work.
#c.Spawner.cpu_guarantee = None

## Maximum number of cpu-cores a single-user notebook server is allowed to use.
#  
#  If this value is set to 0.5, allows use of 50% of one CPU. If this value is
#  set to 2, allows use of up to 2 CPUs.
#  
#  The single-user notebook server will never be scheduled by the kernel to use
#  more cpu-cores than this. There is no guarantee that it can access this many
#  cpu-cores.
#  
#  This needs to be supported by your spawner for it to work.
#c.Spawner.cpu_limit = None

## Enable debug-logging of the single-user server
#c.Spawner.debug = False

## The URL the single-user server should start in.
#  
#  `{username}` will be expanded to the user's username
#  
#  Example uses:
#  
#  - You can set `notebook_dir` to `/` and `default_url` to `/tree/home/{username}` to allow people to
#    navigate the whole filesystem from their notebook server, but still start in their home directory.
#  - Start with `/notebooks` instead of `/tree` if `default_url` points to a notebook instead of a directory.
#  - You can set this to `/lab` to have JupyterLab start by default, rather than Jupyter Notebook.
#c.Spawner.default_url = ''

## Disable per-user configuration of single-user servers.
#  
#  When starting the user's single-user server, any config file found in the
#  user's $HOME directory will be ignored.
#  
#  Note: a user could circumvent this if the user modifies their Python
#  environment, such as when they have their own conda environments / virtualenvs
#  / containers.
#c.Spawner.disable_user_config = False

## Whitelist of environment variables for the single-user server to inherit from
#  the JupyterHub process.
#  
#  This whitelist is used to ensure that sensitive information in the JupyterHub
#  process's environment (such as `CONFIGPROXY_AUTH_TOKEN`) is not passed to the
#  single-user server's process.
#c.Spawner.env_keep = ['PATH', 'PYTHONPATH', 'CONDA_ROOT', 'CONDA_DEFAULT_ENV', 'VIRTUAL_ENV', 'LANG', 'LC_ALL']

## Extra environment variables to set for the single-user server's process.
#  
#  Environment variables that end up in the single-user server's process come from 3 sources:
#    - This `environment` configurable
#    - The JupyterHub process' environment variables that are whitelisted in `env_keep`
#    - Variables to establish contact between the single-user notebook and the hub (such as JUPYTERHUB_API_TOKEN)
#  
#  The `enviornment` configurable should be set by JupyterHub administrators to
#  add installation specific environment variables. It is a dict where the key is
#  the name of the environment variable, and the value can be a string or a
#  callable. If it is a callable, it will be called with one parameter (the
#  spawner instance), and should return a string fairly quickly (no blocking
#  operations please!).
#  
#  Note that the spawner class' interface is not guaranteed to be exactly same
#  across upgrades, so if you are using the callable take care to verify it
#  continues to work after upgrades!
#c.Spawner.environment = {}

## Timeout (in seconds) before giving up on a spawned HTTP server
#  
#  Once a server has successfully been spawned, this is the amount of time we
#  wait before assuming that the server is unable to accept connections.
#c.Spawner.http_timeout = 30

## The IP address (or hostname) the single-user server should listen on.
#  
#  The JupyterHub proxy implementation should be able to send packets to this
#  interface.
#c.Spawner.ip = ''

## Minimum number of bytes a single-user notebook server is guaranteed to have
#  available.
#  
#  Allows the following suffixes:
#    - K -> Kilobytes
#    - M -> Megabytes
#    - G -> Gigabytes
#    - T -> Terabytes
#  
#  This needs to be supported by your spawner for it to work.
#c.Spawner.mem_guarantee = None

## Maximum number of bytes a single-user notebook server is allowed to use.
#  
#  Allows the following suffixes:
#    - K -> Kilobytes
#    - M -> Megabytes
#    - G -> Gigabytes
#    - T -> Terabytes
#  
#  If the single user server tries to allocate more memory than this, it will
#  fail. There is no guarantee that the single-user notebook server will be able
#  to allocate this much memory - only that it can not allocate more than this.
#  
#  This needs to be supported by your spawner for it to work.
#c.Spawner.mem_limit = None

## Path to the notebook directory for the single-user server.
#  
#  The user sees a file listing of this directory when the notebook interface is
#  started. The current interface does not easily allow browsing beyond the
#  subdirectories in this directory's tree.
#  
#  `~` will be expanded to the home directory of the user, and {username} will be
#  replaced with the name of the user.
#  
#  Note that this does *not* prevent users from accessing files outside of this
#  path! They can do so with many other means.
#c.Spawner.notebook_dir = ''

## An HTML form for options a user can specify on launching their server.
#  
#  The surrounding `<form>` element and the submit button are already provided.
#  
#  For example:
#  
#  .. code:: html
#  
#      Set your key:
#      <input name="key" val="default_key"></input>
#      <br>
#      Choose a letter:
#      <select name="letter" multiple="true">
#        <option value="A">The letter A</option>
#        <option value="B">The letter B</option>
#      </select>
#  
#  The data from this form submission will be passed on to your spawner in
#  `self.user_options`
#c.Spawner.options_form = ''

## Interval (in seconds) on which to poll the spawner for single-user server's
#  status.
#  
#  At every poll interval, each spawner's `.poll` method is called, which checks
#  if the single-user server is still running. If it isn't running, then
#  JupyterHub modifies its own state accordingly and removes appropriate routes
#  from the configurable proxy.
#c.Spawner.poll_interval = 30

## The port for single-user servers to listen on.
#  
#  Defaults to `0`, which uses a randomly allocated port number each time.
#  
#  If set to a non-zero value, all Spawners will use the same port, which only
#  makes sense if each server is on a different address, e.g. in containers.
#  
#  New in version 0.7.
#c.Spawner.port = 0

## An optional hook function that you can implement to do some bootstrapping work
#  before the spawner starts. For example, create a directory for your user or
#  load initial content.
#  
#  This can be set independent of any concrete spawner implementation.
#  
#  Example::
#  
#      from subprocess import check_call
#      def my_hook(spawner):
#          username = spawner.user.name
#          check_call(['./examples/bootstrap-script/bootstrap.sh', username])
#  
#      c.Spawner.pre_spawn_hook = my_hook
#c.Spawner.pre_spawn_hook = None

## Timeout (in seconds) before giving up on starting of single-user server.
#  
#  This is the timeout for start to return, not the timeout for the server to
#  respond. Callers of spawner.start will assume that startup has failed if it
#  takes longer than this. start should return when the server process is started
#  and its location is known.
#c.Spawner.start_timeout = 60

#------------------------------------------------------------------------------
# LocalProcessSpawner(Spawner) configuration
#------------------------------------------------------------------------------

## A Spawner that uses `subprocess.Popen` to start single-user servers as local
#  processes.
#  
#  Requires local UNIX users matching the authenticated users to exist. Does not
#  work on Windows.
#  
#  This is the default spawner for JupyterHub.

## Seconds to wait for single-user server process to halt after SIGINT.
#  
#  If the process has not exited cleanly after this many seconds, a SIGTERM is
#  sent.
#c.LocalProcessSpawner.interrupt_timeout = 10

## Seconds to wait for process to halt after SIGKILL before giving up.
#  
#  If the process does not exit cleanly after this many seconds of SIGKILL, it
#  becomes a zombie process. The hub process will log a warning and then give up.
#c.LocalProcessSpawner.kill_timeout = 5

## Extra keyword arguments to pass to Popen
#  
#  when spawning single-user servers.
#  
#  For example::
#  
#      popen_kwargs = dict(shell=True)
#c.LocalProcessSpawner.popen_kwargs = {}

## Seconds to wait for single-user server process to halt after SIGTERM.
#  
#  If the process does not exit cleanly after this many seconds of SIGTERM, a
#  SIGKILL is sent.
#c.LocalProcessSpawner.term_timeout = 5

#------------------------------------------------------------------------------
# Authenticator(LoggingConfigurable) configuration
#------------------------------------------------------------------------------

## Base class for implementing an authentication provider for JupyterHub

## Set of users that will have admin rights on this JupyterHub.
#  
#  Admin users have extra privileges:
#   - Use the admin panel to see list of users logged in
#   - Add / remove users in some authenticators
#   - Restart / halt the hub
#   - Start / stop users' single-user servers
#   - Can access each individual users' single-user server (if configured)
#  
#  Admin access should be treated the same way root access is.
#  
#  Defaults to an empty set, in which case no user has admin access.
#c.Authenticator.admin_users = set()

## Automatically begin the login process
#  
#  rather than starting with a "Login with..." link at `/hub/login`
#  
#  To work, `.login_url()` must give a URL other than the default `/hub/login`,
#  such as an oauth handler or another automatic login handler, registered with
#  `.get_handlers()`.
#  
#  .. versionadded:: 0.8
#c.Authenticator.auto_login = False

## Enable persisting auth_state (if available).
#  
#  auth_state will be encrypted and stored in the Hub's database. This can
#  include things like authentication tokens, etc. to be passed to Spawners as
#  environment variables.
#  
#  Encrypting auth_state requires the cryptography package.
#  
#  Additionally, the JUPYTERHUB_CRYPTO_KEY envirionment variable must contain one
#  (or more, separated by ;) 32B encryption keys. These can be either base64 or
#  hex-encoded.
#  
#  If encryption is unavailable, auth_state cannot be persisted.
#  
#  New in JupyterHub 0.8
#c.Authenticator.enable_auth_state = False

## Dictionary mapping authenticator usernames to JupyterHub users.
#  
#  Primarily used to normalize OAuth user names to local users.
#c.Authenticator.username_map = {}

## Regular expression pattern that all valid usernames must match.
#  
#  If a username does not match the pattern specified here, authentication will
#  not be attempted.
#  
#  If not set, allow any username.
#c.Authenticator.username_pattern = ''

## Whitelist of usernames that are allowed to log in.
#  
#  Use this with supported authenticators to restrict which users can log in.
#  This is an additional whitelist that further restricts users, beyond whatever
#  restrictions the authenticator has in place.
#  
#  If empty, does not perform any additional restriction.
#c.Authenticator.whitelist = set()

#------------------------------------------------------------------------------
# LocalAuthenticator(Authenticator) configuration
#------------------------------------------------------------------------------

## Base class for Authenticators that work with local Linux/UNIX users
#  
#  Checks for local users, and can attempt to create them if they exist.

## The command to use for creating users as a list of strings
#  
#  For each element in the list, the string USERNAME will be replaced with the
#  user's username. The username will also be appended as the final argument.
#  
#  For Linux, the default value is:
#  
#      ['adduser', '-q', '--gecos', '""', '--disabled-password']
#  
#  To specify a custom home directory, set this to:
#  
#      ['adduser', '-q', '--gecos', '""', '--home', '/customhome/USERNAME', '--
#  disabled-password']
#  
#  This will run the command:
#  
#      adduser -q --gecos "" --home /customhome/river --disabled-password river
#  
#  when the user 'river' is created.
#c.LocalAuthenticator.add_user_cmd = []

## If set to True, will attempt to create local system users if they do not exist
#  already.
#  
#  Supports Linux and BSD variants only.
#c.LocalAuthenticator.create_system_users = False

## Whitelist all users from this UNIX group.
#  
#  This makes the username whitelist ineffective.
#c.LocalAuthenticator.group_whitelist = set()

#------------------------------------------------------------------------------
# PAMAuthenticator(LocalAuthenticator) configuration
#------------------------------------------------------------------------------

## Authenticate local UNIX users with PAM

## The text encoding to use when communicating with PAM
#c.PAMAuthenticator.encoding = 'utf8'

## Whether to open a new PAM session when spawners are started.
#  
#  This may trigger things like mounting shared filsystems, loading credentials,
#  etc. depending on system configuration, but it does not always work.
#  
#  If any errors are encountered when opening/closing PAM sessions, this is
#  automatically set to False.
#c.PAMAuthenticator.open_sessions = True

## The name of the PAM service to use for authentication
#c.PAMAuthenticator.service = 'login'

#------------------------------------------------------------------------------
# CryptKeeper(SingletonConfigurable) configuration
#------------------------------------------------------------------------------

## Encapsulate encryption configuration
#  
#  Use via the encryption_config singleton below.

## 
#c.CryptKeeper.keys = []

## The number of threads to allocate for encryption
#c.CryptKeeper.n_threads = 4
