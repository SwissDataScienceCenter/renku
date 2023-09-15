.. _interactive_exploration:

Interactively explore the flights data
--------------------------------------

At this point in a data-science project, you would normally start by looking at
the data, trying to understand its structure, and see how to go about answering
our question: *how many flights had Austin, TX as their destination.*

In this tutorial, we will jump-start the process by using some notebooks and scripts
that have already been prepared.

.. tab-set::

    .. tab-item:: Python

        Go to `the first example Python notebook
        <https://renkulab.io/projects/renku-tutorials/renku-tutorial-flights-material/files/blob/notebooks/FilterFlights.ipynb>`_
        and download the file to your computer. Go to the ``notebooks`` directory in your
        running interactive session and drag and drop the downloaded notebook there.

    .. tab-item:: Julia

        Go to `the first example Julia notebook
        <https://renkulab.io/projects/renku-tutorial/flights-tutorial-julia/files/blob/.tutorial/meta/templates/FilterFlights.ipynb>`_
        and download the file to your computer. Go to the ``notebooks`` directory in your
        running interactive session and drag and drop the downloaded notebook there.

    .. tab-item:: R

        Go to `this example script <https://renkulab.io/projects/renku-tutorial/flights-tutorial-r/files/blob/.tutorial/meta/templates/FilterFlights.R>`_
        download the file to your computer. Make the ``src`` directory in your
        running interactive session and upload the script there.


Note: some browsers might change the file extension to ``.txt`` - make sure
to change it to ``.ipynb`` if that is the case!

In any case, use the ``renku save`` command to save your results, like the
following:

.. code-block:: console

    $ renku save -m "Created notebook to filter flights to AUS, TX."

    Successfully saved to branch master:
          notebooks/FilterFlights.ipynb
    OK

You should look at the respective notebook or script to understand the logic.
You can execute the relevant steps iteratively if you are new to this type of
data wrangling.
