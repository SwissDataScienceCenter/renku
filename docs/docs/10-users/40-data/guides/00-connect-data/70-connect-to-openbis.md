---
title: openBIS
---

[openBIS](https://openbis.ch/) is a combined FAIR data management platform, Electronic Laboratory Notebook (ELN) and Inventory Management System.

You can mount your openBIS storage into a Renku session starting
with Renku version `2.12.0`.

Starting from your Renku project page, you can do the following:
1. Click the `+` sign in the `Data` section on the bottom left.
2. Select the `Create a data connector` option.
3. Select the `openBIS` storage type.
4. Click `Next`.
5. Specify the host of your openBIS deployment. For, example for
  the demo deployment you would specify `openbis-eln-lims.ethz.ch`.
  Note that for a real use case you will need a real deployment, not the demo one.
6. Get an openBIS session token:
    1. Navigate to your specific openBIS deployment.
    2. Log in.
    3. Navigate to `Tools` on top navigation menu.
    4. In the sidebar under `Utilities` navigate to `User Profile`.
    5. You will find the session token under the `openBIS session token` field.
7. Back on Renku, after you have pasted your openBIS session token,
  click on `Test connection`.
8. If the configuration and credentials are correct the test will succeed
  and you can save the data connector along with the session token.
9. Start a new session and browse the contents of your openBIS projects.

:::info

Many openBIS deployments are behind a VPN or firewall and they may not
be publicly accessible from your Renku instance. In this case testing the
connection to the data will persistently fail. If this is the case, in order to see
openBIS data in Renku you need to contact your openBIS administrators
and ask them to allow access to openBIS from your Renku instance.
The administrators of your Renku instance can provide the IP addresses that 
should be whitelisted in the openBIS VPN or firewall.

:::
