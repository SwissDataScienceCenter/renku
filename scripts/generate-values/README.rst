.. _generating-renku-values:

Generating starting Renku values
================================

The Renku deployment requires a number of values to be configured. Here is a
script that makes this a bit easier: it generates the random secrets as well as
enters all the supporting service information where needed. These are the steps
it takes:

1. Generate random secrets
2. Prompt the user for infromation about GitLab
3. Render the template with the user values and generate a new values file

Setting up
----------

You need:

* python or docker
* a registered hostname for Reknu or a wildcard DNS
* Renku configured as a Gitlab OAuth application

This setup assumes that you have configured a hostname for your Renku instance
or that you have access to a wildcard DNS. A full list of prerequisites for a
fully-functioning Renku deployment can be found in the general Renku admin
documentation.

You need to first configure Renku as as an OAuth client to Gitlab, you
can do this under ``User Settings --> Applications`` in your Gitlab deployment. 
Configure the callback URLs:

.. code-block::

   https://<your-renku-dns>/login/redirect/gitlab
   https://<your-renku-dns>/api/auth/gitlab/token

And set the scopes:

.. code-block::

   api (Access the authenticated user's API)
   read_user (Read the authenticated user's personal information)
   read_repository (Allows read-access to the repository)
   read_registry (Grants permission to read container registry images)
   openid (Authenticate using OpenID Connect)

Keep this page open or note the application ID and secret to use it with the
values script.

Running the script
------------------

To run the script, you can set up a python virtualenv with the required packages
or use our docker image.

The script supports a number of different options: one options worth
noting is the `--output <filename>` option. It is
useful for separating the resulting secrets file from the program output.

Using python
^^^^^^^^^^^^

Make the virtual environment:

.. code-block::

   # if you don't have virtualenv yet
   $ pip install virtualenv
   $ virtualenv .venv
   $ source .venv/bin/activate
   $ pip install -r requirements.txt

Call the script:

.. code-block::

   $ ./generate-values.py

If you experience issues with the python script, we recommend you try the
docker-based approach below.

Using Docker
^^^^^^^^^^^^

To run it from a Docker container after you have built the container with `docker build`:

.. code-block::

   $ docker run --rm -ti -v ${PWD}:/work renku/generate-values

To run it from a Docker container with the resulting output should be written to a file called `renku-values.yaml`.

.. code-block::

   $ docker run --rm -ti -v ${PWD}:/work renku/generate-values --output /work/renku-values.yaml

Wrapping up
-----------

Check the generated values file and make sure you don't see anything missing or
odd. Now refer to the rest of the renku admin documentation for actually
deploying the platform.
