.. _trouble_shooting:

Trouble Shooting
================

Some documented issues & how to fix them.

Building the code
-----------------

**Q**: Should I run my code with root?

**A**: No, this will create issues further down the line.

**Q**: I get an issue with docker.sock if I don't run as root?

**A**: Add your user to the docker group `as explained here <https://docs.docker.com/engine/installation/linux/linux-postinstall/#manage-docker-as-a-non-root-user>`_.

Using Swagger
-------------

**Q**: Upon creating a bucket with POST /authorize/create_bucket I get a failed response after a successful creation.

Example::

  "status": "completed",
     "response": {
       "uuid": "940c147e-82fc-4b01-8ac7-d2fa75c4c233",
       "event": {
         "status": "failed",
         "reason": "Property Key with given name does not exist: type"
       },
       "timestamp": "2017-08-10T10:29:51.923Z"
     }

**A**: The bucket has been created properly but failed to write to the graph. Check the docker-compose version, if the version is not very recent, try upgrading docker-compose (it has been tested on Docker-compose version 1.14.0) 

