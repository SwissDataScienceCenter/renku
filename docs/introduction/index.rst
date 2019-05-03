.. _renku_introduction:

Introduction
============

.. epigraph::

   **Renku** (連句 "linked verses"), is a Japanese form of popular
   collaborative linked verse poetry, written by more than one author
   working together.*

   -- Wikipedia

**Renku** is a software platform designed to foster multidisciplinary (data)
science collaboration.


Features
--------

Renku consists of a collection of services, including a web-based user interface
and a command-line client, exploiting in a coherent setup the joint features of:

* `GitLab <https://gitlab.com>`_ -  repository management
* `JupyterHub <http://jupyter.org/hub>`_ - interactive notebooks
* `Kubernetes <https://kubernetes.io>`_ - container orchestration
* `Keycloak <https://www.keycloak.org>`_ - identity and access management
* `Common Workflow Language <https://www.commonwl.org>`_ - analysis workflows & tools description

Here is a quick, high-level overview of current Renku features.

Renku allows you to:

* track the progress of analysis from raw data to results, with the help of
  a minimal CLI
* basic knowledge graph creation, limited to single repositories
* produce reusable workflow components on-the-fly to easily rerun your
  analysis when data or code change
* share data across projects with workflow dependency tracking
* push projects to the cloud for easy sharing and collaboration
* interactively explore the project at any point of its revision history,
  complete with a versioned execution environment
* embed notebooks directly in project discussions
* preview and merge interactive notebook changes in the web UI


Anticipated Features in the Short-term
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

* running and re-running of Renku workflows in the cloud
* visualizing and modifying workflow steps in the web UI
* basic platform-wide search utilizing the Knowledge Graph


.. _concepts:

Renku Concepts
--------------

The goal of Renku is to provide data scientists with simple tools to make
their work reproducible, repeatable, reusable and shareable. To achieve these
ends, we glue together many different technologies and approaches from data
science, computer science and software engineering. Here we provide an
introduction to these concepts so that you as a user have a better
understanding of what takes place behind the scenes as you work with the Renku
platform.


Reproducibility
^^^^^^^^^^^^^^^

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
* How can I share my latest analysis with a colleague through the cloud?
* How can I make sure that our colleagues in another team can reproduce our results?

Answering these questions in a reliable fashion is difficult. Renku provides
the tools that substantially reduce the effort required on the part of the
data scientists who want to keep their work reproducible, reliable and robust.
Here, we introduce the essential concepts underlying the design and
implementation of Renku.

Several important concepts intertwine in Renku to enable reproducible data science.
These are:

.. toctree::
  :maxdepth: 1

   Lineage <lineage>
   Version control <version_control>
   Environments and containerization <containerization>


Re-usability
^^^^^^^^^^^^

Once a project takes advantage of the lineage-tracking provided by Renku, it
opens many new possibilities. For example, one can now reuse pieces of the
project, whether it is a related project building on the previous one or a
completely new analysis. Importing an object with Renku lineage into another
Renku project means that the information is automatically carried over. One of
the upcoming improvements to Renku will be to use this information when
searching for data or algorithms and allow us to answer queries like "who is
using my algorithm/dataset and how?"


Long-term vision
----------------

As in the traditional art form of renku from which it borrows its name, the
platform encourages the interdisciplinary cooperation (or coopetition) between
data scientists to advance a research agenda through individual leap
contributions.

In the same spirit, **Renku's** philosophy is to mobilize and connect skills
from the data science community, to enhance their collective intelligence, and
to allow the discovery of knowledge without premeditation. From the users’
perspective, it is essentially a digital canvas where data science projects
are constantly conducted and improved, evolving asynchronously into a web of
interwoven data science threads, where the output of one analytics becomes the
input of another.

Under the hood, the platform is governed by a loosely coupled federated model
whose goal is to allow autonomous organizations to unite in order to share
resources without renouncing their authority to manage the resources they own
the way they want.  In this form, it acts as a connected ecosystem of data,
meta-data, analytics, and computing resources. It implements services to
securely investigate data science problems and allows data scientists to
evaluate, compare, and share each other’s methods and results. Participating
organizations interact in a flat hierarchy where mutual trust between
different administrative domains or a central authority is avoided. They are
in full control of their respective platform services and resources, and
manage them according to their own preferences.

The platform automatically materializes data lineage into a knowledge graph
representation that serves as the basis for traceable and repeatable research.
Data lineage seamlessly capture the workflows both within and across data
science projects, allowing the lineage of any derived data to be unambiguously
traced back to the original raw data sources in a manner that is fully
transparent and traceable. Data lineage is automatically recorded; it includes
the code of intermediate data transforms or data analytics and is designed to
enable the repeatability of research. It is also non-circumventable and
tamper-proof, and thus it can be used for governance, intellectual property
attribution, and audit purposes (e.g. answer the question “who used my data,
and for what purpose”). The knowledge representation also provides a trusted
single view of truth. It is a one-stop shop to a vast collection of digital
content that data scientists can explore to find good quality data they can
trust, and confidently reuse in order to eliminate redundant efforts and
accidental data duplication as much as possible.


History
-------

- 2018.11.30. **Renku** v0.3.0 is released with bug-fixes and performance improvements

- 2018.09.25. First functional release of **Renku** v0.2.0; A public deployment is launched at https://renkulab.io.

- 2018.02.01. A rewrite of **Renku** is underway

- 2017.09.19.  **Renku** 0.1.0 (Beta) is released to the public, with some of
  the planned features and all the faults.

- 2017.02.06.  Inauguration of the Swiss Data Science Center, design of
  **Renku** is started.


Disclaimer
----------

THIS SOFTWARE AND ALL ACCOMPANYING DOCUMENTATIONS ARE PROVIDED FREE "AS IS"
AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE SWISS DATA SCIENCE CENTER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
