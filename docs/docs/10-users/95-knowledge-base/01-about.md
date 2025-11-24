# How Renku works

Renku is a platform for collaborative data science developed at the [Swiss Data Science Center](https://www.datascience.ch). It enables researchers to build collaborative projects that bring together [data](/docs/users/data/data), [code](/docs/users/code/code-repository), and [compute resources](/docs/users/sessions/session-launcher) from a variety of sources. 

<p class="">
![image.jpg](./first_page_1.jpg)
</p>

Rather than hosting all these resources itself, Renku is a connecting hub that provides the means to bring them all together. For example, a project might use a published dataset from [Zenodo](https://www.notion.so/How-to-connect-data-from-data-repositories-such-as-Zenodo-or-Dataverse-1eb0df2efafc802ab3bef1c47c8c45b4?pvs=21), raw data from a shared [cloud storage resource](/docs/users/data/data) and code from a [GitHub repository](/docs/users/code/guides/add-code-repository-to-project). These are then made seamlessly available in containerized browser-based compute [sessions](/docs/users/sessions/session). Renku sessions can run on Renku servers or on an external computational resources such as a local cloud provider or - coming soon! - an HPC cluster. The sessions can be used for development with [environments](/docs/users/sessions/guides/add-session-launcher-to-project) like VSCode, Jupyter, or RStudio, or they can be used to display dashboards with Streamlit or Shiny. To promote sustainable computational research, Renku also makes it straightforward to build Docker images based on users' [software requirements](/docs/users/sessions/guides/create-environment-with-custom-packages-installed), ensuring that everyone using a project does so with the right software stack.

<p class="image-container-l">
![image.png](./renku-connected.png)
</p>

Collaborate on a fully reproducible Renku project by adding project [members](/docs/users/collaboration/guides/add-people-to-project), and showcase interactive demos and apps with anyone via [public session launch links](/docs/users/sessions/guides/share-session-launch-link). For larger collaborations, users can organize projects and other assets under [groups](/docs/users/use-cases/groups), which helps streamline collaboration.

Renku is developed under the Apache 2.0 license and can be deployed on a variety of cloud infrastructures offering Kubernetes. The flagship instance operated by the Swiss Data Science Center is available under [https://renkulab.io](https://renkulab.io/) and is open to everyone with a publicly-accessible free resource tier.

## License
Renku is developed under the Apache 2.0 license and can be deployed on a variety of cloud infrastructures offering Kubernetes. The flagship instance operated by the Swiss Data Science Center is available under [https://renkulab.io](https://renkulab.io/) and is open to everyone with a publicly-accessible free resource tier.