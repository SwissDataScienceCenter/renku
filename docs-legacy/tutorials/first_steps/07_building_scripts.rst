.. _building_scripts:

Building scripts
----------------

In order to best utilize Renku's powerful commands, it is usually a good idea
to refactor interactive notebooks and other exploratory tools into scripts,
once that part of the pipeline has been tested by the user.

For Jupyter notebooks, this usually amounts to refactoring it into a Python
or Julia script. For R scripts are usually the standard way of exploring data
within RStudio anyway, but nevertheless there is some care to be taken when
making scripts which are designed to be run within a console or 'sourced' to be
ready for the command-line.

Again, for the tutorial, we have already done the refactoring work for you, and
you can just download the relevant scripts.

.. tab-set::

    .. tab-item:: Python

        `Download Python script <https://renkulab.io/projects/renku-tutorials/renku-tutorial-flights-material/files/blob/src/filter_flights.py>`_.

        .. code-block:: python

            #
            # Usage: python src/filter_flights.py <input-path> <output-path>
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


    .. tab-item:: Julia

        `Download Julia script <https://renkulab.io/projects/renku-tutorial/flights-tutorial-julia/files/blob/.tutorial/meta/templates/FilterFlights.jl>`_

        .. code-block:: julia

            #
            # Usage: julia src/FilterFlights.jl <input-path> <output-path>
            #

            using CSV, ZipFile, DataFrames

            input_path = "../../data/flight-data/2019-01-flights.csv.zip"
            output_path = "../../data/output/2019-01-flights-filtered.csv"

            if length(ARGS) > 0
                input_path = ARGS[1]
            end
            if length(ARGS) > 1
                output_path = ARGS[2]
            end

            output_folder = dirname(output_path);

            # Read the flights database
            println("Reading $input_path ...")
            rows = CSV.File(ZipFile.Reader(input_path).files[1]);
            full_df = rows |> DataFrame;

            # Filter to flights to Austin, TX
            df = full_df[full_df.DEST .== "DFW", :]

            # Write the filtered list out
            println("Writing $output_path ...")
            run(`mkdir -p $output_folder`)
            CSV.write(output_path, df)

    .. tab-item:: R

        `Download R script <https://renkulab.io/projects/renku-tutorial/flights-tutorial-r/files/blob/.tutorial/meta/templates/RunFilterFlights.R>`_

        .. code-block:: r

            #
            # Usage: Rscript src/RunFilterFlights.R <input-path> <output-path>
            #

            args <- commandArgs(trailingOnly = TRUE)

            if (length(args) != 2) {
            stop("At least two arguments must be supplied (input and output files).",
                call. = FALSE)
            }

            inputPath <- args[1]
            outputPath <- args[2]

            library(tidyverse)

            data <- unzip(inputPath) %>%
            read.csv()

            filteredData <- data %>% filter(DEST == "DFW")

            outputFolder <- dirname(outputPath)

            if (!dir.exists(outputFolder)) {
            dir.create(outputFolder)
            }

            write.csv(filteredData, outputPath, row.names = FALSE)


As before, you can download it and drag & drop into the JupyterLab session or
upload the file using RStudio into your ``src`` directory.

This script will allow us to very easily execute the filtering code as a workflow
step using the command line.

You can inspect the code in the file viewer in your JupyterLab session or RStudio.

.. code-block:: console

    $ renku save -m 'added filter script'


Organizing scripts within your project
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

For this tutorial we have organized our scripts such that they live in the
``src`` folder. If your project grows to requiring several programming languages
or bash scripts, you may want to organize them into separate folders as we have
demonstrated in the example below.

::

    my-project
    ├── .renku
    ├── data
    ├── notebooks
    ├── Dockerfile
    ├── requirements.txt
    ├── environment.yml
    └── src
        ├── bash
        │   └── init.sh
        ├── julia
        │   └── FilterFlights.jl
        ├── python
        │   └── filter_flights.py
        └── r
            └── RunFilterFlights.r
