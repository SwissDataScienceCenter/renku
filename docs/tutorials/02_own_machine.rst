.. _own_machine:

Running Renku on Your Own Machine
=================================

Would you like to use Renku, but on your own machine, rather than on RenkuLab.io? Then this tutorial is for you!

This tutorial will help you set up your Renku project on whatever machine you'd like to run it.


Make an account on RenkuLab
---------------------------

First, head to renkulab.io_ (or your own instance of
Renku) and click on the **Login** button located on the top right corner of
the Renku web interface.

On renkulab.io_ you can create an account or sign in with your GitHub
identity by clicking on the corresponding button.



Create your Renku Project
-------------------------

Once logged in, create a new project by going to the **Projects** (1) page
and clicking on the **New Project** (2) button.

.. image:: ../_static/images/ui_01_create-project.png
    :width: 100%
    :align: center
    :alt: Head to new project page

Enter your project title (1); if you wish, change
the visibility (or leave it *public*) (2); select an appropriate Renku
template (in this tutorial, either Python, Julia or R) (3); fill in an optional description
(4) and any other parameters appearing after selecting the template.
Click on the **Create project** button (5).

.. image:: ../_static/images/ui_02_new-project.png
    :width: 100%
    :align: center
    :alt: Create a new project



Set Up Renku on your own Machine
--------------------------------

Install Renku
^^^^^^^^^^^^^

[Install pipx](https://github.com/pipxproject/pipx#install-pipx) and make sure that the `$PATH` is correctly configured.

.. code-block:: shell-session

    $ python3 -m pip install --user pipx
    $ pipx ensurepath

Install renku

.. code-block:: shell-session

    $ pipx install renku
    $ which renku
    ~/.local/bin/renku


Install Docker
^^^^^^^^^^^^^^

Renku uses Docker to manage your project's computational environment.

Install Docker from their website https://www.docker.com/products/docker-desktop/

Clone your Renku project
------------------------


Enable remote access to your Renku Project via an SSH Key
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In order to save your work back to RenkuLab from your own machine, you'll need to create an SSH key and save that key in
your Renku GitLab account.

Create an SSH Key
~~~~~~~~~~~~~~~~~

If you do not have an existing SSH key pair, generate a new one.


1. Open a terminal on the machine where you'd like to run your Renku project.

2. Type `ssh-keygen -t` followed by the key type and an optional comment.
This comment is included in the .pub file that's created.
You may want to use an email address for the comment.

For example, for ED25519:

.. code-block:: shell-session

    $ ssh-keygen -t ed25519 -C "<comment>"


Add your SSH key to RenkuLab
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

1. Copy the contents of your public key file. You can do this manually or use a script.
For example, to copy an ED25519 key to the clipboard
(Replace id_ed25519.pub with your filename. For example, use id_rsa.pub for RSA).

.. tabbed:: macOS

    .. code-block:: console

        $ tr -d '\n' < ~/.ssh/id_ed25519.pub | pbcopy


.. tabbed:: Linux

    This requires the xclip package

    .. code-block:: console

        $ xclip -sel clip < ~/.ssh/id_ed25519.pub

.. tabbed:: Git Bash on Windows

    .. code-block:: console

        $ cat ~/.ssh/id_ed25519.pub | clip


2. Go to https://renkulab.io/gitlab/-/profile/keys

(You can get here by going to https://renkulab.io/gitlab , then in the top right corner, select your avatar > Preferences > SSH Keys)

3. In the Key box, paste the contents of your public key.
If you manually copied the key, make sure you copy the entire key,
which starts with ssh-ed25519 or ssh-rsa, and may end with a comment.


In the Title box, type a description, like Work Laptop or
Home Workstation.


`Optional:` In the Expires at box, select an expiration date.


Select Add key.





Clone the Renku Project
^^^^^^^^^^^^^^^^^^^^^^^

1. Back on renkulab.io_, on your Renku project's page, click “View in GitLab”
2. Click “Clone”, and then “Clone with SSH”
3. On your machine’s terminal, navigate to where you want your project to be located
4. Run `git clone <the clone url you copied>`

NOTE!!! If you created a private project, you'll need to - actually to push back, you'll need your key anyway

Start a Renku session
---------------------

1. Navigate into the renku project directory

.. code-block:: shell-session

    $ cd <project_name>


2. Start a renku session

.. code-block:: shell-session

    renku session start


3. Build the docker image
Renku may inform you that the container image for the current commit does not exist on your machine, and offer to build
it for you. Say yes!

4. Open the session in your browser
When the session starts, it will print out a url where the session is running.
Copy this url into your browser to access your Renku project running inside it's containerized environment.

If you need to find this url again later, you can find all running Renku sessions by running

.. code-block:: shell-session

    $ renku session list
    ID          STATUS    URL
    ----------  --------  ------------------------------------------------------------
    f1693c198e  running   http://0.0.0.0:56674/?token=910ca732ef574049a22d41d0f1109f56

and then

.. code-block:: shell-session

    $ renku session open <session_id>

.. note::

    Would you like to enter your containerized project environment on a shell, rather than via the browser?
    Since Renku uses Docker to manage your project's computational environment, you can use Docker commands to enter the
    container directly and use the shell.


    First, list the running docker processes:

    .. code-block:: console

        $ pocker ps
        CONTAINER ID   IMAGE                          COMMAND                  CREATED          STATUS          PORTS                     NAMES
        f1693c198ea7   renku-stories/geemap:9f50c8a   "tini -- /entrypoint…"   24 minutes ago   Up 24 minutes   0.0.0.0:56674->8888/tcp   dazzling_ishizaka


    Then, open a shell in a running container by providing the Container ID:

    .. code-block:: console

        $ docker exec -it <CONTAINER ID> /bin/bash
        base ▶ ~ ▶ work ❯ project_name ▶ master ▶ $ ▶


.. _renkulab.io: https://renkulab.io
