.. _documentation:

Contributing to the Documentation
=================================

You have come across some missing or outdated information in the Renku docs? Why
not submit a pull request to fix it. Contributions to the Renku documentation
are highly appreciated!

If you are a VSCode user, note that you can use either a local dev container or
a GitHub Codespace to get an environment with all the dependencies taken care of for you.

Building the docs
-----------------

To build the documentation locally, we recommend using ``virtualenv``:

.. code-block:: console

    /workspace/renku $ virtualenv .venv
    /workspace/renku $ source .venv/bin/activate
    (.venv) /workspace/renku $ pip install -r docs/requirements.txt
    (.venv) /workspace/renku $ cd docs
    (.venv) /workspace/renku/docs $ make clean; make html

After building the docs you can access them by opening
``_build/html/index.html`` in a browser.

Running documentation tests
---------------------------

Before submitting a pull request with your documentation changes we ask you
to ensure that the documentation builds (`Building the docs`_) and check your
changes for spelling mistakes by running:

.. code-block:: console

    (.venv) /workspace/renku/docs $ sphinx-build -nNW -b spelling -d _build/doctrees ./ _build/spelling
    (.venv) /workspace/renku/docs $ sphinx-build -nNW . _build/html
