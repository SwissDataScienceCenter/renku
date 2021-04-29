.. _rstudio:

R: working in RStudio, with git
-------------------------------

The canonical way of working interactively with R is using the RStudio IDE.
As default, there are four panels available in the userface, which cover a
code editor, console/terminal section, environment panel and file explorer.

We note that it is actually possible to run Jupyter Notebooks with an R kernel,
if one navigates to the */lab* endpoint of the URL of the renku project. There
you can select the R kernel and proceed as usual. However, we proceed with
this part of the tutorial in RStudio.

The R console and script editor can be used together to quickly prototype
data pipelines. An advantage of RStudio is that the environment panel will show
all the data frames and variables stored in local memory. 

In order keep R scripts organised, you may want to create a ``src`` folder in the 
main project directory. 

An important part of using renku within RStudio is the Terminal. This is where
you can access renku commands and make sure you have tracked changes using git.

