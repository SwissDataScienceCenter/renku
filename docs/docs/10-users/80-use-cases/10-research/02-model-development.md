---
title: Model & Algorithm Development
---

# Reproducible environments without the Docker detour

The "works on my machine" problem is a structural tax on scientific collaboration. Traditional solutions are either fragile (a long `README.md` listing exact package versions) or steep (learning Docker well enough to write and maintain a `Dockerfile` for a non-trivial scientific stack). Neither is a reasonable expectation for a researcher whose primary expertise is hydrology, structural biology or econometrics rather than software engineering.

Renku takes a different position: the environment specification you already write in a `requirements.txt`, `environment.yml`, `renv.lock` or similar is enough. Renku reads it, builds the container automatically, and every subsequent session runs inside that container. No Dockerfile. No `conda env create`. No "did you install the right CUDA version?" emails.
