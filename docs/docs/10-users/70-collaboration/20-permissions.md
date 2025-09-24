# Permissions, Roles, and Access Rights

This reference guide covers how permissions, roles, and access rights work in Renku 2.0.

# Project Permissions

---

Access to a Renku project is determined by 2 things:

1. The project’s **visibility** (private or public).
2. The project’s **membership**. This can be determined by being a direct member of the project, or inheriting membership through the project’s namespace (group).

## Visibility

Projects on RenkuLab can be public or private:

- **Private:** The project is accessible only to **members** of the project. The level of access (whether you can modify the project or only view it) is determined by one’s **role** on the project.
- **Public:** The project is visible to everyone on the internet. This means everyone can see the project page and launch sessions. However, only project members can modify the project.

## Project Ownership (*namespace*)

A project may be owned by an individual user or by a group.

If the project is owned by a group, all members of the group inherit membership to the project.

## Project Membership

There are 2 types of project membership:

- **Direct membership:** A person is added directly to the project as a member.
- **Inherited membership**: A person has access to the project by being a member of the project’s namespace (group). For more details, see [Role Inheritance](https://www.notion.so/Role-Inheritance-b70b2a48d74043ca9b78a5ed1f60ae10?pvs=21).

## Project Roles

Members of projects can have one of the following roles:

- **Owner:** Can do everything on the project. This role is automatically assigned to whoever created the project.
- **Editor:** Can edit most things on the project, but cannot change who can access the project (cannot add members or change the project visibility).
- **Viewer:** Can see the project and launch sessions.  ****

For more details, see [Project Abilities by Roles](Permissions,%20Roles,%20and%20Access%20Rights%2013b0df2efafc803e9b57c30df1be6c0a.md).

There may be multiple people in each of these roles (for example, a project may have more than one owner).

## Project Abilities by Roles

| Action | Owner | Editor | Viewer |
| --- | --- | --- | --- |
| View the project page | ✅ | ✅ | ✅ |
| Launch sessions | ✅ | ✅ | ✅ |
| See other project members and their roles | ✅ | ✅ | ✅ |
| Can see the project in search | ✅ | ✅ | ✅ |
| Add code repositories | ✅ | ✅ | ❌ |
| Create data connector in the project | ✅ | ✅ | ❌ |
| Link data connectors to the project | ✅ | ✅ | ❌ |
| Create session launchers | ✅ | ✅ | ❌ |
| Modify the configuration of project components (code repos, session launchers) (See Note below) | ✅ | ✅ | ❌ |
| Edit project metadata (description, keywords, etc) | ✅ | ✅ | ❌ |
| Add and remove project members | ✅ | ❌ | ❌ |
| Change the roles of project members | ✅ | ❌ | ❌ |
| Change project visibility | ✅ | ❌ | ❌ |
| Change the namespace the project is in | ✅ | ❌ | ❌ |
| Delete the project | ✅ | ❌ | ❌ |

# Group Permissions

---

## Group Roles

Members on groups can have one of the following roles:

- **Owner:** Can do everything on the group, including edit all the content (projects and data connectors) in the group. This role is automatically assigned to whoever created the group.
- **Editor:** Can edit all content in the group and add content (projects and data connectors) to the group, but cannot modify group membership.
- **Viewer:** Can view all content in the group (projects and data connectors)

For more details, see [Group Abilities by Role](Permissions,%20Roles,%20and%20Access%20Rights%2013b0df2efafc803e9b57c30df1be6c0a.md) .

## Group Abilities by Role

| Action | Owner | Editor | Viewer |
| --- | --- | --- | --- |
| Inherited role on content inside the group (see also: [Role Inheritance](https://www.notion.so/Role-Inheritance-b70b2a48d74043ca9b78a5ed1f60ae10?pvs=21)) | Owner | Viewer | Viewer |
| View content in the group (projects and data connectors) | ✅ | ✅ | ✅ |
| Create new content in the group (projects and data connectors) | ✅ | ✅ | ❌ |
| Edit content in the group (projects and data connectors) | ✅ | ✅ | ❌ |
| Move projects into the group | ✅ (The individual must also be an owner of the project being moved into the group) | ✅ (The individual must also be an owner of the project being moved into the group) | ❌ |
| Move projects out of group | ✅ | ✅ (The individual must also be an owner of the project being moved out of the group) | ✅ (Project owners may move their project to a different namespace, regardless of their role in the original namespace) |
| Remove a member from the group | ✅ | ❌ | ❌ |
| Change roles of group members | ✅ | ❌ | ❌ |
| Edit the namespace (name, slug) | ✅ | ❌ | ❌ |
| Add a member to the group | ✅ | ❌ | ❌ |

# Data Connector Permissions

---

Access to a Renku data connector is determined by 2 things:

1. The data connector’s **visibility** (private or public).
2. The data connector’s **membership**. This is (mostly) determined by the data connector’s namespace (project, group, or user).

<aside>
<img src="https://www.notion.so/icons/info-alternate_blue.svg" alt="https://www.notion.so/icons/info-alternate_blue.svg" width="40px" /> **Note:** **Access to a data connector is not equivalent to access to the data!**
Being able to edit a data connector is not the same as being able to access the data itself! The access to data is managed externally by the system where the data lives. See also: [Data Connectors & Credentials](Permissions,%20Roles,%20and%20Access%20Rights%2013b0df2efafc803e9b57c30df1be6c0a.md).

</aside>

## Visibility

Data connectors on RenkuLab can be public or private:

- **Private:** The data connector is accessible only to **members** of the data connector. The level of access (whether you can modify the project or only view it) is determined by one’s **role** on the data connector.
- **Public:** The data connector is visible to everyone on the internet. This means everyone can see the data connector listing, and can use the data connector in sessions on projects where it is linked. However, only data connector members can modify the data connector.

## Data Connector Ownership (*namespace*)

A data connector may be owned by a **project**, a **group**, or an individual **user**.

If a data connector is owned by a project, all members of the project inherit membership to the data connector. In other words, the project members have the same role on the data connector as they do to in project.

If the data connector is owned by a group, all members of the group inherit membership to the project.

If the data connector is owned by an individual user, only that user can edit the data connector, and if it is private, only that user can see and use that data connector.

## Data Connector Membership

There are 2 types of data connector membership:

- **Inherited membership**: A person has access to the data connector by being a member of the data connector’s namespace (project, group, or user). For more details, see [Role Inheritance](https://www.notion.so/Role-Inheritance-b70b2a48d74043ca9b78a5ed1f60ae10?pvs=21).
- *(Direct membership of data connectors does not exist at this time.)*

## Data Connector Roles

Members of data connectors can have one of the following roles:

- **Owner:** Can do everything on the data connector. This role is automatically assigned to whoever created the data connector.
- **Editor:** Can edit the configuration of the data connector.
- **Viewer:** Can see the data connector, and use it in projects where it is already linked.

For more details, see [Data Connector Abilities by Roles](Permissions,%20Roles,%20and%20Access%20Rights%2013b0df2efafc803e9b57c30df1be6c0a.md) .

There may be multiple people in each of these roles (for example, a data connector may have more than one owner).

## Data Connector Abilities by Roles

| Action | Owner | Editor | Viewer |
| --- | --- | --- | --- |
| Use the data connector (i.e. the data connector will be mounted in a launched session)
*Note: any required credentials must be provided by the user launching the session | ✅ | ✅ | ✅ |
| Link the data connector to another project | ✅ | ✅ | ✅ |
| See the data connector in search | ✅ | ✅ | ✅ |
| Edit the data connector configuration (see [**Note:** **Access to a data connector is not equivalent to access to the data!**
Being able to edit a data connector is not the same as being able to access the data itself! The access to data is managed externally by the system where the data lives. See also: [Data Connectors & Credentials](Permissions,%20Roles,%20and%20Access%20Rights%2013b0df2efafc803e9b57c30df1be6c0a.md). ](Permissions,%20Roles,%20and%20Access%20Rights%2013b0df2efafc803e9b57c30df1be6c0a.md)  | ✅ | ✅ | ❌ |
| Change the data connector visibility | ✅ | ❌ | ❌ |
| Delete the data connector (which removes the data connector from all linked projects) | ✅ | ❌ | ❌ |

## Data Connectors & Credentials

If a data connector requires credentials in order to access it, each user who launches a session with the data connector will be prompted to enter their credentials for that data.

Credentials are not shared across Renku users.

A user may choose to save their credentials for a data connector in RenkuLab so that they do not have to re-enter them at every session launch. But these saved credentials are not shared with any other Renku user.

<aside>
<img src="https://www.notion.so/icons/info-alternate_blue.svg" alt="https://www.notion.so/icons/info-alternate_blue.svg" width="40px" />

Note: This documentation is valid starting from RenkuLab version `0.67.0`.

</aside>