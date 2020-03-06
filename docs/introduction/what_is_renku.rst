.. _what_is_renku_verbose:

What is Renku?
==============

The Renku Project is a web platform (:ref:`renkulab`) and a command-line
interface (:ref:`renku`) built on top of open source components for researchers,
data scientists, educators, and students to help manage:

* code,
* data,
* execution environments, and
* workflows

Renku combines many widely-used open-source tools in order to make any project
on the platform reproducible, repeatable, reusable and shareable from the start.
Version control for data and code, containerization for runtime environments and
automatic workflow capture are the core pillars on which the platform is built.


Reproducibility
---------------

Renku facilitates reproducibility by keeping a record of the analysis project
from raw data to final result. The sources of all the important elements can be
identified even if they span multiple projects or might originate in external
public repositories. This cross-linking of research artifacts is made possible
through the Renku :ref:`Knowledge Graph <knowledge-graph>`. The analysis steps can be
re-executed to ensure the veracity of the results or reused in other projects on
different data or rerun with different parameters.

The final results can be packaged into a dataset and easily published with all
the requisite metadata on public repositories like `Zenodo
<https://zenodo.org>`_ or `Dataverse <https://dataverse.org>`_. However, the
archived data is only one (static) part of the story - the analysis project on
the Renku platform holds the invaluable information about the entire processing
chain that led to those published results, and thanks to the versioned runtime
environment can be reproduced and verified by anyone.


Reusability
-----------

Once the data is packaged into a dataset in Renku, its use and application can
be discovered through the Knowledge Graph search. The collaborators, colleagues
or the interested public can then easily reuse this data with the full information
of how it needs to be processed and applied. Similarly, the workflows applied to the
data can be reused and rerun on the same data with different input parameters to
scrutinize the robustness of the conclusions.


Collaboration
-------------

Data-driven discovery does not happen in a vacuum - Renku allows researchers and
analysts to easily share computational environments for rapid prototyping to
more quickly move ideas forward. Projects can be discussed through interactive
notebooks and templates with complex runtimes can be reused to quickly bootstrap
new experiments.

To lean more about the hosted part of the platform, read about :ref:`Renkulab
<renkulab>`. To explore the possibilities of the lower-level tools, head to the
:ref:`renku` documentation.
