.. _workflows:

Renku Workflows
===============

Together with :ref:`provenance of results <provenance>`, Renku workflows is
a key feature of Renku to make code and processing pipelines reusable.
When tracking executions with Renku, each execution creates a workflow step.
These steps can be joined together into bigger workflows, making it easy to
run them with varying inputs and on different execution backends.

There are many uses for this. You could define and test a pipeline locally
and then execute it on an HPC cluster on a big dataset once you're happy.
Or you could run your workflow over a range of different parameters to see
how the results differ. Or you create a workflow that runs daily with a
CRON job.

All executions of workflows are also tracked in the provenance, without you
having to re-type and memorize the commands used in each step.

Working with Workflows
----------------------

Tracking execution of a command with :meth:`renku run <renku.ui.cli.run>`:

.. code-block:: console

    $ renku run --name run-analysis -- python run_analysis.py -i inputs -o outputs

creates a workflow step called `run-analysis`. You can inspect the workflow
with :meth:`renku workflow show <renku.ui.cli.workflow>`:

.. code-block:: console

    $ renku workflow show run-analysis
    Id: /plans/76d73efb94964e9aac3635176ea57a36
    Name: run-analysis
    Creators: John Doe <example@renku.ch>
    Command: python run_analysis.py -i inputs -o outputs
    Success Codes:
    Inputs:
            - input-1:
                    Default Value: run_analysis.py
                    Position: 1
            - i-2:
                    Default Value: inputs
                    Position: 2
                    Prefix: -i
    Outputs:
            - o-3:
                    Default Value: outputs
                    Position: 3
                    Prefix: -o

You can execute it with :meth:`renku workflow execute <renku.ui.cli.workflow>`:

.. code-block:: console

    $ renku workflow execute run-analysis --set i-2=other_inputs

which would run it on the file `other_inputs` instead of the original `inputs`
file.
You could also specify an execution backend with `--provider`, e.g. `toil` for
execution in an HPC cluster (You need to install `renku` with the `toil` extra
for this to be available).

To create a workflow `my-workflow` out of multiple steps use :meth:`renku workflow compose <renku.ui.cli.workflow>`:

.. code-block:: console

    $ renku workflow compose --link-all my-workflow run-analysis process-output

if you had two steps named `run-analysis` and `process-output`. `--link-all`
tells Renku to automatically infer dependencies between steps for you. The newly
created `my-workflow` can also be executed with :meth:`renku workflow execute <renku.ui.cli.workflow>`.

Inspecting Workflows
--------------------

You can see workflows in a project in a Renku deployment such as `renkulab.io <https://renkulab.io>`_
by going to a project and opening the `Workflows` tab:

.. Insert picture!

There you can see, filter and navigate all workflows and steps used in this
project.

Selecting a workflow or step shows you its details and allows you to navigate
between steps.

If you click on a step, you can see the command used in it, it's inputs,
outputs and parameters as well as other related metadata, such as when it was
last executed, how long executions of it take on average and more:

.. Insert picture!

Selecting a workflow will instead show you the steps it contains, parameters of
steps that it exposes directly as well as the dependencies between steps.

.. Insert picture!