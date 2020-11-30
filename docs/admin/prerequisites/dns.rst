.. _dns:

DNS
====

Renku requires a DNS domain.

Get a domain name from a registrar.
Create an `A` record pointing to the load balancer and, optionally, a wildcard `CNAME`:

==================  =====  ================== ========
NAME                TYPE   TARGET             TTL
==================  =====  ================== ========
renku.mydomain.ch   A      load balancer IP   15 min.
\*.mydomain.ch      CNAME  renku.mydomain.ch  15 min.
==================  =====  ================== ========

Now, check the DNS setup:

.. code-block:: console

   $ curl -v http://mydomain.ch/

The output should be:

.. code-block:: console

   default backend - 404
