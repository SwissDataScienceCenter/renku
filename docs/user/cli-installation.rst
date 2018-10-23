.. _cli_installation:

Command-line Interface Installation
===================================

Interaction with the platform takes place via the Python-based command-line
interface (CLI) and the Python API.

Mac OS X
--------

It is easiest to install the CLI using `homebrew <https://brew.sh/>`_:

.. code-block:: console

  brew tap swissdatasciencecenter/renku
  brew install renku


Pip Script Installer (``pipsi``)
--------------------------------

You can use `pipsi <https://github.com/mitsuhiko/pipsi>`_ to isolate
dependencies and to guarantee that there are no version conflicts. Make sure
you have the ``pipsi`` command correctly installed and ``~/.local/bin`` is in
your ``PATH``.

.. code-block:: console

  pipsi install renku
  which renku
  ~/.local/bin/renku


``pip``/``pipenv``
------------------

For development or if the above methods do not work for you, Renku can also
be installed with ``pip`` or ``pipenv``:

.. code-block:: console

  pip install --pre renku

If you want to have Renku as a project dependency, you may install it using
``pipenv`` in your project's directory:

.. code-block:: console

  pipenv install --pre renku
  pipenv shell


Installing from source
----------------------

Using ``conda``, we can very easily install renku from source into an isolated
environment.

Bleeding-edge
^^^^^^^^^^^^^

To install the latest bleeding-edge version of Renku and keep it from
polluting your application environment, an easy solution is to place it inside
an  isolated ``conda`` environment. If you don't have ``conda`` already, you
should `install miniconda <https://conda.io/miniconda.html>`__. Once you have
it installed, you can run

.. code-block:: console

  conda create -y -n renku python=3.6
  $(conda env list | grep renku | awk '{print $2}')/bin/pip install -e git+https://github.com/SwissDataScienceCenter/renku-python.git#egg=renku
  mkdir -p ~/.renku/bin
  ln -s "$(conda env list | grep renku | awk '{print $2}')/bin/renku" ~/.renku/bin/renku

This will create an isolated environment for renku and link the binary to
``.renku/bin`` in your home directory. If you want to use it, you should
add this to your ``PATH``:

.. code-block:: console

  export PATH=~/.renku/bin:$PATH

If you want it to be done automatically for your shell (bash), add it to ``.bashrc``:

.. code-block:: console

  echo "export PATH=~/.renku/bin:$PATH" >> $HOME/.bashrc
  source $HOME/.bashrc

When you want to update the installed version again, simply do

.. code-block:: console

  $(conda env list | grep renku | awk '{print $2}')/bin/pip install -e git+https://github.com/SwissDataScienceCenter/renku-python.git#egg=renku


Specific version
^^^^^^^^^^^^^^^^

To install a specific version of renku the procedure is nearly identical
the above, but instead of installing from source you install a version with ``pip``.
For example, after creating the ``conda`` environment as described in the previous
section, you can install `renku v0.2.0` with

.. code-block:: console

  $(conda env list | grep renku | awk '{print $2}')/bin/pip install renku==0.2.0


.. note::

    You may get a ``ValueError: unknown locale: UTF-8`` - see `here
    <https://docs.pipenv.org/diagnose/#valueerror-unknown-locale-utf-8>`_ for
    instructions on how to fix it.
