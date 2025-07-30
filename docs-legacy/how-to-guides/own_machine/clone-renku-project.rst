.. _clone_renku_project:

Clone a Renku project
=====================

In order to save your work back to RenkuLab from your own machine, you'll need to authenticate with RenkuLab.
You can do this easily using the ``renku login`` command.

However, if you find yourself needing to authenticate via an SSH Key, you can find those instructions below as well.

.. tab-set::

    .. tab-item:: Authenticate via renku login

        To authenticate with Renku via a browser, you can use :meth:`renku login <renku.ui.cli.login>`.

        Provide the url of the RenkuLab instance where your project is located in the following command:

        .. code-block:: console

            $ renku login <url of your renkulab instance>

        For example, if your project is on https://renkulab.io , enter:

        .. code-block:: console

            $ renku login renkulab.io

        This will open RenkuLab in a browser window, if possible, where you can enter your credentials.
        If it is not possible to open a browser (e.g. because you are logged in to a remote server) you can copy/paste the URL it displays to complete the login.
        Then, the local Renku CLI receives and stores a secure token that will be used for future authentications.

        **Clone your Renku Project**

        #. Back on renkulab.io_, on your Renku project's page, click “Settings”.
        #. Under "Clone commands" copy the :meth:`renku clone <renku.ui.cli.clone>` command.
        #. On your machine’s terminal, navigate to where you want your project to be located.
        #. Paste and run the clone command you copied.

    .. tab-item:: Authenticate via SSH Key

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

        1. Copy the contents of your public key file.

        You can do this manually or use a script.
        For example, to copy an ED25519 key to the clipboard
        (Replace ``id_ed25519.pub`` with your filename. For example, use ``id_rsa.pub`` for RSA).

            .. tab-set::

                .. tab-item:: macOS

                    .. code-block:: console

                        $ tr -d '\n' < ~/.ssh/id_ed25519.pub | pbcopy


                .. tab-item:: Linux

                    Note: This requires the ``xclip`` package

                    .. code-block:: console

                        $ xclip -sel clip < ~/.ssh/id_ed25519.pub

                .. tab-item:: Git Bash on Windows

                    .. code-block:: console

                        $ cat ~/.ssh/id_ed25519.pub | clip

        2. Go to https://renkulab.io/gitlab/-/profile/keys. (You can get here by going to https://renkulab.io/gitlab, then in the top right corner, select your avatar ``> Preferences > SSH Keys``)

        3. In the "Key" box, paste the contents of your public key. If you manually copied the key, make sure you copy the entire key, which starts with ``ssh-ed25519`` or ``ssh-rsa``, and may end with a comment.

        4. In the "Title" box, type a description, like "Work Laptop" or "Home Workstation".

        5. `Optional:` In the "Expires at" box, select an expiration date.

        6. Click "Add key".


        **Clone your Renku Project**

        #. Back on renkulab.io_, on your Renku project's page, click “Settings”.
        #. Under "Clone commands" and "Repository URL" copy the **SSH** url.
        #. On your machine’s terminal, navigate to where you want your project to be located.
        #. Run ``git clone <url>``.


.. _renkulab.io: https://renkulab.io
