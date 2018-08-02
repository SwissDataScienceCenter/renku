.. _cli_installation:

Command-line Interface Installation
===================================

Interaction with the platform takes place via the Python-based command-line
interface (CLI) and the Python API.

Mac OS X
--------

Easiest is to install using `homebrew <https://brew.sh/>`_:

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


.. note::

    You may get a ``ValueError: unknown locale: UTF-8`` - see `here
    <https://docs.pipenv.org/diagnose/#valueerror-unknown-locale-utf-8>`_ for
    instructions on how to fix it.
