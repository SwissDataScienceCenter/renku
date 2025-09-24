# Renku Users Documentation

Renku is an open-source platform that connects the ecosystem of data, code, and compute to empower researchers to build collaborative communities.

Our mission is to enable the Swiss National Open Research Data (ORD) Strategy, supporting the accessibility and reuse of research data across Switzerland.

## Connecting data, code, compute, and *people*.

Stop juggling multiple platforms and scattered resources. Renku provides a seamless environment
where all your project components work together.

## Renku empowers collaboration by reducing friction

### Findability

**On Renku, your project doesn’t exist in isolation.** The connections to other research activities are preserved, powering discovery and understanding.

### Accessibility

**Collaborate on your Renku project with anyone,** across specialties and skill levels.

### Interoperability

**Connect, Share, Launch!** It just works.

### Reusability

**Publishing your project is as easy as flipping a toggle from private to public.** Offering your work for discovery and reuse is automatic, not added work.

# How Renku works

Renku is a platform for collaborative data science developed at the [Swiss Data Science Center](https://www.datascience.ch). It enables researchers to build collaborative projects that bring together [data](https://www.notion.so/Data-connector-3ae1e46fdb094cc48516a104457e5633?pvs=21), [code](https://www.notion.so/Code-repository-2bbc38797efe4ed1b5f6be16fd95b82e?pvs=21), and [compute resources](https://www.notion.so/Session-Launcher-518df05050a7434eb3eb0493181d715c?pvs=21) from a variety of sources. 

Rather than hosting all these resources itself, Renku is a connecting hub that provides the means to bring them all together. For example, a project might use a published dataset from [Zenodo](https://www.notion.so/How-to-connect-data-from-data-repositories-such-as-Zenodo-or-Dataverse-1eb0df2efafc802ab3bef1c47c8c45b4?pvs=21), raw data from a shared [cloud storage resource](https://www.notion.so/Data-connector-3ae1e46fdb094cc48516a104457e5633?pvs=21) and code from a [GitHub repository](https://www.notion.so/How-to-add-a-code-repository-to-your-project-53658e1ef33d431bb3c3129a82d99a5f?pvs=21). These are then made seamlessly available in containerized browser-based compute [sessions](https://www.notion.so/Session-fd7c8246082145df8bcad675cf919206?pvs=21). Renku sessions can run on Renku servers or on an external computational resources such as a local cloud provider or - coming soon! - an HPC cluster. The sessions can be used for development with [environments](https://www.notion.so/How-to-add-a-session-launcher-to-your-project-601ba47455354413b87c69447aa33831?pvs=21) like VSCode, Jupyter, or RStudio, or they can be used to display dashboards with Streamlit or Shiny. To promote sustainable computational research, Renku also makes it straightforward to build Docker images based on users' [software requirements](https://www.notion.so/How-to-create-a-custom-environment-from-a-code-repository-1960df2efafc801b88f6da59a0aa8234?pvs=21), ensuring that everyone using a project does so with the right software stack.

Collaborate on a fully reproducible Renku project by adding project [members](https://www.notion.so/How-to-add-people-to-a-project-9026ada14bd7446cbd6a0a638a7eb5d1?pvs=21), and showcase interactive demos and apps with anyone via [public session launch links](https://www.notion.so/How-to-share-a-session-launch-link-25e0df2efafc801d8afac861c3160532?pvs=21). For larger collaborations, users can organize projects and other assets under [groups](https://www.notion.so/Collaborate-on-Renku-as-a-group-1460df2efafc806faffee5d715e625f4?pvs=21), which helps streamline collaboration.

Renku is developed under the Apache 2.0 license and can be deployed on a variety of cloud infrastructures offering Kubernetes. The flagship instance operated by the Swiss Data Science Center is available under [https://renkulab.io](https://renkulab.io/) and is open to everyone with a publicly-accessible free resource tier.

:::tip

Want to try it out? [Get started with Renku](https://www.notion.so/Get-started-with-Renku-1a50df2efafc800f8554e30fd7458fa6?pvs=21)! 

:::
