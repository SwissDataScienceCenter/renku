.. _dns:

TODO Re format and re factor this (taken from renku-admin-docs)
================================================================

DNS
===

## F. DNS setup

Get a domain name, e.g. `renku.ch` from a registrar.

Create an `A` record pointing to the load balancer and
a wildcard `CNAME` (86.119.40.77 is the floating ip of the load balancer):

| NAME | TYPE | TARGET | TTL |
| ----- | -------- | ----- | -------- |
| internal.renku.ch | A | 86.119.40.77 | 15 min. |
| *.internal.renku.ch | CNAME | internal.renku.ch | 15 min. |

Another example could be:

| NAME | TYPE | TARGET | TTL |
| ----- | -------- | ----- | -------- |
| example.com | A | 86.119.40.77 | 5 min. |
| *.example.com | CNAME | example.com | 5 min. |

Now, we can check the DNS setup:
```bash
$ curl -v http://internal.renku.ch/
default backend - 404
