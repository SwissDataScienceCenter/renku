# Configure coding agents for a session environment

You can install coding agents when you [create an environment from
code](/docs/users/compute/environment/guides/create-environment-with-custom-packages-installed)
for a session launcher or a job launcher. **Coding agents** is optional.

## Choose which agents to install

- Leave **Coding agents** empty to use the repository’s `project.toml`. Renku installs the
  agents that file requests. The launcher panel shows **Coding agents** as `project.toml`.
- Select one or more of Claude Code, Pi, and Codex to install only those agents. The
  selection replaces `project.toml`.

After you change the selection, or the agent configuration in the repository, rebuild the
environment. Click the launcher to open the launcher side panel, go to **Environment**, and
click **Rebuild**. See [Updating a code-based
environment](/docs/users/compute/environment/guides/create-environment-with-custom-packages-installed#updating-a-code-based-environment).

Renku includes each agent’s configuration from the code repository in the environment. You
can run the plugin commands locally and commit the result, or run them inside the Renku
session.

## Limit what agents can access

A coding agent runs with the same permissions as you in the session. It can read the
**secrets** and **data connectors** available to the launcher, change or delete data in writable
data connectors, or send what it reads to its model provider.

Before you use an agent, restrict the launcher to what the task needs:

1. Click the launcher to open the launcher right-side panel.
2. In the **Secrets** section, click on the top button and restrict access so that only the secrets this launcher needs are
   available. For example, keep the agent’s own key (e.g. `ANTHROPIC_API_KEY`) if you use one.
3. In the **Data connectors** section, click on the top button and restrict access so that only the data connectors this
   launcher needs are mounted.
4. Restart any running session from this launcher so that the change applies.

:::tip

Use **two launchers** on the same project: one with coding agents and only the access
it needs, and one without agents for work with sensitive secrets or data.

:::

:::warning

Restricting a launcher limits what it can reach, but it is not a sandbox. The agent can still
read and change the files in the session and the repository, and use the network. Do not use
coding agents on data classified as confidential, unless the model provider is
approved for it.

:::

## Claude Code

Claude Code (`claude`): add `ANTHROPIC_API_KEY`, or sign in after launch. Commit a
`.claude/` folder, and put skills in `.claude/skills/`.

To install a project-scoped plugin:

```bash
claude plugin install superpowers@claude-plugins-official --scope project
```

## Pi

Pi (`pi`): bring your own model provider. Commit a `.pi/` folder, and put skills in
`.pi/skills`.

To install a local plugin:

```bash
pi install npm:pi-sdsc-vllm --local
```

## Codex

Codex (`codex`): add `OPENAI_API_KEY`, or sign in after launch. Enable Device flow in
[ChatGPT security settings](https://chatgpt.com/#settings/Security).

Commit a `.codex/` directory, and put skills in `.codex/skills`. Install Codex plugins at
user level. They are unreliable before you log in.
