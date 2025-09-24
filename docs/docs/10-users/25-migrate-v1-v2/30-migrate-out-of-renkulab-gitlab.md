# How to migrate out of RenkuLab GitLab

<aside>
<img src="https://www.notion.so/icons/new-alert_red.svg" alt="https://www.notion.so/icons/new-alert_red.svg" width="40px" />

**DATA REMOVAL DEADLINE - JANUARY 2026**

The RenkuLab GitLab (gitlab.renkulab.io) will soon be shut down. All code repositories and hosted images remaining there will be removed. Follow the migration instructions below before January 2026 to save your work.

</aside>

You have probably landed on this page because you have a code repository or session launcher that needs to be migrated out of the RenkuLab GitLab before GitLab is shut down in January 2026. **If you don’t migrate before the shut down, you will lose access to your code repository and/or docker image.**

To determine what resources in your project need to be migrated, look for the **Migration needed** badge on code repositories and session launchers in your project:

![2_migration pill_2x.png](./migrate-out-of-renkulab-gitlab-10.png)

Then, follow the instructions below to migrate each resource.

<aside>
<img src="https://www.notion.so/icons/code_red.svg" alt="https://www.notion.so/icons/code_red.svg" width="40px" />

I need to migrate a **code repository**

→ [Instructions](/docs/users/code/guides/migrate-code-repository-out-of-renkulab-gitlab)

</aside>

<aside>
<img src="https://www.notion.so/icons/package_red.svg" alt="https://www.notion.so/icons/package_red.svg" width="40px" />

I need to migrate a **session launcher (docker image)**

→ [Instructions](/docs/users/projects/guides/migrate-session-launcher-that-references-renku-gitlab)

</aside>

To learn more about the Renku Legacy and GitLab shut downs, see our blog.

[Sunsetting Renku Legacy: Your Guide to a Smooth Transition | Renku Blog](https://blog.renkulab.io/sunsetting-legacy/)
