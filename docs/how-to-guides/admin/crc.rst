Working with the CRC service
============================

Currently, the CRC offers a REST API which can be interacted with via a Swagger
Web UI. This is available at:

<renku.landing.page>/swagger/?urls.primaryName=crc%20service

It is worth noting that the API is designed such that read operations do not
require authorization but write operations do. As such, it is possible to use
this interface to see the state of the system without requiring a valid
authentication token.

Making changes to the state
---------------------------

To make changes to the state, it is necessary to use a valid authentication
token. There are two steps to this:

- first, you need to ensure that your user has the `renku-admin` role. This can
  be done by searching for your user and choosing the `Role mapping` tab; if
  the `renku-admin` role is already mapped to your user, there is nothing to do
  but if not you need to click `Assign role` and add this role to your user.

- when this has been done, you can authorize the Swagger UI; this can be done
  by clicking on the `Authorize` icon at the top of the page. This provides a
  number of different options for authorization (maybe 7) - you need to select
  the PKCE option and you do not need to enter a client_id or client_secret -
  simply leave these as defaults and click on `Authorize`.

When you have successfully authenticated, you can send `POST` requests to the
endpoint. The API definition provides examples of what kind of entities should
be sent to the endpoint to create resources.

Note that the token has an expiry which means that at some point you may need
to re-authenticate. If you see systematic errors in your responses, this is likely
the case.
