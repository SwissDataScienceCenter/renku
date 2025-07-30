  .. _cli_installation:

Install the Renku Command-line Interface
========================================

The Python-based command-line interface (CLI) and the Python API that we call
``renku`` is available for use via RenkuLab sessions, and you can
also install it on your local machine. Most available CLI commands are
documented in our :download:`cheatsheet <../../renku-python/docs/_static/cheatsheet/cheatsheet.pdf>`.


Local installation with the script manager ``pipx``
---------------------------------------------------

Install and execute renku in an isolated environment using ``pipx``.
It will guarantee that there are no version conflicts with dependencies
you are using for your work and research.

.. note::

  This is the method of installation in the Renku docker images,
  i.e. the default environment you use when you launch a JupyterLab session
  via the Renku browser interface.

`Install pipx <https://github.com/pipxproject/pipx#install-pipx>`_
and make sure that the ``$PATH`` is correctly configured.

::

    $ python3 -m pip install --user pipx
    $ python3 -m pipx ensurepath

Once ``pipx`` is installed use following command to install ``renku``.

::

    $ pipx install renku
    $ which renku
    ~/.local/bin/renku


.. _upgrading_local:

Upgrading
^^^^^^^^^

To upgrade renku to the latest stable version:

::

    $ pipx upgrade renku

To install renku at a specific version:

::

    $ pipx install --force renku==0.10.3

To upgrade to the latest development version:

::

    $ pipx upgrade --pip-args=--pre renku

If you run into dependency problems during the CLI installation,
have a look at the :ref:`cli-troubleshooting`.

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

To install the latest bleeding-edge version of renku and keep it from
polluting your application environment, an easy solution is to place it inside
an  isolated ``conda`` environment. If you don't have ``conda`` already, you
should `install miniconda <https://conda.io/miniconda.html>`__. Once you have
it installed, you can run

.. code-block:: console

  conda create -y -n renku python=3.9
  $(conda env list | grep '^renku\s' | awk '{print $2}')/bin/pip install -e git+https://github.com/SwissDataScienceCenter/renku-python.git#egg=renku
  mkdir -p ~/.renku/bin
  ln -s "$(conda env list | grep '^renku\s' | awk '{print $2}')/bin/renku" ~/.renku/bin/renku

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

  $(conda env list | grep '^renku\s' | awk '{print $2}')/bin/pip install -e git+https://github.com/SwissDataScienceCenter/renku-python.git#egg=renku


Specific version
^^^^^^^^^^^^^^^^

To install a specific version of renku the procedure is nearly identical
the above, but instead of installing from source you install a version with ``pip``.
For example, after creating the ``conda`` environment as described in the previous
section, you can install `renku v0.3.0` with

.. code-block:: console

  $(conda env list | grep '^renku\s' | awk '{print $2}')/bin/pip install renku==0.3.0


.. note::

    You may get a ``ValueError: unknown locale: UTF-8`` - see `here
    <https://docs.pipenv.org/diagnose/#valueerror-unknown-locale-utf-8>`_ for
    instructions on how to fix it.


.. _cli-troubleshooting:

CLI installation problems
-------------------------

``psutil`` failure during renku CLI execution or installation
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Using ``pip`` or ``pipx`` can result in a failure of the ``psutil`` dependency
to install correctly. For example:

.. code-block:: shell

  pipx install renku
  ...
  /root/.local/pipx/venvs/renku/include/site/python3.7/psutil" failed with error code 1 in /tmp/pip-install-c7z7y8vs/psutil/
  '/root/.local/pipx/venvs/renku/bin/python -m pip install renku -q' failed

This can be solved in \*nix systems by installing the ``musl`` library. For
example, on Ubuntu:

.. code-block:: shell

  # install the musl library and headers
  apt-get install musl-dev
  # link the library
  ln -s /usr/lib/x86_64-linux-musl/libc.so /lib/libc.musl-x86_64.so.1
