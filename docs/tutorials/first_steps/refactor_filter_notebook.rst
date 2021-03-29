.. _refactor_notebook:

Refactor the notebook
---------------------

To make our filtering step easier to reuse and easier to maintain, we will
refactor what we have written in the notebook into a Python script. To do this
we convert the code in the notebook into a regular Python *.py* file.

Again, for the tutorial, we have already done the refactoring work for you, and
you can just download the script from `here
<https://renkulab.io/projects/renku-tutorials/renku-tutorial-flights-material/files/blob/src/filter_flights.py>`_:

.. code-block:: python

    #
    # Usage: python filter_flights.py <input-path> <output-path>
    #

    import pandas as pd
    import sys

    # It would be more robust to use argparse or click, but we want this to be simple
    if len(sys.argv) < 3:
        sys.exit("Please invoke with two arguments: input and output paths")

    input_path = sys.argv[1]
    output_path = sys.argv[2]


    # Read in the data
    df = pd.read_csv(input_path)

    # Select only flights to Austin (AUS)
    df = df[df['DEST'] == 'DFW']

    # Save the result
    df.to_csv(output_path, index=False)


As before, you can download it and drag & drop into the JupyterLab session or
<<<<<<< HEAD
copy/paste the code above and create a new file in your JupyterLab session. We
will put the scripts in a ``src`` directory (because they are source code). In
JupyterLab, click on the "New Directory" button and name the directory ``src``:

.. image:: ../../_static/images/jupyterlab-new-directory.png
    :width: 85%
    :align: center
    :alt: Create a new directory in JupyterLab


Put the script file into this newly made directory.
=======
copy/paste the code above and create a new file in your JupyterLab session. Put
the file into the ``src`` directory because it is "source code".
>>>>>>> chore: update first steps tutorial

This script will allow us to very easily execute the filtering code as a workflow
step using python.

You can inspect the code in the file viewer in your JupyterLab session.

.. code-block:: console

    $ renku save -m 'added filter script'
