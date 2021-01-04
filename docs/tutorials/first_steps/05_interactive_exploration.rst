.. _interactive_exploration:

Interactively explore the flights data
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

At this point in a data-science project, you would normally start by looking at
the data, trying to understand its structure, and see how to go about answering
our question: *how many flights had Austin, TX as their destination.*

In this tutorial, we will jump-start the process by using some notebooks that
have already been prepared.

Use the commands below to add the first notebook to your project.

.. code-block:: console

    wget -O notebooks/00-FilterFlights.ipynb https://renkulab.io/gitlab/renku-tutorial/renku-tutorial-flights/raw/master/.tutorial/meta/templates/00-FilterFlights-doi.ipynb

    # Output similar to:
    # --2019-04-29 14:38:02--  https://renkulab.io/gitlab/renku-tutorial/renku-tutorial-flights/raw/master/.tutorial/meta/templates/00-FilterFlights-doi.ipynb
    # Resolving renkulab.io (renkulab.io)... 86.119.40.77
    # Connecting to renkulab.io (renkulab.io)|86.119.40.77|:443... connected.
    # HTTP request sent, awaiting response... 200 OK
    # Length: 1909 (1.9K) [text/plain]
    # Saving to: ‘notebooks/00-FilterFlights.ipynb’
    #
    # notebooks/00-FilterFlights.ipynb        100%[==============================================================================>]   1.86K  --.-KB/s    in 0s
    #
    # 2019-04-29 14:38:03 (105 MB/s) - ‘notebooks/00-FilterFlights.ipynb’ saved [1909/1909]

.. code-block:: console

    git add notebooks
    git commit -m"Created notebook to filter flights to AUS, TX."
    git push

    # [...]
    # To https://dev.renku.ch/gitlab/john.doe/flights-tutorial.git
    #     0fb9ac1..d0c4d1f  master -> master

You should look at the notebook by navigating to
*notebooks/00-FilterFlights.ipynb*. The logic is not complex to understand, but
you should feel free to execute it to see what it does.

Refactor the notebook
^^^^^^^^^^^^^^^^^^^^^

To make our filtering step easier to reuse and easier to maintain, we will
refactor what we have written in the notebook into a Python script. To do this
we convert the code in the notebook into a regular Python *.py* file.

Again, for the tutorial, we have already done the refactoring work for you,
and you can just download the script. We will save it in the `src` folder
because it is source code.

.. code-block:: console

    mkdir src
    wget -O src/00-FilterFlights.py https://renkulab.io/gitlab/renku-tutorial/renku-tutorial-flights/raw/master/.tutorial/meta/templates/00-FilterFlights.py

    # [...]
    # 2019-04-29 14:56:52 (114 MB/s) - ‘src/00-FilterFlights.py’ saved [1823/1823]

You can inspect the code in the file viewer in your JupyterLab session.

Again, the code needs to be added to the repository:

.. code-block:: console

    git add src
    git commit -m"Extracted logic from FilterFlights notebook into script."
    git push

    # [...]
    # To https://dev.renku.ch/gitlab/john.doe/flights-tutorial.git
    #     a40f192..7922ee1  master -> master

.. _renkulab.io: https://renkulab.io
.. _documentation: https://renku.readthedocs.io/
.. _papermill: https://papermill.readthedocs.io/en/latest/
