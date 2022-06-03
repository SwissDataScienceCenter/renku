.. _clone_renku_project:

Clone your Renku project
========================

In order to save your work back to RenkuLab from your own machine,
you'll need to create an SSH key and save that key in your Renku GitLab account.

If you already have an SSH key in your RenkuLab GitLab profile, then skip down to "Clone the Renku Project".
Otherwise, follow along to set up a key.

Enable remote access to your Renku Project via an SSH Key
---------------------------------------------------------

Create an SSH Key
~~~~~~~~~~~~~~~~~

If you do not have an existing SSH key pair, generate a new one.


#. Open a terminal on the machine where you'd like to run your Renku project.

#. Create an ssh key:

   .. code-block:: shell-session

     $ ssh-keygen -t ed25519 -C "<comment>"

   For the comment, you may specify an email address.


Add your SSH key to RenkuLab GitLab
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

#. Copy the contents of your public key file. You can do this manually or use a script.
   For example, to copy an ED25519 key to the clipboard
   (Replace ``id_ed25519.pub`` with your filename. For example, use ``id_rsa.pub`` for RSA).

    .. tabbed:: macOS

        .. code-block:: console

            $ tr -d '\n' < ~/.ssh/id_ed25519.pub | pbcopy


    .. tabbed:: Linux

        This requires the ``xclip`` package

        .. code-block:: console

            $ xclip -sel clip < ~/.ssh/id_ed25519.pub

    .. tabbed:: Git Bash on Windows

        .. code-block:: console

            $ cat ~/.ssh/id_ed25519.pub | clip


#. Go to https://renkulab.io/gitlab/-/profile/keys
   (You can get here by going to https://renkulab.io/gitlab , then in the top right corner, select your avatar > Preferences > SSH Keys)

#. In the Key box, paste the contents of your public key.
   If you manually copied the key, make sure you copy the entire key,
   which starts with ssh-ed25519 or ssh-rsa, and may end with a comment.


#. In the Title box, type a description, like Work Laptop or Home Workstation.


#. `Optional:` In the Expires at box, select an expiration date.


#. Select Add key.


Clone the Renku Project
-----------------------

#. Back on renkulab.io_, on your Renku project's page, click “View in GitLab”.
#. Click “Clone”, and then “Clone with SSH”.
#. On your machine’s terminal, navigate to where you want your project to be located.
#. Run ``git clone <the clone url you copied>``


.. _renkulab.io: https://renkulab.io
