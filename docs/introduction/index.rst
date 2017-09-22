.. warning::

  **This is a beta version of Renga**

  This platform is a (very early) beta version. It is made available to early
  adopters only for the purpose of getting acquainted with the APIs. THIS
  VERSION IS NOT INTENDED TO BE USED IN PRODUCTION MODE OR WITH SENSITIVE
  DATA. In this first version we put the focus on the knowledge
  representation, data lineage, data versioning, meta-data exploration,
  application deployments, storage, resource management, and authentication.
  The component that will fulfill the above vision of the platform are in
  place, however it also includes several place holders. Most notably the
  permission management policies will be available only in the next release of
  the platform, which is planned for Q1 2018. Even thought the enforcement
  methods of the permission policies are in place, the only available policy
  in this version is *full access to everything for all*. In addition, this
  being a beta version, the code and default configurations has not been
  edited for security vulnerabilities. We provide no guarantee whatsoever
  about the security of the data stored on this platform.

.. _renga_introduction:

Introduction
============

.. epigraph::

   **Renga** (連歌), *plural* **renga**, *a genre of Japanese linked-verse
   poetry in which two or more poets supply alternating sections of a poem
   linked by verbal and thematic associations.*

   -- Encyclopædia Britannica

**Renga** is a highly-scalable & secure open software platform designed to
foster multidisciplinary data (science) collaboration across mutually
untrusted academic and industrial institutions.

The platform allows (data)scientists to:

* Securely manage, share and process large-scale data across untrusted parties
  operating in a federated environment.

* Automatically capture complete lineage up to original raw data for detailed
  traceability, auditability & reproducibility.

Philosophy
----------

As in the traditional art form of renga from which it borrows its name, the
platform encourages the interdisciplinary cooperation (or coopetition) between
data scientists to advance a research agenda through individual leap
contributions.

In the same spirit, **Renga's** philosophy is to mobilize and connect skills
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

- 2017.09.19  **Renga** 0.1.0 (Beta) is released to the public, with some of
  the planned features and all the faults.

- 2017.02.06  Inauguration of the Swiss Data Science Center, design of
  **Renga** is started.


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
