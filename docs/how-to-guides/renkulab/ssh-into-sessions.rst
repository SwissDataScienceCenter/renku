.. _ssh_into_sessions:

SSH into RenkuLab Sessions
==========================

Would you prefer to work in a RenkuLab session from your local environment? Do
you prefer to edit code in your own IDE, rather than in a browser?

You can use the SSH feature of RenkuLab sessions to connect directly to the
remote session environment and work on it from your local machine. You can even
open the session with your favoite tools, such as VSCode.

Check if SSH is enabled in your project
---------------------------------------

You can check if your project supports SSH by clicking the drop down next to the
Session Start button and seeing if the SSH option is visible and enabled. If you
see the SSH-enabled icon in the screenshot below, your project is SSH-enabled,
and you can skip down to the next section!

.. warning::
    <screenshot of the Session Start drop down menu with SSH enabled>

.. note::
    **Don't see an SSH option in the Session Start menu?** If you are working on
    a custom instance of RenkuLab (not renkulab.io), this may be because SSH
    needs to be enabled by your administrator. Contact your RenkuLab
    administrator to see if SSH can be enabled.

Enabling SSH for your project
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. warning::
    <screenshot of the Session Start drop down menu with SSH NOT enabled>

If the SSH dialogue says that SSH is not enabled for this project, this likely
means that your project is using an old project template. To update your project
template, go to the Status page and click "Update" in the "Template Version"
section.

.. warning::
    <screenshot of the Status page with a Template Version box that's asking to be updated>

.. note::
    **Are you using a custom project template?** If you've updated your project
    template but your project still doesn't support SSH, ask the template
    maintainer to enable SSH in the template and perform the update again.

.. note::
    **Note for template maintainers:** For your template to support SSH, you need
    to add a ``ssh_supported: true`` entry for each template in the ``manifest.yaml``
    and update all the Dockerfile to use at least version ``0.14.0`` of the Renku
    base images, which is the version that comes with an SSH server installed.


Set up your local system for SSH access
---------------------------------------

Connecting to RenkuLab sessions via SSH requires 3 prerequisites:
* OpenSSH is installed
* the Renku CLI is installed, with version >= <X>
* your Renku project is cloned

This section will guide you through setting up these prerequisites.

#.  To use the SSH feature, you need to have OpenSSH installed on your system.

    .. warning::
        <How to check is OpenSSH is installed?!>

#.  Install the Renku CLI, with version >= <X>. 

    If you do not already have the CLI installed, see :ref:`cli_installation`. 
   
    To check the version of your Renku CLI, run ``renku --version``. If the
    version is less than <X>, run ``pip install --upgrade renku>=<X>``.

    .. warning::
        <Enter version number above 3x>

#.  Clone your Renku Project. You can find the :meth:`renku clone <renku.ui.cli.clone>`
    command under your project's Settings tab. Or, form it yourself in the style
    of the following URL: 

    .. code-block:: console
    
        $ renku clone https://renkulab.io/gitlab/user/my-project.git

    Once you have your project cloned, ``cd`` into it.

    .. code-block:: console
    
        $ cd my-project


#.  Log in to RenkuLab:

    .. code-block:: console
    
        $ renku login renkulab.io

    Note: If your project is located on a custom RenkuLab (not
    ``renkulab.io``), use that RenkuLab's url instead.

#.  Set up SSH for use with this RenkuLab deployment:

    .. code-block:: console
    
        $ renku session setup-ssh

    This command creates SSH keys for use with that renku instance and setup
    the initial SSH config needed for connecting to your sessions.


Launch an SSH-enabled session
-----------------------------

#.  Navigate so your current working directory is the project you want to start
    a session in.

    .. code-block:: console
    
        $ cd my-project

#. Log in to RenkuLab with :meth:`renku login <renku.ui.cli.login>`: 

    .. code-block:: console

        $ renku login renkulab.io

    .. warning::
        <is this correct that this is a necessary pre-requisite?>


#. Pull any changes. This makes sure that the session you start is for the
   latest version of your project.

    .. code-block:: console

        $ git pull


#. Start a session with :meth:`renku session start <renku.ui.cli.session>`,
   using the ``--ssh`` flag:

    .. code-block:: console

        $ renku session start -p renkulab --ssh
        [...]
        SSH connection successfully configured, use 'ssh renkulab.io-myproject-02a9e407' to connect.
        Session user-myproject-02a9e407 successfully started

    Curious what's happening under the hood? This command starts a new session
    on RenkuLab. But first, it adds your local SSH keys to the ``allowed_keys``
    in the project and pushes those changes to RenkuLab. If this is your first
    time using the SSH feature on RenkuLab, this command creates an SSH keypair
    for you. Once the session is started, it creates an SSH connection entry in
    your local SSH config for that session id. This SSH config entry can be used
    with your SSH client or tools like VSCode.


Connect directly to an SSH-enabled session
------------------------------------------

You can use the Renku CLI to open an SSH connection for you. This will open a
terminal in your RenkuLab Project session environment.

The :meth:`renku session open <renku.ui.cli.session>` command uses the ``id`` of
the session, which is printed when the session is started by :meth:`renku session start <renku.ui.cli.session>`
. In the example above, the SSH connection name is
``renkulab.io-myproject-02a9e407``, so the commmand to open the SSH session is:

.. code-block:: console

    $ renku session open --ssh user-myproject-02a9e407
    venv ▶ ~ ▶ $

(If prompted to accept host keys, confirm with ``yes``).

To exit the SSH shell, simply type ``exit``.


Open an SSH session in VSCode
------------------------------

To open Renku Sessions in VSCode, you need to have the `Remote - SSH Extension <https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-ssh>`_
installed.

Once you run :meth:`renku session start <renku.ui.cli.session>` as mentioned
above, the created SSH connection should show up in VSCode.

Open the ``Remote Explorer`` in the left bar and pick the SSH connection for the session,
e.g. ``renkulab.io-myproject-02a9e407``. This should open the remote project in
your VSCode.

.. warning::
    <screenshot of selecting session ID in VSCode>

For more details on the VSCode SSH extension see
`the official documentation <https://code.visualstudio.com/docs/remote/ssh>`_.
