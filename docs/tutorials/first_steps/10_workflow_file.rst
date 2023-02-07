.. _workflow_file:

Define your Workflow in a Workflow File
---------------------------------------

The ``renku run`` command is great for tracking the use of a small number of
scripts or commands. However, if you are building a processing pipeline that
involves many steps, we recommend to encode your workflow in a workflow file.


Introducing the Renku Workflow File
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In this example, we will use the same  ``filter_flights`` and ``count_flights``
scripts as in the prior parts of the tutorial, but this time we will encode our
workflow in a workflow definition file, rather than using the command line.

To create a workflow file in your Renku project, create a file called
``workflow.yml``.

We'll start by creating the simplest version of the Renku workflow file:

.. code-block:: yaml

    name: flights-processing-pipeline
    steps:
      filter:
        command: python src/filter_flights.py data/flight-data/2019-01-flights.csv.zip data/output/flights-filtered.csv
        inputs:
          - src/filter_flights.py
          - data/flight-data/2019-01-flights.csv.zip
        outputs:
          - data/output/flights-filtered.csv

This workflow file defines the workflow's name and a sequence of steps. For now,
we've only included the first step of our workflow, which we've named
``filter``. Within the ``filter`` step, we define the command to run, and then
we tell Renku which parts of this command are inputs and outputs by copying
those paths into the relevant sections.

To run this workflow file, run:

.. code-block:: console

        $ renku run workflow.yml


Using Templating in a Workflow File
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Renku provides a templating feature so that you never have to type the same path
twice. In the ``command`` field, we can replace the paths to the inputs with the
``$inputs`` template, and likewise for the ``$outputs``.

.. code-block:: yaml

    name: flights-processing-pipeline
    steps:
      filter:
        command: python $inputs $outputs
        inputs:
          - src/filter_flights.py
          - data/flight-data/2019-01-flights.csv.zip
        outputs:
          - data/output/flights-filtered.csv


A Multi-Step Workflow File
~~~~~~~~~~~~~~~~~~~~~~~~~~

Below, you can see what the full workflow file looks like for the two-step
workflow.

.. code-block:: yaml

    name: flights-processing-pipeline
    steps:
      filter:
        command: python $inputs $outputs
        inputs:
          - src/filter_flights.py
          - data/flight-data/2019-01-flights.csv.zip
        outputs:
          - data/output/flights-filtered.csv
  
      count:
        command: python $inputs $outputs
        inputs:
          - src/count_flights.py
          - data/output/flights-filtered.csv
        outputs:
          - data/output/flights-count.csv


Executing a workflow file
^^^^^^^^^^^^^^^^^^^^^^^^^

Running ``renku run workflow.yml`` will execute all steps in the workflow file.

Renku also helps you run only portions of your workflow at a time. For example,
you can execute just one step of the workflow by referencing that step's name:

.. code-block:: console

        $ renku run workflow.yml filter


You may specify more than one step to run:

.. code-block:: console

        $ renku run workflow.yml filter count


.. If we had a longer workflow, perhaps with 10 or more steps, we could specify a
.. subset of steps to run.

.. .. code-block:: console

..         # runs the step 'filter' and every step after it.
..         $ renku run workflow.yml filter:

..         # runs every step before 'count', and the 'count' step
..         $ renku run workflow.yml :count

..         # runs every step between 'filter' and 'count', including 'filter' and 'count' themselves
..         $ renku run workflow.yml filter:count


Want to learn more?
^^^^^^^^^^^^^^^^^^^

For much more information about writing and executing workflow files, see
:ref:`workflow-definition-file-topic-guide`.
