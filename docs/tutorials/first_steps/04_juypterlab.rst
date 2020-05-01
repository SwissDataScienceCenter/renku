.. _jupyterlab:

Use Renku from within JupyterLab
--------------------------------

Create new notebooks
^^^^^^^^^^^^^^^^^^^^

In the JupyterLab interface, use the file navigator in the left sidebar to go
to the **Files** (1) tab. Here, you see a listing of files and folders from
your project. To create a new notebook, first double-click on the **notebooks**
folder (2), then on the **'+'** button (3). Select *Python 3* to create a new
notebook (4).

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

If you are not familiar with JupyterLab, you can read more in the
`JupyterLab documentation <https://jupyterlab.readthedocs.io/en/latest/>`_. You may want to take some time to play with the JupyterLab
interface before continuing.

If you want to save your new notebook(s), you can go to the console and use
``git`` to add your work to the repository. For example, if you want to keep
the new notebook(s), run the following in the terminal:

.. code-block:: console

    git add notebooks # track everything inside the notebooks folder
    git commit -m "Added some notebooks"
    git push

    # [master 0fb9ac1] Added some notebooks
    #     1 file changed, 32 insertions(+)
    #     create mode 100644 notebooks/MyNewNotebook.ipynb
    # Counting objects: 4, done.
    # Delta compression using up to 8 threads.
    # Compressing objects: 100% (4/4), done.
    # Writing objects: 100% (3/3), 639 bytes | 639.00 KiB/s, done.
    # Total 4 (delta 1), reused 0 (delta 0)
    # To https://dev.renku.ch/gitlab/john.doe/flights-tutorial.git
    #     c1dcfe4..0fb9ac1  master -> master

If you prefer to use a UI, an extension providing access to the basic git
commands is built into JupyterLab. Click on the git icon on the left sidebar of
JupyterLab (1) to open the git panel. Here, you can add untracked files by
selecting them and clicking the up arrow (2). Finally, you can enter a commit
message in (3) and click the check mark to make a commit.

.. image:: ../../_static/images/jupyterlab-git-panel.png
    :width: 85%
    :align: center
    :alt: Commit notebook in JupyterLab

.. _renkulab.io: https://renkulab.io
.. _documentation: https://renku.readthedocs.io/
.. _papermill: https://papermill.readthedocs.io/en/latest/
