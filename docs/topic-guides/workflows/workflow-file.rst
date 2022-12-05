.. _workflow-definition-file-topic-guide:

The Renku Workflow File
-----------------------

If your data processing workflow has many steps, you might find it convenient to
create a workflow definition file. A workflow file help organize your data
processing flow, making it easier to execute portions of the pipeline at a time.
A workflow file also makes your code easier for your collaborators to
understand!

To create a workflow definition file in your project, create a file called
``workflow.yml``. (This is just to get started - you may have more than one
workflow file in your project, and they can be named however you like (not just
``workflow.yml``).

There are a few options for how you may define your workflow.

In the simplest version of the Renku workflow file, the command, inputs and
outputs are simply listed, as in the example below:

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

.. note:: Note that the script being run is always listed in the ``inputs`` section.

This workflow file defines the workflow's name and a sequence of steps. This
file only includes one step, which is named ``filter``. Within the ``filter``
step, list the ``command`` to run, and then we tell Renku which parts of this
command are ``inputs`` and ``outputs`` by copying those paths into the relevant
sections.

To run this workflow file, run:

.. code-block:: console

        $ renku run workflow.yml


Using Templating in a Workflow File
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Renku provides a templating feature so that you never have to type the same path
twice. There are a few different ways to use templating in your workflow file,
and they can be mixed and matched depending on what works best for your command.

Templating by Group
^^^^^^^^^^^^^^^^^^^
In the example above, all of the inputs go together into the same place in the
command, followed by the output. So, rather than listing each input, output, and
parameter in the command individually, you can use the ``$inputs``,
``$outputs``, and ``$parameters`` templates to tell Renku "put all the inputs
here".

.. code-block:: yaml

    name: flights-processing-pipeline
    steps:
      filter:
        command: python $parameters $inputs $outputs
        inputs:
          - src/filter_flights.py
          - data/flight-data/2019-01-flights.csv.zip
        outputs:
          - data/output/flights-filtered.csv
        parameters:
          - n
          - 10


The inputs are filled in to the command in the order in which they are specified
in the ``inputs`` section.


Templating by Argument Name
^^^^^^^^^^^^^^^^^^^^^^^^^^^

If the ordering of arguments in your command is more complex, you can reference
each argument individually by name. To do so, assign each input and output a
name (such as ``raw-flights``) and a ``path``. Then, we reference those names in
the ``command`` using ``$``.

.. code-block:: yaml

    name: flights-processing-pipeline
    steps:
      filter:
        command: python $n $filter-py $raw-flights $filtered-flights
        inputs:
          - filter-py:
              path: src/filter_flights.py
          - raw-flights:
              path: data/flight-data/2019-01-flights.csv.zip
        outputs:
          - filtered-flights:
              path: data/output/flights-filtered.csv
        parameters:
          - n:
            prefix: -n
            value: 10

.. note:: If your command uses the ``$`` character, you can escape it by doing ``$$``.


A Multi-Step Workflow File
~~~~~~~~~~~~~~~~~~~~~~~~~~

Below, you can see what the a workflow file looks like for the two-step
workflow.

.. code-block:: yaml

    name: flights-processing-pipeline
    steps:
      filter:
        command: python $filter-py $raw-flights $filtered-flights
        inputs:
          - filter-py:
              path: src/filter_flights.py
          - raw-flights:
              path: data/flight-data/2019-01-flights.csv.zip
        outputs:
          - filtered-flights:
              path: data/output/flights-filtered.csv

      count:
        command: python $count-py $filtered-flights $flight_count
        inputs:
          - count-py:
              path: src/count_flights.py
          - filtered-flights:
              path: data/output/flights-filtered.csv
        outputs:
          - flight_count:
              path: data/output/flights-count.csv


Executing a Workflow File
~~~~~~~~~~~~~~~~~~~~~~~~~

Running ``renku run workflow.yml`` will execute all steps in the workflow file.

Renku also helps you run only portions of your workflow at a time. For example,
you can execute just one step of the workflow by referencing that step's name:

.. code-block:: console

        $ renku run workflow.yml filter

        # you may specify more than one step
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


Workflow Step Execution Order
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

When you execute a workflow file, Renku builds an execution graph. This means
that Renku determines how the steps in the workflow are related. For example,
Renku notices that the output of step ``filter`` (``flights-filtered.csv``) is
the input to step ``count``, and therefore step ``filter`` `must`` be executed
before step ``count``. On the other hand, if there are no dependencies between
steps, they may be run in any order. For this reason, unrelated workflow steps
may be executed in a different order than which they are written in the workflow
file.

The ``--dry-run`` and ``--no-commit`` flags
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
By passing the ``--dry-run`` flag to the ``renku run`` command, you can instruct
Renku to only print the order of execution of the steps without actually running
any of them. 

The ``--no-commit`` flags causes Renku to run the workflow file but it won't
create a commit after the execution. Renku also won't create any metadata in
this case.


Adding more Information to a Workflow File
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Implicit Input and Output Files
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If your script consumes or generates an input or output that is not explicitly
passed in the command, you may still list the file in the workflow file step so
that is tracked by Renku. When doing so, also add the `implicit: true` key;
otherwise, Renku will warn that the file is not used in the command string.

Description and Keywords
^^^^^^^^^^^^^^^^^^^^^^^^

You may provide further details in your workflow definition, such as a
`description` of each parameter, and `keywords` that describe your workflow.

.. code-block:: yaml

    name: flights-processing-pipeline
    description: The workflow in the Renku Tutorial
    keywords:
      - tutorial
    steps:
      filter:
        command: python $filter-py $raw-flights $filtered-flights
        description: Filter the raw flights data to only flights to the destination of interest
        inputs:
          - filter-py:
              path: src/filter_flights.py
          - raw-flights:
              description: The raw flights data
              path: data/flight-data/2019-01-flights.csv.zip
        outputs:
          - filtered-flights:
              description: Flights to the destination of interest
              path: data/output/flights-filtered.csv

      count:
        command: python $count-py $filtered-flights $flight_count
        description: Count the number of flights
        inputs:
          - count-py:
              path: src/count_flights.py
          - filtered-flights:
              description: Flights to the destination of interest
              path: data/output/flights-filtered.csv
        outputs:
          - flight_count:
              description: Number of flights to the destination of interest
              path: data/output/flights-count.csv

Alternative Success Codes
^^^^^^^^^^^^^^^^^^^^^^^^^

By default, Renku considers a workflow step to have successfully executed if it
returns a success code of 0. If the command is expected to return a success code
other an 0, specify the acceptable codes in a `success_codes` key:

.. code-block:: console

    name: command-with-alternative-success-codes
    steps:
      head:
        command: head -n 10 data/collection/models.csv data/collection/colors.csv > intermediate
        success_codes: [0, 127]
        ...
