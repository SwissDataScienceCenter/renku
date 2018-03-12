.. _first_steps:

First Steps
===========

.. meta::
    :description: First steps with Renga
    :keywords: hello world, first steps, starter, primer

Interaction with the platform takes place via the Python-based
command-line interface (CLI) and the Python API. You can get both via pip:

.. code-block:: console

   $ pip install -e git+https://github.com/SwissDataScienceCenter/
   renga-python.git@development#egg=renga

.. note::

   We recommend using `virtualenv
   <https://virtualenv.pypa.io/en/stable/>`_ when installing the Renga
   package.


Our first Renga project
---------------------------

First, create a project directory:

.. code-block:: console

    $ mkdir -p ~/renga-projects/myproject
    $ cd ~/renga-projects/myproject

Initialize the project as a Renga project:

.. code-block:: console

    $ renga init

This command created a git repository for your project and an additional
`.renga` sub-directory:

.. code-block:: console

    $ tree -L 1 .renga
    .renga
    └── metadata.yml

Create a dataset and import data
--------------------------------

Creating datasets is useful to group pieces of data together for e.g. sharing
or publication.

.. code-block:: console

    $ renga dataset create mydataset
    $ tree data
    data
    └── mydataset
        └── metadata.yml

At this point, our dataset just consists of metadata in JSON-LD format:

.. code-block:: console

    $ cat data/mydataset/metadata.yml
    '@context':
      added: http://schema.org/dateCreated
      affiliation: scoro:affiliate
      authors:
        '@container': '@list'
      created: http://schema.org/dateCreated
      dcterms: http://purl.org/dc/terms/
      dctypes: http://purl.org/dc/dcmitypes/
      email: dcterms:email
      files:
        '@container': '@index'
      foaf: http://xmlns.com/foaf/0.1/
      identifier:
        '@id': dctypes:Dataset
        '@type': '@id'
      name: dcterms:name
      prov: http://www.w3.org/ns/prov#
      scoro: http://purl.org/spar/scoro/
      url: http://schema.org/url
    '@type': dctypes:Dataset
    authors:
    - '@type': dcterms:creator
      affiliation: null
      email: roskarr@ethz.ch
      name: Rok Roskar
    created: 2018-03-11 22:23:02.409684
    files: {}
    identifier: ae503fdf-40ff-419b-8356-131747c22187
    name: mydataset

We can import data from a variety of sources: local directories, remote URLs,
local or remote git repositories or other renga project. Here, we will import the
`README` file of this repo from the web:

.. code-block:: console

    $ renga dataset add mydataset https://raw.githubusercontent.com/
    SwissDataScienceCenter/renga-python/development/README.rst

Until now, we have created a Renga project and populated it with a dataset and
some data. Next, we will see how to use Renga to create a repeatable workflow.


Running a reproducible analysis
-------------------------------

For the purpose of the tutorial, we will count the number of lines the words
"science" and "renga" appear on in our `README` document by using standard
UNIX commands `grep` and `wc`.

First, get all occurrences of "science" and "renga":

.. code-block:: console

    $ renga run grep -i science data/mydataset/README.rst > readme_science
    $ renga run grep -i renga data/mydataset/README.rst > readme_renga

Now, combine these intermediate outputs into our final calculation:

.. code-block:: console

    $ renga wc readme_science readme_renga > wc.out

For each of our invocations of `renga run`, Renga recorded the command we
executed into a `Common Workflow Language <http://www.commonwl.org/>`_ (CWL)
step. Renga uses this information to keep track of the lineage of data. For
example, we can see the full lineage of `wc.out` using the `renga log`
command:

.. code-block:: console

    $ renga log wc.out
    *  c53dbfa0 wc.out
    *    c53dbfa0 .renga/workflow/80a3f98ede2346f6bc686200016b17d6_wc.cwl
    |\
    * |  18bb2c64 readme_science
    * |  18bb2c64 .renga/workflow/edb4c0b1b4b44d2fb2aff45a8960f905_grep.cwl
    | *  faa4f82a readme_renga
    | *  faa4f82a .renga/workflow/3b454003c5884ee8b5b8a943665447fe_grep.cwl
    |/
    @  c7b5f922 data/mydataset/README.rst


This sequence represents the basic building blocks of a reproducible
scientific analysis workflow enabled by Renga. Each component of the workflow
we produced is bundled with rich metadata that allows us to continue to track
its lineage and therefore to reuse it as a building block in other projects
and workflows.

