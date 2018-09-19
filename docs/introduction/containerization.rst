.. _containerization:

Environments and Containerization
=================================

Knowing how we converted data into actionable results by recording lineage and
keeping track of versioning gets us most of the way to being able to fully
reproducing an analysis workflow. A final piece is encapsulating the actual
computational environment. In a quantitative sense, using different releases
of the same library can simple lead to different results. A more practical
aspect, however, is that replicating a computational environment for
reproducibility's sake is often simply very time consuming.

"Containerization" can help with both of these problems. A "container" is in
essence a process running in a fully specified environment, including the
operating system and all dependencies that are needed for a code to run. The
most popular (but certainly not the only) containerization framework is
`Docker <https://www.docker.com/>`_ and we  make use of it extensively for all
parts of Renku. In terms of user workflows, we try to do as much of the
boilerplate for you as possible so for the simpler tasks you don't really
need to worry about the fact that your code is executing in Docker containers.
