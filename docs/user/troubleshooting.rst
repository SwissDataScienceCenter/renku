.. _troubleshooting:

Troubleshooting
================

Some documented issues & how to fix them.

Building the code
-----------------

**Q**: Should I run my code as root?

**A**: No, this will create issues further down the line.

**Q**: I get an issue with docker.sock if I don't run as root?

**A**: Add your user to the docker group `as explained here
<https://docs.docker.com/engine/installation/linux/linux-postinstall/#manage-docker-as-a-non-root-user>`_.
