.. _update_workflow:

Update your results using the recorded workflow
-----------------------------------------------

Here, we will quickly see one of the advantages of using the ``renku`` command
line tool.

Check the file ``data/output/flights-count.txt``; in it, you should see that
there were 23078 flights to Austin, TX in Jan 2019.

This does not seem quite right. Austin, TX is not a very large airport, but
that number would mean that it had a flight landing on average
every two minutes, around the clock, during the entire month of January 2019.

Go back and take a look at the filtering script: it contains 
an error! In the code block

.. tabbed:: Python

    .. code-block:: console

        # Select only flights to Austin (AUS)
        df = df[df['DEST'] == 'DFW']

.. tabbed:: Julia

    .. code-block:: console

        # Filter to flights to Austin, TX
        df = full_df[full_df.DEST .== "DFW", :]

.. tabbed:: R

    .. code-block:: console

        # Select only flights to Austin (AUS)
        data %>% filter(DEST == "DFW")


we want to select flights to Austin-Bergstrom (AUS), but mistakenly select
flights to a different airport, ``DFW``. This would explain the discrepancy
we found. Dallas/Fort Worth is a much larger airport!

Let us fix this. Change ``DFW`` to ``AUS`` and save the file. Now when you
execute ``git status`` you should see something like the following:

.. code-block:: console

    $ git status

    Output:
    On branch master
    Your branch is up to date with 'origin/master'.

    Changes not staged for commit:
      (use "git add <file>..." to update what will be committed)
      (use "git checkout -- <file>..." to discard changes in working directory)

            modified:   src/filter_flights.py

    no changes added to commit (use "git add" and/or "git commit -a")

Since we have made a change to our code, we need to commit the updated file to
the repository.

.. code-block:: console

    $ renku save -m 'fix filter error'

**Reflection**

Now that we have made this change, how would you update everything *without*
Renku? Without Renku, you would need to think back and remember what files
would be affected by this change and what commands were run to initially
create them. To effect an update, you would manually carry out those steps
again, while being careful to do so in the correct order.

So without Renku, updating a project in response to a change can be tedious and
error-prone. But *with* Renku, it is very easy. We can just ask the system
what changed and what needs to be updated. The outputs are analogous for all
programming languages.

.. code-block:: console

    $ renku status

    On branch master
    Outdated outputs:
         (use "renku log [<file>...]" to see the full lineage)
         (use "renku update [<file>...]" to generate the file from its latest inputs)

                data/output/flights-count.txt: src/filter_flights.pycfaf152b
                data/output/flights-filtered.csv: src/filter_flights.pycfaf152b

    Modified inputs:
        (use "renku log --revision <sha1> <file>" to see a lineage for the given revision)

                src/filter_flights.py: bfd5e408, cfaf152b

Renku is telling us that ``src/filter_flights.py`` was changed and
``data/output/flights-filtered.csv``, ``data/output/flights-count.txt`` all need
to be updated as a result. We do not need to remember how to update them: Renku
already knows this. We can just ask it to make the update by running ``renku
update --all`` or ``renku update data/output/flights-filtered.csv
data/output/flights-count.txt``.

.. code-block:: console

    $ renku update --all

    ...
    [workflow ] completed success

**Wasn't that easy!?**

Now, if you look at ``data/output/flights-count.txt``, you should see that
there were 4951 flights to Austin, TX in Jan 2019, which sounds plausible.

Before calling it a day, we should not forget to push our work:

.. code-block:: console

    $ renku save
