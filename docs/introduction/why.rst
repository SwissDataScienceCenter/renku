.. _why_renku:

Why Renku?
==========

If you carry out research or analysis with code and data, chances are you will
be faced with answering the following questions:

* How do I repeat an analysis I did 1 month ago?
* How does new data change my results?
* How can I easily share my analysis with others?
* How is this type of data or algorithm usually used?
* Who is using my data or my algorithms?

The tool-chain required to answer all of these questions comfortably is
long and complex. The **Renku Project** aims to provide researchers with easy
access to these state-of-the-art tools to improve reproducibility,
increase productivity through reusability and enable collaboration.


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


Head to :ref:`What is Renku <what_is_renku_verbose>` to learn more about what
makes up the **Renku Project**.
