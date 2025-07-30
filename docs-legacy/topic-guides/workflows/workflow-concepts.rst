

Renku Workflows: Concepts
=========================

One of the most important ideas behind Renku is the concept of capturing the
**provenance** of the analysis process. Lets assume we are working with ``input
data``, ``code``, and ``results``:

.. graphviz::
    :align: center

    graph foo {
        rankdir="LR"
        nodesep=0.01
        node [fontname="Raleway"]
        _results [color="white", label="", image="../../_static/icons/scatter_plot.svg"]
        results [color="white"]
        _code [color="white", label="", image="../../_static/icons/electronics.svg"]
        code [color="white"]
        data [color="white", label="", image="../../_static/icons/data_sheet.svg"]
        "input data" [color="white"]


        _results--results [color="white"]
        _code--code [color="white"]
        data--"input data" [color="white"]
    }

If you write a piece of code that takes some input data, processes it and
writes some output to disk, the **provenance graph** would look something like
this:

.. graphviz::
    :align: center

    digraph foo {
        rankdir="LR"
        edge [fontname="Raleway"]
        data [color="white", label="", image="../../_static/icons/data_sheet.svg"]
        code [color="white", label="", image="../../_static/icons/electronics.svg"]
        results [color="white", label="", image="../../_static/icons/scatter_plot.svg"]
        data->code [label="used by"]
        code->results [label="generated"]
    }


Naturally, a ``result`` may also be used as ``input data`` to a subsequent step:

.. graphviz::
    :align: center

    digraph foo {
        edge [fontname="Raleway"]
        rankdir="LR"
        data [color="white", label="", image="../../_static/icons/data_sheet.svg"]
        data2 [color="white", label="", image="../../_static/icons/data_sheet2.svg"]
        code [color="white", label="", image="../../_static/icons/electronics.svg"]
        code2 [color="white", label="", image="../../_static/icons/electronics2.svg"]
        results [color="white", label="", image="../../_static/icons/scatter_plot.svg"]
        results2 [color="white", label="", image="../../_static/icons/scatter_plot2.svg"]
        data->code
        code->results
        results->code2
        data2->code2
        code2->results2
    }

In Renku, we provide tools for building such `workflows` to record and show how
data and code are connected. By encoding these relationships, your project is
easier for you to manage and faster for others to read and reuse! No more
reading through multiple files to understand how they are connected - workflows
make the connections between code and data files easy to understand by listing
each workflow step and its inputs and outputs.

Each time you track an execution with Renku, you create a workflow step.
Encoding a workflow step makes it easier for you to rerun it without retyping
long commands. Recording workflow steps in Renku also records metadata that you
and others can use to understand how an output was generated.

To take full advantage of workflows, join individual steps together into
multi-step workflows. When your code pipeline is encoded as a workflow, you can
easily re-run all or portions of your workflow with simple commands, test your
code with different parameters and compare the results, or send it to different
execution backends.
