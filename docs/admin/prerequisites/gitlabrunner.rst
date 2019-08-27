.. _gitlabrunner:

Gitlab runner configuration
===========================

In order for Gitlab to build images that later will be used for launching the Jupyter Notebooks, you need to configure a node that runs a Gitlab runner.
These are the steps needed for setting up a Gitlab Runner. You can make your own variations of the configuration as you see best fit.

1. Install Docker and Gitlab runner in the VM
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: bash

   $ sudo yum update docker-ce --disableexcludes=main # In case you use a centos image with older version of docker
   $ curl -L https://packages.gitlab.com/install/repositories/runner/gitlab-runner/script.rpm.sh | sudo bash
   $ sudo yum install gitlab-runner

`Gitlab docs <https://docs.gitlab.com/runner/install/linux-repository.html>`_

2. Run Gitlab-runner
~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: bash

   $ sudo docker run -d --name gitlab-runner --restart always \
     -v /srv/gitlab-runner/config:/etc/gitlab-runner \
     -v /var/run/docker.sock:/var/run/docker.sock \
     gitlab/gitlab-runner:latest


3. Register the Gitlab runner with Gitlab in Renku
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

You can use shared runners.
Get the registration token from https://renku.mydomain.ch/gitlab/admin/runners

.. code-block:: bash

   $ sudo docker exec -ti gitlab-runner gitlab-runner \
     register -n -u https://<renku.mydomain.ch>/gitlab/ \
     --name gitlab/gitlab-runner -r  <TOKEN_FROM_GITLAB> \
     --executor docker --locked=false  --run-untagged=true \
     --docker-image="docker:stable"

4. Configure Gitlab runner to support more concurrent jobs
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- Login to the VM and edit the gitlab-runner config file.

.. code-block:: bash

   $ sudo vi /srv/gitlab-runner/config/config.toml

- Add the desired quantity of runners.

.. code-block:: bash

   concurrent = 10

5. (Optional) Configure a way to clean up disk
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Even if you provide sufficient storage space for the gitlab runner instance, you might need at some point to clean it up.

To cleanup non used disk space you can use an existing tool like `Gitlab Docker cleanup <https://gitlab.com/gitlab-org/gitlab-runner-docker-cleanup>`_ or run a cronjob that periodically prunes non-used images, volumes, containers and networks.
