.. _doing_data_science:

Ways to do data science in Renku
--------------------------------

.. tab-set::

    .. tab-item:: Jupyter Notebooks

        This is the main way Python and Julia users will interactively do data science.
        In the JupyterLab interface, use the file navigator in the left sidebar to go
        to the **Files** (1) tab. Here, you see a listing of files and folders from
        your project. To create a new notebook, first double-click on the **notebooks**
        folder (2), then on the **'+'** button (3). Select *Python 3* or
        *Julia 1.\*.\** to create a new notebook (4).

        .. image:: ../../_static/images/jupyterlab-files-notebooks.png
            :width: 85%
            :align: center
            :alt: Files tab and notebooks folder in JupyterLab

        To rename the notebook, right-click on its name (``Untitled.ipynb``) and
        select rename.

        .. image:: ../../_static/images/jupyterlab-rename.png
            :width: 85%
            :align: center
            :alt: Rename notebook in JupyterLab

        You can read more about JupyterLab in the `JupyterLab documentation
        <https://jupyterlab.readthedocs.io/en/latest/>`_ if you are not familiar with it
        already. You may want to take some time to play with the JupyterLab interface
        before continuing.

        To quickly save your work, the easiest is to use the ``renku save`` command from
        the terminal - this will commit any uncommitted files and sync the changes with
        the git server. You can specify a custom commit message with ``-m``.

        .. code-block:: console

            $ renku save -m "saving the new notebook"

            Successfully saved to branch master:
                notebooks/new notebook.ipynb
            OK

        Alternatively, if you want more control over the commit process, you can go to the console and use
        ``git`` to add your work to the repository. For example, if you want to keep
        the new notebook(s), run the following in the terminal:

        .. code-block:: console

            # track everything inside the notebooks folder
            $ git add notebooks
            $ git commit -m "Added some notebooks"
            $ git push

            [master 0fb9ac1] Added some notebooks
                1 file changed, 32 insertions(+)
                create mode 100644 notebooks/MyNewNotebook.ipynb
            Counting objects: 4, done.
            Delta compression using up to 8 threads.
            Compressing objects: 100% (4/4), done.
            Writing objects: 100% (3/3), 639 bytes | 639.00 KiB/s, done.
            Total 4 (delta 1), reused 0 (delta 0)
            To https://dev.renku.ch/gitlab/john.doe/flights-tutorial.git
                c1dcfe4..0fb9ac1  master -> master

        If you prefer to use a GUI, an extension providing access to the basic git
        commands is built into JupyterLab. Click on the git icon on the left sidebar of
        JupyterLab (1) to open the git panel. Here, you can add untracked files by
        selecting them and clicking the up arrow (2). You can enter a commit message in
        (3) and click the ``Commit`` button. Finally, sync your changes with the server
        by clicking on the push icon (4).

        .. image:: ../../_static/images/jupyterlab-git-panel.png
            :width: 85%
            :align: center
            :alt: Commit notebook in JupyterLab

    .. tab-item:: RStudio

        The canonical way of working interactively with R is using the RStudio IDE.
        As default, there are four panels available in the user interface, which cover a
        code editor (1), Console/Terminal section (2), Environment panel (3) and
        a file explorer (4).

        .. image:: ../../_static/images/rstudio-ui.png
            :width: 100%
            :align: center
            :alt: RStudio User Interface

        We note that it is actually possible to run Jupyter Notebooks with an R kernel,
        if one navigates to the */lab* endpoint of the URL of the renku project. There
        you can select the R kernel and proceed as usual. However, we proceed with
        this part of the tutorial in RStudio.

        The R console and script editor can be used together to quickly prototype
        data pipelines. An advantage of RStudio is that the Environment panel will show
        all the data frames and variables stored in local memory.

        In order keep R scripts organized, you may want to create a ``src`` folder in the
        main project directory.

        An important part of using renku within RStudio is accessing the Terminal (1). This
        is where you can access renku commands and make sure you have tracked changes
        using git. The Git tab in the Sessions panel (2) also gives you the option of a
        graphical user interface (GUI) to git.

        .. image:: ../../_static/images/rstudio-git-ui.png
            :width: 100%
            :align: center
            :alt: RStudio User Interface
