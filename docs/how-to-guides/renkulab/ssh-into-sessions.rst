.. _ssh_into_sessions:

SSH Connections into Sessions
=============================

Often, the compute environment offered by Renku is very useful, but having to interact
with it through a browser window is limiting and can interrupt normal development flow.

In these cases, you can use the SSH feature of renku sessions to connect directly
to the remote environment and work on it from your local machine with tools such as
VSCode.

Checking if SSH is supported in your project
--------------------------------------------

For SSH to be supported, both the Renku deployment and your project have to be set up
correctly. Official Renku deployments such as renkulab.io all support SSH, if you
are working on a custom deployment and SSH is not set up, contact your administrator
to check if it can be enabled.

The easiest way to check if SSH is supported is to go to the status page of your project
and check if it says that SSH is enabled.

If no information is shown about SSH, this deployment of Renku isn't set up to handle SSH.

If it says that SSH is not supported for this project, this means that your project
is either based on an older project template or uses a project template that doesn't
support SSH. If you are using one of the official project templates, simply go to
the Status page and click "Update" in the "Template Version" section, which will
update your project to a version that supports SSH. If after this your project still
doesn't support SSH, ask the template maintainer to enable SSH in the template and
perform the update again.

.. note::
    For template maintainers: For your template to support SSH, you need to add
    a ``ssh_supported: true`` entry for each template in the ``manifest.yaml`` and
    update all the Dockerfile to use at least version ``0.14.0`` of the Renku base
    images, which is the version that comes with an SSH server installed.

Setting up your local system for SSH access
-------------------------------------------

To use the SSH feature, you need to have OpenSSH and the Renku CLI installed on
your system.

You need to clone the project, log in to your renku instance (This guide will
assume renkulab.io) and setup SSH config:

.. code-block:: console

    $ renku clone https://renkulab.io/gitlab/user/my-project.git
    $ cd my-project
    $ renku login renkulab.io
    $ renku session setup-ssh

The last command will create SSH keys for use with that renku instance and set
up the initial SSH config needed for connecting to your sessions.

Launching and connecting to an SSH enabled session
--------------------------------------------------

To launch a session, simply use the CLI with the ``--ssh`` flag:

.. code-block:: console

    $ renku session start -p renkulab --ssh
    [...]
    SSH connection successfully configured, use 'ssh renkulab.io-myproject-02a9e407' to connect.
    Session user-myproject-02a9e407 successfully started

This will add your local SSH keys to the allowed keys in the session, push
those changes and then start the session. Once the session is started, it
will create an SSH connection entry for that session that can be used with
your SSH client or tools like VSCode.

In the example above, the SSH connection name is ``renkulab.io-myproject-02a9e407``.

You can also use renku to open an SSH connection for you, using the id of the
session (If prompted to accept host keys, confirm with ``yes``):

.. code-block:: console

    $ renku session open --ssh user-myproject-02a9e407
    venv ▶ ~ ▶ $

To exit the SSH shell, simply type ``exit``.

Using an SSH session in VSCode
------------------------------

Once you ran ``renku session start`` as mentioned above, the created SSH connection
should show up in VSCode if you have the `Remote - SSH Extension <https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-ssh>`_
installed.
Open the ``Remote Explorer`` in the left bar and pick the SSH connection for the session,
e.g. ``renkulab.io-myproject-02a9e407``. This should open the remote project in
your VSCode.

For more details on the VSCode SSH extension see
`the official documentation <https://code.visualstudio.com/docs/remote/ssh>`_.

