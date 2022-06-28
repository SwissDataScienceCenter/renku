.. _clone_renku_project:

Clone a Renku project
=====================

In order to save your work back to RenkuLab from your own machine, you need to authenticate with RenkuLab.

If you're working with Renku on a machine with a browser, like your laptop,
then Renku provides a shortcut for authenticating with RenkuLab.

If you're working on a remote machine that you connected to via SSH, then you'll need to set up an SSH key.

Choose which situation you're working in:

.. tabbed:: Authenticate via the browser

    To authenticate with Renku on a local machine where you have access to a browser, you can use :meth:`renku login <renku.ui.cli.login>`.

    Provide the url of the RenkuLab instance where your project is located in the following command:

    .. code-block:: console

        $ renku login <url of your renkulab instance>

    For example, if your project is on https://renkulab.io , enter:

    .. code-block:: console

        $ renku login renkulab.io

    This will open RenkuLab in a browser window, where you can enter your credentials.
    Then, the local Renku CLI receives and stores a secure token that will be used for future authentications.

    **Clone your Renku Project**

    #. Back on renkulab.io_, on your Renku project's page, click “Settings”.
    #. Under "Clone commands" copy the :meth:`renku clone <renku.ui.cli.clone>` command.
    #. On your machine’s terminal, navigate to where you want your project to be located.
    #. Paste and run the clone command you copied.

.. tabbed:: Authenticate via SSH Key

    To authenticate with RenkuLab from a remote machine,
    you'll need to create an SSH key and save that key in your Renku GitLab account.

    If you already have an SSH key in your RenkuLab GitLab profile, then skip down to "Clone your Renku Project" below.
    Otherwise, follow along to set up a key.

    **Create an SSH Key**

    If you do not have an existing SSH key pair, generate a new one.

    #. Open a terminal on the machine where you'd like to run your Renku project.

    #. Create an ssh key:

       .. code-block:: shell-session

         $ ssh-keygen -t ed25519 -C "<comment>"

       For the comment, you may specify an email address.


    **Add your SSH key to RenkuLab GitLab**

    #. Copy the contents of your public key file.

       You can do this manually or use a script.
       For example, to copy an ED25519 key to the clipboard
       (Replace ``id_ed25519.pub`` with your filename. For example, use ``id_rsa.pub`` for RSA).

        .. tabbed:: macOS

            .. code-block:: console

                $ tr -d '\n' < ~/.ssh/id_ed25519.pub | pbcopy


        .. tabbed:: Linux

            Note: This requires the ``xclip`` package

            .. code-block:: console

                $ xclip -sel clip < ~/.ssh/id_ed25519.pub

        .. tabbed:: Git Bash on Windows

            .. code-block:: console

                $ cat ~/.ssh/id_ed25519.pub | clip


    #. Go to https://renkulab.io/gitlab/-/profile/keys .

       (You can get here by going to https://renkulab.io/gitlab , then in the top right corner, select your avatar > Preferences > SSH Keys)

    #. In the "Key" box, paste the contents of your public key.
       If you manually copied the key, make sure you copy the entire key,
       which starts with ``ssh-ed25519`` or ``ssh-rsa``, and may end with a comment.


    #. In the "Title" box, type a description, like "Work Laptop" or "Home Workstation".


    #. `Optional:` In the "Expires at" box, select an expiration date.


    #. Click "Add key".


    **Clone your Renku Project**

    #. Back on renkulab.io_, on your Renku project's page, click “Settings”.
    #. Under "Clone commands" and "Repository URL" copy the **SSH** url.
    #. On your machine’s terminal, navigate to where you want your project to be located.
    #. Run ``git clone <url>``.


.. _renkulab.io: https://renkulab.io
