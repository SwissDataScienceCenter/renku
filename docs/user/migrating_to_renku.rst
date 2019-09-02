.. _migrating_to_renku:

Migrating to Renku
==================

So, at this point you've gone through :ref:`first_steps` and learned how Renku can
help your research be more reproducible and collaborative. The following guide
will help you migrate your existing projects into the Renku platform.

.. _practical_renku_usage:

Practical Renku Usage & Migration Tips
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In addition to supplementing your research with reproducibility bits,
Renku aims to help you use current best practices for data science workflows in
an unobtrusive way. Therefore, when you initialize a new Renku project in either
the commandline interface or through the web platform, a template set of directories
and files will be created for you. These files deal with setting up ``renku``,
``docker``, software dependencies, and organizational structure parts of your
project. For the full specification of files and directories present in the
provided templates read the :ref:`templates` documentation.

Note that although Renku provides this default template, it is fully customizable.
As long as you keep ``.renku``, you should be able to modify this filesystem to
fit your own project structure. You can also create renku projects that serve as
templates that you can fork and use for other projects.

.. _migration_first_steps:

Create an account on Renku & set up GitLab SSH key
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In order to upload your project into the Renku web platform, you will need an
account on https://renkulab.io. There are several login options; pick the one
most convenient for you.

When you create a Renku account, a GitLab account gets created automatically for
you. On this GitLab account (https://renkulab.io/gitlab), you will need to set
up an SSH key to be able to clone and push code from your local machine. To do
this:

* visit http://renkulab.io/gitlab (while logged into renkulab.io)
* click the icon in the top righthand corner
* click **Settings** from the dropdown
* in the lefthand column, click **SSH Keys**
* follow the instructions on this page

Renku-ize your project with Renku CLI
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The following steps for Renku-izing your project require
that you have set up the Renku commandline interface (CLI) on your local machine.
If you haven't already, you can find the quick installation instructions
`here <https://renku-python.readthedocs.io/en/latest/index.html>`_.

If your project is not yet under version control (i.e. you haven't called
``git init``), you can do the following (replace ``my_project`` with the name of
your existing project)::

  $ cd /path/to/my_project
  $ renku init

If your project is *already* under version control, first make sure your
``git status`` is clean, and inside the top level of your project run::

  $ renku init --force

As noted in the above section on :ref:`practical_renku_usage`, when you
``renku init``'d, a number of directories, files, and dotfiles were added and
committed in the top level of your directory.

Now your project is ready to be sent to the web platform!

Send your project to Renku web
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Sending your project to Renku's GitLab instance boils down to adding the right
remote and pushing the code. Perform the following, replacing ``<namespace>`` with
the username that appears on the renkulab-generated GitLab instance (unless you
are pushing to a group), and ``<project-name>`` with the name you are giving your project.

If your project doesn't have a remote yet (check ``git remote``), you can use ``origin``::

  $ git remote add origin git@renkulab.io/<namespace>/<project-name>.git
  $ git push origin master

If there is already a remote::

  $ git remote add renku git@renkulab.io/<namespace>/<project-name>.git
  $ git push renku master

View your project on Renku web
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If everything went smoothly, you should be able to view your project at
https://renkulab.io/gitlab/<namespace>/<project-name>. When you push code to this
repo, the CI/CD pipeline will run according to settings in the generated ``gitlab-ci.yml``
file, rebuilding your docker image as specified by the generated ``Dockerfile``.
If your build fails, see :ref:`advanced_interfaces`.

.. warning::

  If you already had a ``Dockerfile`` and/or ``gitlab-ci.yml`` file, keep a close
  eye on the logs to make sure you're getting expected behavior.

Your project should also now appear in *Your Projects* list on the front page
of your logged-in Renku home page and the **Projects** tab from the top of the
page.


Utilize Renku-web features
""""""""""""""""""""""""""

At this point you can also continue development as usual by starting up a Jupyterlab
or RStudio session from the Renku web platform (if you don't remember how to do
this, you can check out :ref:`jupyterlab`).

To make full use of the Renku reproducibility features, you will want to check
back to the tutorial for how to :ref:`add_data`, and ``renku run`` and
``renku rerun`` :ref:`create_workflow`.

For Renku collaboration features, you can check out :ref:`collaborating`.
