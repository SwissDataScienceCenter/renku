# Renku for Events

Renku is a great choice **for computing events** like **hackathons** and **workshops**. Take full advantage of Renku’s **collaboration features and integration with other platforms.** Focus on innovation, learning and skilling up, not on setup and infrastructure. Provide a consistent environment for all teams, and get participants coding and collaborating right away. 

# Register your event

In order to assure that sufficient resources are available for your event, please register it in advance.
→ [Register your event](https://renku.notion.site/ac0228ddcc1e4391840d961d1c10a4dc?pvs=105)

# Onboarding event participants in Renku

In order to manage rights of the participants in your setup for the event in Renku, they need to be registered users. Therefore, ask them to **create an account well in advance before the event**, as described in [How to create your Renku account](/docs/10-users/20-getting-started/10-tutorial-start.md). After that, they should share with you the identifier of their Renku account that can be found with an @ at the top left hand side of the dashboard after they log in.

# Managing access rights to event project

We recommend you to create a group for your event following the instructions in [How to create a group](/docs/10-users/70-collaboration/guides/20-create-group.md).

We recommend to add **other event admins as owners** of the group so that they can edit all the projects in the group.

Add the **participants as** **viewers** of the  group. As a result, they will be able to see the projects and data connectors in the group, but they will not be able to edit them.

# Setting up an event with Renku projects

Depending on the requirements of the event, we propose two modes for setting up the event projects:

1. **Ready-to-go Mode**: Ideal for events where participants cannot share the information across working groups or have little background on digital skills.
    
    → 
    
2. **Template Mode:** Participants create their own copies of an event template project. Sharing visibility across participants projects is part of the dynamics and participants are comfortable setting up some basic resources on their projects. 
    
    →
    
flowchart TD
    A[Do participants need to have the projects completely set-up when the event happens?]
    A -->|Yes| B[<b>End-to-end Mode:</b><br>Participants have their own group with the required connectors, e.g. data, code.]
    A -->|No| C[<b>Template Mode:</b><br>Participants can copy the event template project and add their own connectors.]
  

### Quick-Reference Guides

### Working in your local IDE

Do you prefer to code locally in your Renku project, then read the following guide to connect your IDE to your Renku session: [How to connect to a Renku session from your local VSCode](/docs/10-users/60-sessions/guides/50-connect-to-renku-session-from-your-local-vscode.md) 


### Work collaboratively using git

Manage the access to your team participants at the source code repository (each participant needs to have an account in the code repository to be able to commit and push their changes). 

### Integrate your GitHub/GitLab repository for code version control in your session

Ensure that these team members have integrated their git account inside Renku. See [How to connect your Renku account to your GitHub or GitLab account](/docs/10-users/50-code/guides/20-connect-renku-account-to-github-or-gitlab-account.md)

---
