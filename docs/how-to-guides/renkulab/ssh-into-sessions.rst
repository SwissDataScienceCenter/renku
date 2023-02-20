.. _ssh_into_sessions:

SSH into RenkuLab Sessions
==========================

Would you prefer to work in a RenkuLab session from your local environment? Do
you prefer to edit code in your own IDE, rather than in a browser?

You can use the SSH feature of RenkuLab sessions to connect directly to the
remote session environment and work on it from your local machine. You can even
open the session with your favorite tools, such as VSCode.

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
template, go to the project "Overview" tab and open the "Status" section. Find
the "Template Version" section, and click "Update".

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

Connecting to RenkuLab sessions via SSH requires 3 prerequisites to be setup on
your local machine (where you want to SSH from):

* OpenSSH is installed
* the Renku CLI is installed, with version >= <X>
* your Renku project is cloned

This section will guide you through setting up these prerequisites.

    .. warning::
        <Enter version number above 1x>

#.  To use the SSH feature, you need to have OpenSSH installed on your system.

    .. warning::
        <How to check is OpenSSH is installed?!>

#.  Install the Renku CLI version >= <X>.

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


Launch an SSH-enabled Session
-----------------------------

#.  Navigate so your current working directory is the project you want to start
    a session in:

    .. code-block:: console
    
        $ cd my-project


#.  Log in to RenkuLab with :meth:`renku login <renku.ui.cli.login>`: 

    .. code-block:: console

        $ renku login renkulab.io


#.  Pull any changes. This makes sure that the session you start is for the
    latest version of your project.

    .. code-block:: console

        $ git pull


#.  Start a session with :meth:`renku session start <renku.ui.cli.session>`,
    using the ``--ssh`` flag and ``-p renkulab`` to specify running the session
    remotely on RenkuLab:

    .. code-block:: console

        $ renku session start -p renkulab --ssh
        [...]
        SSH connection successfully configured, use 'ssh renkulab.io-myproject-02a9e407' to connect.
        Session user-myproject-02a9e407 successfully started
    
    .. note::

        **Curious what's happening under the hood?** This command starts a new
        session on RenkuLab. But first, it adds your local SSH keys to the
        ``allowed_keys`` in the project and pushes those changes to RenkuLab. If
        this is your first time using the SSH feature on RenkuLab, this command
        creates an SSH keypair for you. Once the session is started, it creates
        an SSH connection entry in your local SSH config for that session id.
        This SSH config entry can be used with your SSH client or tools like
        VSCode.


Open an SSH Session via a Shell
-------------------------------

You can use :meth:`renku session open <renku.ui.cli.session>` to open an SSH
connection directly. This will open a terminal in your RenkuLab Project session
environment.

.. code-block:: console

    $ renku session open --ssh <session-id>
    venv ▶ ~ ▶ $

(If prompted to accept host keys, confirm with ``yes``).

To exit the SSH shell, simply type ``exit``.

.. note::

    **How do I find my Session ID?** The ``id`` of the session is
    printed when the session is started by :meth:`renku session start
    <renku.ui.cli.session>` .
    
    In the example above, the session id is ``user-myproject-02a9e407``, so the
    commmand to open the SSH session is: ``renku session open --ssh
    user-myproject-02a9e407``.
    
    If you need to find your session id again, use
    :meth:`renku session ls <renku.ui.cli.session>`.

.. note::

    **Can I use normal the** ``ssh`` **command?** Yes! If you prefer, you can
    use the ``ssh`` command directly rather than the Renku CLI. The ``ssh``
    command is printed upon starting an ssh session.

        .. code-block:: console

            $ renku session start -p renkulab --ssh
            [...]
            SSH connection successfully configured, use 'ssh renkulab.io-myproject-02a9e407' to connect.


Open an SSH Session in VSCode
------------------------------

After starting an SSH session, follow these steps to open your session in
VSCode:

#.  Install the `Remote - SSH Extension <https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-ssh>`_
    in VSCode if you don't have it already.

#.  In VSCode, open the "Remote Explorer" in the left bar and pick the SSH
    connection for the session, e.g. ``renkulab.io-myproject-02a9e407``.

#.  Open the "Explorer" in the left bar, and select "Open Folder". Enter
    ``/home/jovyan/work``.

You can now browse and edit your Renku Project files in VSCode, and run commands
in the RenkuLab session via the VSCode terminal!

 .. warning::

    <screenshot of selecting session ID in VSCode>

For more details on the VSCode SSH extension see
`the official documentation <https://code.visualstudio.com/docs/remote/ssh>`_.
