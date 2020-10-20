.. _renku:

Renku Client
============

The core of the Renku Project is the ``renku`` command-line client,
which offers tools for easily capturing your data-science process as you work.
With these tools, you can describe and annotate data and workflows, providing
information that is used to build the provenance of your results, simplifying
iterative development and making your work reproducible. The client can be used
within Renkulab or locally, on your own machine.

The importance of version control for working with code is widely recognized.
``renku`` aims to be "git for research", by extending version control to encompass
the complete research and data exploration life-cycle. It lets you manage
versioned datasets, automatically create workflows and keep track of
computational environments. Check out :ref:`first_steps` tutorial for a complete
example.

Datasets
--------

The ``renku`` client can  be used to create "datasets", which are just
collections of data files bundled together and enriched with some metadata.

It is easy to create datasets

.. code-block:: shell

  renku dataset create mydataset

add files

.. code-block:: shell

  renku dataset add mydataset datafile

and even import existing datasets from public repositories like `Zenodo
<https://zenodo.org/>`_ and `Dataverse <https://dataverse.harvard.edu/>`_:

.. code-block:: shell

  renku dataset import https://zenodo.org/record/3981451

The full metadata from the data repository is preserved and mirrored in the
knowledge graph for easy retrieval and search.


Lineage of results
------------------

Capturing the :ref:`provenance of results <provenance>` is critical for understanding
what input data were used, what code was run, and what results were produced

The ``renku`` client gives researchers and analysts simple tools to
automatically track provenance and iteratively develop a workflow.

Creating a workflow is done by invoking ``renku run`` in front

.. code-block:: shell

  renku run echo "hello-world!" > hello
  renku run wc hello > hello.wc


Installing
----------

You can follow these `installation instructions`_ for running renku locally if you wish to
forgo using renkulab or need to interact with your project locally.

.. _`installation instructions`: https://renku-python.readthedocs.io/en/latest/#installation

.. _`CLI documentation`: https://renku-python.readthedocs.io
