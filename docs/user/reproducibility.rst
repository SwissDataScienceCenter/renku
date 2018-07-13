.. _reproducibility:

Reproducibility
===============

First and foremost, we have designed Renku to enable reproducible data
science. In addition, Renku enables you to share pieces of your analysis
process with others at any time, and conversely, to reuse the work of others.
We encourage such cooperation by recording use of both data and code so that
the lineage and provenance of both can be uniquely determined.


In a typical data analysis or modeling project, these types of questions arise
very frequently:

* How exactly was this model or result obtained? How was the raw data pre-processed?
* Who is using my data, model, or result and how?
* What does this new data mean for our last month's report?
* How can I share my latest analysis with a colleague in the cloud?
* How can I make sure that our colleagues in another team can reproduce our results exactly?

Answering these questions in a reliable fashion is difficult. Renku provides
the tools that substantially reduce the effort required on the part of the
data scientist who wants to keep her work reproducible, reliable and robust.
Here, we introduce the essential concepts underlying the design and
implementation of Renku.

Three important concepts intertwine in Renku to enable reproducible data science.
These are **lineage**, **version control** and **containerization**. We discuss
each of these in turn below.

.. _reproducibility-lineage:

Lineage of results
------------------

One of the most important ideas behind Renku is the concept of capturing the
**lineage** of the analysis process. Lets assume we are working with ``input
data``, ``code``, and ``results``:

.. graphviz::
    :align: center

    graph foo {
        rankdir="LR"
        nodesep=0.01
        node [fontname="Raleway"]
        _results [color="white", label="", image="../_static/icons/scatter_plot.svg"]
        results [color="white"]
        _code [color="white", label="", image="../_static/icons/electronics.svg"]
        code [color="white"]
        data [color="white", label="", image="../_static/icons/data_sheet.svg"]
        "input data" [color="white"]


        _results--results [color="white"]
        _code--code [color="white"]
        data--"input data" [color="white"]
    }

If you write a piece of code that takes some input data, processes it and
writes some output to disk, the **lineage graph** would look something like
this:

.. graphviz::
    :align: center

    digraph foo {
        rankdir="LR"
        edge [fontname="Raleway"]
        data [color="white", label="", image="../_static/icons/data_sheet.svg"]
        code [color="white", label="", image="../_static/icons/electronics.svg"]
        results [color="white", label="", image="../_static/icons/scatter_plot.svg"]
        data->code [label="used by"]
        code->results [label="generated"]
    }


Naturally, a ``result`` may also be used as ``input data`` to a subsequent step:

.. graphviz::
    :align: center

    digraph foo {
        edge [fontname="Raleway"]
        rankdir="LR"
        data [color="white", label="", image="../_static/icons/data_sheet.svg"]
        data2 [color="white", label="", image="../_static/icons/data_sheet2.svg"]
        code [color="white", label="", image="../_static/icons/electronics.svg"]
        code2 [color="white", label="", image="../_static/icons/electronics2.svg"]
        results [color="white", label="", image="../_static/icons/scatter_plot.svg"]
        results2 [color="white", label="", image="../_static/icons/scatter_plot2.svg"]
        data->code
        code->results
        results->code2
        data2->code2
        code2->results2
    }

In a real analysis, such a graph may become very complex. Without a detailed
record of the connections between the different data, code and result blocks,
it may be impossible to efficiently regenerate parts of the chain. Keeping
track of the lineage now allows us to easily recreate the final result if the
original raw data changes, for example, or to examine what happens when we
change our preprocessing pipeline. Recording lineage is also critical for
enabling data and code audits, should they be required.

We hope that using Renku will encourage people to share their data, results,
and analysis codes. By capturing the lineage not only within, but also *across*
projects we ensure that if you use someone else's results you can always
track exactly where they came from. Conversely, you can also see how someone
is using your shared data or code in their analysis. Renku will allow you to
explore these connections in detail.


.. _reproducibility-version_control:

Version control
---------------

Data and code change frequently in a typical project. Knowing which *exact*
version of code and data produced a particular result is critical for ensuring
the robustness and veracity of your work. In Renku, version control is the
base upon which everything else is built.

We rely on the currently most widespread version control system, `git <https
://git-scm.com/>`_. If you are unfamiliar with ``git`` it wouldn't hurt to
read at least some of their `excellent tutorials <https://git-
scm.com/docs/gittutorial>`_. In Renku we try to take care of most of the
boiler plate ``git`` commands for you, but you should still be aware that it
is being used under the hood.

The added benefit of using a version control system like ``git`` is that it also
automatically encourages you to be creative, explore new ideas, and break things.
"Branching" is extremely light-weight in ``git`` and allows you to freely
experiment with complete peace-of-mind that you can always simply restore your
last sane version of your work if everything happens to go off the rails. This
is a fantastic advantage in a data science process, where experimentation is
a critical part of the discovery process.


.. _reproducibility-containerization:

Containerization
----------------

Knowing how we converted data into actionable results by recording lineage and
keeping track of versioning gets us most of the way to being able to fully
reproducing an analysis workflow. A final piece is encapsulating the actual
computational environment. In a quantitative sense, using different releases
of the same library can simple lead to different results. A more practical
aspect, however, is that replicating a computational environment for reproducibility's
sake is often simply very time consuming.

"Containerization" can help with both of these problems. A "container" is in
essence a process running in a fully specified environment, including the
operating system and all dependencies that are needed for a code to run. The
most popular (but certainly not the only) containerization framework is
`Docker <https://www.docker.com/>`_ and we  make use of it extensively for all
parts of Renku. In terms of user workflows, we try to do as much of the
boilerplate for you as possible so for the simpler tasks you don't really
need to worry about the fact that your code is executing in Docker containers.
For more complex scenarios, some familiarity with Docker will be required.


.. _reproducibility-further_reading:

Further Reading
---------------

Renku helps you achieve the goal of fully reproducible data science by bundling together several technologies:


* for keeping track of the lineage, our CLI relies heavily on the Common Workflow Language. `Here <cwl.html>`_ you can learn more about our CWL integration
* for version control, we rely on `git <https://git-scm.com/>`_
* your project includes a container specification from day 1. Check out these `docs <ci.html>`_ to learn more about how we build images for your project and what we do with them 

