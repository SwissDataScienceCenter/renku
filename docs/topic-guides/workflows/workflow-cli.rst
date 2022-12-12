.. _workflow-cli-topic-guide:

Tracking Workflows Interactively
================================

Renku provides a simple set of commands for recording your data processing steps
as you run them. You can also stitch these steps together into workflows.

Tracking a Workflow Step with ``renku run``
-------------------------------------------

To track your code execution as a Renku workflow, simply prepend :meth:`renku run <renku.ui.cli.run>`
in front of your command. Assigning a name makes it easier to re-run this step
later.

.. code-block:: console

    $ renku run --name run-analysis python run_analysis.py input_file.csv output_file.csv

If you need to distinguish ``renku`` arguments from your script's arguments, use
``--`` to mark where the Renku arguments end and the script command starts.

.. code-block:: console

    $ renku run --name run-analysis -- python run_analysis.py input_file.csv output_file.csv

This command creates a workflow step called `run-analysis`.

You can inspect the workflow with :meth:`renku workflow show <renku.ui.cli.workflow>`:

.. code-block:: console

    $ renku workflow show run-analysis
    Id: /plans/76d73efb94964e9aac3635176ea57a36
    Name: run-analysis
    Creators: John Doe <example@renku.ch>
    Command: python run_analysis.py -i input_file.csv -o output_file.csv
    Success Codes:
    Inputs:
            - input-1:
                    Default Value: run_analysis.py
                    Position: 1
            - i-2:
                    Default Value: input_file.csv
                    Position: 2
                    Prefix: -i
    Outputs:
            - o-3:
                    Default Value: output_file.csv
                    Position: 3
                    Prefix: -o

Once the workflow is recorded, you can execute it again :meth:`renku workflow execute <renku.ui.cli.workflow>`:

.. code-block:: console

    $ renku workflow execute run-analysis

Similarly, you can re-execute the workflow with modified parameters, for example:

.. code-block:: console

    $ renku workflow execute run-analysis --set i-2=other_input_file.csv

which would run it on the file ``other_input_file.csv`` instead of the original
``input_file.csv`` file. You could also specify an execution backend with
``--provider``, e.g. ``toil`` for execution in an HPC cluster (You need to
install ``renku`` with the ``toil`` extra for this to be available).


Updating a Workflow
-------------------

Now that the workflow step is tracked in Renku, Renku keeps track of when
upstream files in a workflow have changed and downstream files need to be
updated. You can use :meth:`renku update <renku.ui.cli.update>` to make sure an
output file is up-to-date. If there have been no changes to the upstream files,
Renku will not re-execute the workflow.

.. code-block:: console

    $ renku update output_file.csv



If you'd like to rerun the workflow and re-generate a file, regardless of
whether upstream files have changed, use :meth:`renku rerun <renku.ui.cli.rerun>`.

.. code-block:: console

    $ renku rerun output_file.csv


Composing Workflows
-------------------

By default, Renku recognizes when workflow steps created by :meth:`renku run <renku.ui.cli.run>` are related.

For example, consider an example where workflow step A uses data file
``initial.txt`` to generate file ``intermediate.txt``, which is an input to
workflow step B in order to yield ``final.txt``. When you run ``renku update
final.txt``, Renku will check for updates in workflow steps A and B, since they
are related.


To make this linkage between workflow steps explicit, you may `compose` workflow
steps in order to create a named multiple-step workflow. To create a
workflow ``my-workflow`` out of multiple steps that were created by ``renku
run``, use :meth:`renku workflow compose <renku.ui.cli.workflow>`:

.. code-block:: console

    $ renku workflow compose --link-all my-workflow run-analysis process-output

If you had two steps named ``run-analysis`` and ``process-output``. ``--link-all``
tells Renku to automatically infer dependencies between steps for you. The newly
created ``my-workflow`` can also be executed with :meth:`renku workflow execute <renku.ui.cli.workflow>`.

For more information about working with workflows using the Renku CLI, see :meth:`renku workflow <renku.ui.cli.workflow>`.
