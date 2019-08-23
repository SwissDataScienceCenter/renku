.. _dns:

DNS
===

Get a domain name, e.g. `renkulab.io` from a registrar.

Create an `A` record pointing to the load balancer and a wildcard `CNAME` :

==============  =====  ================ ========
NAME            TYPE   TARGET           TTL
==============  =====  ================ ========
mydomain.ch      A     <loadBalancerIP>  15 min.
*.mydomain.ch   CNAME   mydomain.ch      15 min.
==============  =====  ================ ========

Now, we can check the DNS setup:

.. code-block:: console

   $ curl -v http://internal.renku.ch/

The output should be:

.. code-block:: console

   default backend - 404
