.. _start_renku_session:

Start a Renku session
---------------------

#. **Navigate into your renku project directory:**

   .. code-block:: shell-session

       $ cd <project_name>


#. **Start a renku session:**

   .. code-block:: shell-session

       $ renku session start
       ID          STATUS    URL
       ----------  --------  ------------------------------------------------------------
       f1693c198e  running   http://0.0.0.0:56674/?token=910ca732ef574049a22d41d0f1109f56

   .. note::

       Note: Renku may inform you that the container image for the current commit
       does not yet exist on your machine, and renku will offer to build it for you. Say yes!

#. **Open the session in your browser.**
   When the session starts, it will print out a url where the session is running.
   Copy this url into your browser to access your Renku project running inside it's containerized environment.

   If you need to find this url again later, you can find all running Renku sessions by running `renku session list`.

   .. code-block:: shell-session

       $ renku session list
       ID          STATUS    URL
       ----------  --------  ------------------------------------------------------------
       f1693c198e  running   http://0.0.0.0:56674/?token=910ca732ef574049a22d41d0f1109f56

   You can also use the session ID to call `renku session open <ID>`, which opens your browser window for you.

   .. code-block:: shell-session

       $ renku session open <session_id>

.. note::

    **Looking for a shell?**

    Would you like to enter your containerized project environment on a shell, rather than via the browser?

    Since Renku uses Docker to manage your project's computational environment, you can use Docker commands to enter the
    container directly and use the shell.


    First, find your renku session's container ID by listing your running sessions:

    .. code-block:: shell-session

       $ renku session list
       ID          STATUS    URL
       ----------  --------  ------------------------------------------------------------
       f1693c198e  running   http://0.0.0.0:56674/?token=910ca732ef574049a22d41d0f1109f56


    Note the value in the ID field.

    Then, open a shell in a running container by providing the Container ID:

    .. code-block:: console

        $ docker exec -it <ID> /bin/bash
        base ▶ ~ ▶ work ❯ project_name ▶ master ▶ $ ▶
