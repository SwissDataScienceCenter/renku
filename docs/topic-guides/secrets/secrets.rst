.. _secrets:

Secrets in RenkuLab
===================

What are secrets?
-----------------

Secrets are sensitive data that you need in sessions but should
not be stored in a repository.
This includes passwords you might need for accessing a database, API keys
for external services, or any other sensitive information.

You can create, replace, and delete secrets from the RenkuLab interface.
They are securely stored in our systems and are only available in sessions
for your user. They will be mounted as files in the session container in
a path you can customize when starting a new session.
Each secret is stored with a unique name, which will be used for the
corresponding file name.

Keep in mind that secrets are currently defined per user. They cannot be
shared with other users, nor scoped down to a single project. You can select
which secrets to use in a session when starting it, so you don't need
to worry about accidentally mounting secrets into a session that you
do not need.

Add and change secrets
----------------------

To add a new secret, go to the User Secrets page in the RenkuLab interface.
You can find it in the user settings menu on the top right.

.. image:: ../../_static/images/secrets_page.png
  :width: 85%
  :align: center
  :alt: User Secrets page

Click on the ``Add New Secret`` button and fill in the Name and Value fields.

The name is a unique identifier for the secret, used for the file name in
sessions. It cannot be empty and the name follows specific validation rules:
you can include only letters, numbers, underscores (_), and dashes (-).

Values can be any non-empty string, including special characters. The length
cannot exceed 5'000 characters. Should you need to store a longer value,
consider splitting it into multiple secrets.

.. image:: ../../_static/images/secrets_add_new.png
  :width: 85%
  :align: center
  :alt: Add a new secret

Once you add a secret, you cannot visualize its value again for security
reasons. You can still change it by clicking on the ``Replace`` button,
or remove it by clicking on the ``Delete`` button. The name cannot be changed;
should you need to rename a secret, please delete it and create a new one
you the new name.

Use secrets in sessions
-----------------------

If you need to include secrets in a session, you need to click on the Start
dropdown menu and select ``Start with options``. Quick-start sessions do not
support secrets.

Once on the "Start Session" page, you can select the secrets you want to
include from the ``User Secrets`` section towards the bottom of the page.
Click on the chevron on the right to expand the secrets list and click on
every secret you want to include. You can customize the path where the
secrets will be mounted in the session container by adjusting the
``Mount path`` input. The default path is ``/secrets``.

.. image:: ../../_static/images/secrets_selection.png
  :width: 85%
  :align: center
  :alt: Select secrets to mount in a new session

Click on the ``Start Session`` button to start the session with the selected
secrets. You can now access the secrets in the session container at the
specified path. The secrets will be stored in files with the same name.

.. note::

  Secrets will be mounted with the value stored at the session start time.
  If you change the value of a secret after starting the session, you will
  need to restart the session to apply the changes.

