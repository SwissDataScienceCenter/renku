.. _install_renku:

Install Renku
=============

Option 1: Install Renku via pipx
--------------------------------

On the machine where we would like to work with your renku project,
install and execute Renku in an isolated environment using ``pipx``.
This will guarantee that there are no version conflicts with dependencies
you are using for your work and research.

Install `pipx <https://github.com/pipxproject/pipx#install-pipx>`_
and make sure that the ``$PATH`` is correctly configured.

::

    $ python3 -m pip install --user pipx
    $ pipx ensurepath

Once ``pipx`` is installed, use following command to install ``renku``.

::

    $ pipx install renku
    $ which renku
    ~/.local/bin/renku

Option 2: Install Renku via pip
-------------------------------

Alternatively, you can install renku via ``pip``. But make sure to install it inside a virtual environment, such as ``conda`` or ``virtualenv``!

::

    $ conda create -n renku python=3.9
    $ pip install renku
