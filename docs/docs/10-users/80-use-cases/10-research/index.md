---
title: Renku for Research & Data Science
---

# Renku for Research & Data Science

Whether you analyse field observations, build interactive dashboards, run large-scale simulations, or iterate on your model architecture, you usually spend too much valuable time on setting the infrastructure instead of on the work itself.

Renku addresses the friction that cuts across all of these situations: data that takes hours to transfer before you can touch it, environments that work on one machine and nowhere else, long-running experiments that die when a laptop lid closes, and results that are difficult to share or reproduce later. Renku connects your data, your code and your environments into a single project, and keeps all three in sync for every collaborator and across every session.

# Who this is for

Renku's research and data science features are built for anyone who:

- **Connects to large or remote datasets**: cloud storage (S3, Azure Blob), institutional mounts (SwitchDrive, SFTP), or archived datasets from Zenodo, Dataverse, or EnviDat.
- **Works across languages, such as Python, R or Julia** and needs a reproducible environment built automatically from a `requirements.txt`, `environment.yml`, or `renv.lock`, with no Dockerfile required.
- **Runs experiments or training jobs** that exceed what a laptop can handle or that need to run overnight, and be resumed from any machine.
- **Collaborates in a team or lab** sharing analysis workflows or interactive dashboards with colleagues, reviewers or the public without sending files and setup instructions via email.
- **Needs to cite or reproduce results** referencing an exact dataset version by DOI and an exact code state by git commit, for a paper submission or a technical report.

If you work with data and need your analysis to be reproducible and shareable, Renku is designed for you.

# What Renku gives you

| Capability                             | What it means in practice                                                                                                                                           |
| -------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Data connectors**                    | Mount S3, Azure Blob, SwitchDrive, SFTP, or DOI-based files directly into your session. No download, no hardcoded paths.                                            |
| **Automatic environments**             | Create a session launcher from your GitHub or GitLab repository. Renku builds the image from your dependency file and every collaborator gets the same environment. |
| **Persistent sessions**                | Sessions run independently of your browser. Close your laptop, and come back tomorrow. Reconnect from any machine.                                                  |
| **Access to supercompute**             | Select a GPU resource class, including CSCS Alps, from the same dropdown as a standard session. No job scripts, no queue systems.                                   |
| **Shareable launch links**             | Share a URL that opens your exact environment in a browser.                                                                                                         |
| **DOI-based inputs and Zenodo export** | Reference the exact dataset your analysis used, and export your results to Zenodo for a citable DOI, all from the same project page.                                |

# The research and data science lifecycle

The guides below follow the natural lifecycle of data intensive projects, from first contact with a new dataset to a published, reproducible result. Each stage is independent, so feel free to jump to the one that matches your current challenge.

```mdx-code-block
import DocCardList from '@theme/DocCardList';

<DocCardList />
```
