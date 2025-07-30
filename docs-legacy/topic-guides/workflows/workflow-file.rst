.. _workflow-definition-file-topic-guide:

The Renku Workflow File
=======================

If your data processing workflow has many steps, you might find it convenient to
create a workflow definition file. A workflow file help organize your data
processing flow, making it easier to execute portions of the pipeline at a time.
A workflow file also makes your code easier for your collaborators to
understand!


Adding a Workflow File to your Project
--------------------------------------

To create a workflow definition file in your project, create a file called
``workflow.yml``.

You may have more than one workflow file in your project! Workflow files may be
named however you like, as long as they end with the `.yml` or `yaml` file
extension.

.. note:: **Do you already have a workflow you created on the Renku CLI that you'd like to convert to a Workflow File?**
    You can export it! ``renku workflow export <workflow-name> --format renku --output workflow.yml``
    (Tip: Find your workflow's name using ``renku workflow ls``)


Defining a Basic Workflow File
------------------------------

There are a few options for how you may define your workflow.

In the simplest version of the Renku workflow file, the command, inputs and
outputs are simply listed, as in the example below:

.. code-block:: yaml

    name: data-pipeline
    steps:
      filter:
        command: python src/filter.py data/input/flights.csv data/output/filtered.csv
        inputs:
          - src/filter.py
          - data/input/flights.csv
        outputs:
          - data/output/filtered.csv

.. note:: Note that the script being run is always listed in the ``inputs`` section (here, ``src/filter.py``).

This workflow file defines the workflow's name and a sequence of steps. This
file only includes one step, which is named ``filter``. Within the ``filter``
step, list the ``command`` to run, and then we tell Renku which parts of this
command are ``inputs`` and ``outputs`` by copying those paths into the relevant
sections.

To run this workflow file, run it with :meth:`renku run <renku.ui.cli.run>`:

.. code-block:: console

        $ renku run workflow.yml


Using Templating in a Workflow File
-----------------------------------

Renku provides a templating feature so that you never have to type the same path
twice. There are a few different ways to use templating in your workflow file,
and they can be mixed and matched depending on what works best for your command.


Templating by Group
~~~~~~~~~~~~~~~~~~~

In the example above, all of the inputs go together into the same place in the
command, followed by the output. So, rather than listing each input, output, and
parameter in the command individually, you can use the ``$inputs``,
``$outputs``, and ``$parameters`` templates to tell Renku "put all the inputs
here".

.. code-block:: yaml

    name: data-pipeline
    steps:
      filter:
        command: python $parameters $inputs $outputs
        inputs:
          - src/filter.py
          - data/input/flights.csv
        outputs:
          - data/output/filtered.csv
        parameters:
          - -n
          - 10


The inputs are filled in to the command at the ``$inputs`` template in the order
in which they are specified in the ``inputs`` section. The same goes for
``$outputs`` and ``$parameters``.


Templating by Argument Name
~~~~~~~~~~~~~~~~~~~~~~~~~~~

If the ordering of arguments in your command is more complex, you can reference
each argument individually by name. To do so, assign each input and output a
name (such as ``raw``) and a ``path``. Then, we reference those names in
the ``command`` using ``$``.

.. code-block:: yaml

    name: data-pipeline
    steps:
      filter:
        command: python $n $filter-py $raw $filtered
        inputs:
          - filter-py:
              path: src/filter.py
          - raw:
              path: data/input/flights.csv
        outputs:
          - filtered:
              path: data/output/filtered.csv
        parameters:
          - n:
            prefix: -n
            value: 10

.. note:: Renku uses basic YAML syntax for workflow definition files.
    Users should not use advanced YAML syntax like anchors, aliases, schema,
    etc. since the behavior is undefined. Moreover, in future we will implement
    a customized YAML parser that won't allow these features.

.. note:: If your command uses the ``$`` character, you can escape it by doing ``$$``.


A Multi-Step Workflow File
--------------------------

Below, you can see what the a workflow file looks like for a two-step
workflow.

.. code-block:: yaml

    name: data-pipeline
    steps:
      filter:
        command: python $filter-py $raw $filtered
        inputs:
          - filter-py:
              path: src/filter.py
          - raw:
              path: data/input/flights.csv
        outputs:
          - filtered:
              path: data/output/filtered.csv

      count:
        command: python $count-py $filtered $counts
        inputs:
          - count-py:
              path: src/count.py
          - filtered:
              path: data/output/filtered.csv
        outputs:
          - counts:
              path: data/output/counts.csv


Executing a Workflow File
-------------------------

Running :meth:`renku run workflow.yml <renku.ui.cli.run>` will execute all steps
in the workflow file. Executing the workflow will commit all workflow inputs and
outputs, too, including the workflow file itself.

.. code-block:: console

    $ renku run workflow.yml
    Executing step 'data-pipeline.filter': 'python src/filter.py data/input/flights.csv data/output/filtered.csv' ...
    Executing step 'data-pipeline.count': 'python src/count.py data/output/filtered.csv data/output/counts.csv' ...

.. note:: **Do you have output files you don't want to be committed, such as log files?**
    You have 2 options: (1) Do not list these outputs in the workflow definition
    file, and Renku will ignore them. Or, (2) include the file in the workflow
    file, but use the ``persist: false`` flag to tell Renku not to commit the
    file.

Executing a Portion of a Workflow
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Renku also helps you run only portions of your workflow at a time. For example,
you can execute just one step of the workflow by referencing that step's name:

.. code-block:: console

        $ renku run workflow.yml filter

You may specify more than one step to run:

.. code-block:: console

        $ renku run workflow.yml filter count


Workflow Step Execution Order
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When you execute a workflow file, Renku builds an execution graph to determine
how the steps in the workflow are related. Renku then executes the steps in that
order. This means that only the data dependencies between steps determine the
execution order, not the order of steps in the workflow file.


The ``--dry-run`` and ``--no-commit`` flags
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

By passing the ``--dry-run`` flag to the ``renku run`` command, you can instruct
Renku to only print the order of execution of the steps without actually running
any of them.

The ``--no-commit`` flags causes Renku to run the workflow file but it won't
create a commit after the execution. Renku also won't create any metadata in
this case. This is a great option to use when developing or verifying a workflow!


Adding more Information to a Workflow File
------------------------------------------


Implicit Input and Output Files
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If your script consumes or generates an input or output that is not explicitly
passed in the command, you may still list the file in the workflow file so that
it is tracked by Renku. When doing so, also add the ``implicit: true`` key;
otherwise, Renku will warn that the file is not used in the command string.

.. code-block:: yaml

    name: script-with-implicit-input
    steps:
      filter:
        command: python $my-script
        inputs:
          - my-script:
              path: my-script.py
          - hidden-input:
              path: data/an-input.txt
              implicit: true


Descriptions and Keywords
~~~~~~~~~~~~~~~~~~~~~~~~~

You may provide further details in your workflow definition, such as a
`description` of each parameter, and `keywords` that describe your workflow.

.. code-block:: yaml

    name: data-pipeline
    description: The workflow in the Renku Tutorial
    keywords:
      - tutorial
    steps:
      filter:
        command: python $filter-py $raw $filtered
        description: Filter the raw flights data to only flights to the destination of interest
        inputs:
          - filter-py:
              path: src/filter.py
          - raw:
              description: The raw flights data
              path: data/input/flights.csv
        outputs:
          - filtered:
              description: Flights to the destination of interest
              path: data/output/filtered.csv

      count:
        command: python $count-py $filtered $counts
        description: Count the number of flights
        inputs:
          - count-py:
              path: src/count.py
          - filtered:
              description: Flights to the destination of interest
              path: data/output/filtered.csv
        outputs:
          - counts:
              description: Number of flights to the destination of interest
              path: data/output/counts.csv


Alternative Success Codes
~~~~~~~~~~~~~~~~~~~~~~~~~

By default, Renku considers a workflow step to have successfully executed if it
returns a success code of 0. If the command is expected to return a success code
other an 0, specify the acceptable codes in a `success_codes` key:

.. code-block:: yaml

    name: command-with-alternative-success-codes
    steps:
      head:
        command: head -n 10 data/collection/models.csv data/collection/colors.csv > intermediate
        success_codes: [0, 127]
        ...


Viewing a Workflow Visually
---------------------------

After executing a workflow, you can view a visual diagram of how any file created
by that workflow was created.

To view this diagram, run :meth:`renku workflow visualize <renku.ui.cli.workflow>`
and pass the path to the file you would like to inspect:

.. code-block:: console

    $ renku workflow visualize data/output/counts.csv
                                        ┌─────────────────────────────────────────┐                    ┌─────────────┐                    ┌──────────────────────┐
                                        │workflows/workflow-flights-tutorial-3.yml│                    │src/filter.py│                    │data/input/flights.csv│
                                        └─────────────────────────────────────────┘                    └─────────────┘                    └──────────────────────┘
                                                            *             *******                                    ***                             ***
                                                            *                    ************                           ****                    *****
                                                            *                                **************                 ****           ****
                                                            *                                              *************  ╔═══════════════════════╗
                                                            *                                                           **║python src/filter.py...║
                                                            *                                                             ╚═══════════════════════╝
                                                            *                                                                              *
                                                            *                                                                              *
                                                            *                                                                              *
        ┌────────────┐                                      *                                                             ┌────────────────────────┐
        │src/count.py│                                      *                                                             │data/output/filtered.csv│
        └────────────┘                                      ***                                                           └────────────────────────┘
                        *********                              *****                                                           *****
                                ************                       *****                                              ********
                                            *************               ****                                 *********
                                                            *************  ╔══════════════════════╗  *****
                                                                         **║python src/count.py...║
                                                                           ╚══════════════════════╝
                                                                                        *
                                                                                        *
                                                                                        *
                                                                            ┌──────────────────────┐
                                                                            │data/output/counts.csv│
                                                                            └──────────────────────┘
