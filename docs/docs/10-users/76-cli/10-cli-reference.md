# Renku CLI (`rnk`)

The Renku CLI (`rnk`) is a command-line tool for managing Renku projects, datasets, jobs, and sessions from the terminal written in Rust.

:::info

`rnk` is a separate project from the Renku platform itself. For the source code, issues, and latest releases, see [SwissDataScienceCenter/renku-cli](https://github.com/SwissDataScienceCenter/renku-cli).

:::

## Installation

### Quick install (Linux / macOS)

```bash
curl -sfSL https://raw.githubusercontent.com/SwissDataScienceCenter/renku-cli/main/install.sh | bash
```

This downloads the latest binary and installs it.

### Manual download

Download the appropriate binary from the [releases page](https://github.com/SwissDataScienceCenter/renku-cli/releases/latest):

- **Linux (amd64):** `*-amd64`
- **Linux (aarch64):** `*-aarch64`
- **macOS:** `*-darwin`
- **Windows:** `*-windows`

No installation step is needed — just make the binary executable and move it to your `$PATH`.

### Shell completions

The CLI can generate completions for bash, fish, zsh, and PowerShell. Add the following to your shell configuration:

```bash
# Bash
echo "source <(COMPLETE=bash rnk)" >> ~/.bashrc

# Fish
echo "COMPLETE=fish rnk | source" >> ~/.config/fish/config.fish

# Zsh
echo "source <(COMPLETE=zsh rnk)" >> ~/.zshrc

# PowerShell
echo '$env:COMPLETE = "powershell"; rnk | Out-String | Invoke-Expression; Remove-Item Env:\COMPLETE' >> $PROFILE
```

## Configuration

Most of the CLI commands are scoped to a project so as not to return too many results.

This project context is resolved in this precedence order (in descending precedence):

1. `--project-context` CLI flag
2. `RENKU_CLI_PROJECT_CONTEXT` environment variable
3. `.renku/config.toml` in the current directory (written by `rnk clone`)
4. Global active project set with `rnk project activate`

### Global flags

- `--format json` — structured JSON output instead of human-readable
- `--proxy <url>` / `--proxy none` — control proxy usage
- `-v` / `-q` — increase / decrease log verbosity

## Commands

### Authentication

| Command      | Description                                      |
| ------------ | ------------------------------------------------ |
| `rnk login`  | Interactive login to Renku (browser-based OAuth) |
| `rnk logout` | Remove stored credentials                        |

### Projects

| Command                              | Description                                                               |
| ------------------------------------ | ------------------------------------------------------------------------- |
| `rnk clone <project-ref>`            | Clone a project by ID, namespace/slug, or URL                             |
| `rnk project clone <project-ref>`    | Same as above (full command form)                                         |
| `rnk project activate <project-ref>` | Set the active project for the current user (`rnk p a`)                   |
| `rnk project deactivate`             | Unset the active project (`rnk p d`)                                      |
| `rnk project current`                | Show the currently active project (`rnk p c`)                             |
| `rnk project list`                   | List projects (`--all` / `-a` to show all, `--n-results` / `-n` to limit) |

### Datasets

| Command               | Description                                |
| --------------------- | ------------------------------------------ |
| `rnk dataset deposit` | Manage dataset deposits (aliases: `rnk d`) |

### Jobs

| Command         | Description                 |
| --------------- | --------------------------- |
| `rnk job list`  | List jobs (`rnk j ls`)      |
| `rnk job start` | Start a non-interactive job |
| `rnk job stop`  | Stop a running job          |
| `rnk job logs`  | View job logs               |

### Sessions

| Command                             | Description                                         |
| ----------------------------------- | --------------------------------------------------- |
| `rnk session start --launcher <id>` | Start an interactive session using a launcher       |
| `rnk session stop <session-id>`     | Stop a running session                              |
| `rnk session list`                  | List currently running sessions                     |
| `rnk session logs <session-id>`     | View session logs (`--follow` / `-f` for live tail) |

### Launchers

| Command             | Description                                                   |
| ------------------- | ------------------------------------------------------------- |
| `rnk launcher list` | List currently running launchers (`--mode` to filter by type) |

### Other

| Command       | Description                         |
| ------------- | ----------------------------------- |
| `rnk version` | Show client and server version info |
| `rnk update`  | Check for and install updates       |

## Quick start

```bash
# 1. Authenticate to your Renku instance
rnk login

# 2. Clone a project
rnk clone <project-ref>

# 3. List running jobs
rnk job list
```

## Links

- [Source Code](https://github.com/SwissDataScienceCenter/renku-cli)
- [Issue Tracker](https://github.com/SwissDataScienceCenter/renku-cli/issues)
- [Installation Guide](https://github.com/SwissDataScienceCenter/renku-cli/blob/main/docs/install.md)
