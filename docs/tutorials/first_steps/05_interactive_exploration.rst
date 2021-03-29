.. _interactive_exploration:

Interactively explore the flights data
--------------------------------------

At this point in a data-science project, you would normally start by looking at
the data, trying to understand its structure, and see how to go about answering
our question: *how many flights had Austin, TX as their destination.*

In this tutorial, we will jump-start the process by using some notebooks that
have already been prepared.

<<<<<<< HEAD
Go to `the first example notebook
<https://renkulab.io/projects/renku-tutorials/renku-tutorial-flights-material/files/blob/notebooks/FilterFlights.ipynb>`_
and download the file to your computer (Note: some browsers might change the
file extension to ``.txt`` - make sure to change it to ``.ipynb`` if that is the
case!). Go to the ``notebooks`` directory in your running interactive session
and drag and drop the downloaded notebook there. To save the notebook run
=======
Go to `the first example notebook <https://renkulab.io/projects/renku-tutorials/renku-tutorial-flights-material/files/blob/notebooks/FilterFlights.ipynb>`_
and download the file to your computer. Go to the ``notebooks`` directory in your
running interactive session and drag and drop the downloaded notebook there. To save
the notebook run
>>>>>>> chore: update first steps tutorial

.. code-block:: console

    $ renku save -m "Created notebook to filter flights to AUS, TX."

    Successfully saved to branch master:
          notebooks/FilterFlights.ipynb
    OK

You should look at the notebook by navigating to
*notebooks/FilterFlights.ipynb*. The logic is not complex to understand, but
you should feel free to execute it to see what it does.
