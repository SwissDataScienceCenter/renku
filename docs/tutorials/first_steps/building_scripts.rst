.. _building_scripts:

Building scripts
----------------

In order to best utilize renku's powerful commands, it is usually a good idea
to refactor interactive notebooks and other exploratory tools into scripts,
once that part of the pipeline has been tested by the user.

For Jupyter notebooks, this usually amounts to refactoring it into a Python
or Julia script. For R scripts are usually the standard way of exploring data
within RStudio anyway, but nevertheless there is some care to be taken when
making scripts which are designed to be run within a console or 'sourced' to be 
ready for the command-line.

Again, for the tutorial, we have already done the refactoring work for you, and
you can just download the relevant scripts.

.. tabbed:: Python

    `Download Python script <https://renkulab.io/projects/renku-tutorials/renku-tutorial-flights-material/files/blob/src/filter_flights.py>`_

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


.. tabbed:: Julia
    
        `Download Julia script <https://renkulab.io/projects/renku-tutorials/renku-tutorial-flights-material/files/blob/src/filter_flights.py>`_
    
        .. code-block:: julia
        
            #
            
.. tabbed:: R

    `Download R script <https://renkulab.io/projects/renku-tutorials/renku-tutorial-flights-material/files/blob/src/filter_flights.py>`_

    .. code-block:: r

        #


As before, you can download it and drag & drop into the JupyterLab session or
copy/paste the code above and create a new file in your JupyterLab session. We
will put the scripts in a ``src`` directory (because they are source code). In
JupyterLab, click on the "New Directory" button and name the directory ``src``:

.. image:: ../../_static/images/jupyterlab-new-directory.png
    :width: 85%
    :align: center
    :alt: Create a new directory in JupyterLab


Put the script file into this newly made directory.

This script will allow us to very easily execute the filtering code as a workflow
step using python.

You can inspect the code in the file viewer in your JupyterLab session.

.. code-block:: console

    $ renku save -m 'added filter script'
