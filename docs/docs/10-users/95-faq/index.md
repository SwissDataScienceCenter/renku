# Frequently Asked Questions

## Data

### Is my data copied into the session?

Data is mounted into your Renku session, not copied. No data is transferred until you access it in a
session.

## Sessions

### What can I do with a Renku session?

You can use Renku sessions for pretty much any computational task that has a graphical user
interface. Though you mostly see examples of Python and R in RenkuLab, you can use any programming
language in Renku.  

You can [configure a custom environment for your Renku
session](10-users/60-sessions/guides/20-create-environment-with-custom-packages-installed.md), and
even [ use your own docker image for a Renku
session](10-users/60-sessions/guides/45-use-your-own-docker-image-for-renku-session.md).

### Can I keep a session running after I close the browser?

If you work logged into Renku, your session will still be running even if you close your browser and
shut down your computer. Sessions in the public resource pool will go to a Pause state after 2 hours
of the session not using CPU resources. When you resume your session, you will have access to your
work where you left it. A paused session that is not resumed before 14 days will be automatically
shut-down. 

## Billing

### Is Renku free? When is RenkuLab not free?

Everyone is welcome to use RenkuLab and run sessions in our free tier resource pool. If you need
more computational resources than are available in our free tier,
[Contact](/docs/users/community) us to set up a
contract for a custom resource pool on RenkuLab.
