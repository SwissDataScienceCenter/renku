.. _loadbalancer:

Configuring a Load Balancer
===========================

To set up a load balancer for k8s in your cluster you can configure a node with an HAProxy instance or use LBASS from your cloud provider.

A. Using an HAProxy loadbalancer
---------------------------------

Steps to setup and configure the HA proxy server.

1. Spawn a VM on SWITCH engine (no volumes, m1.small flavor, k8s-balancer security group, kubenet network) and assign a floating IP.
2. Make sure all ports are enabled in the security group: `22, 2222, 443, 6443, 80`.
3. Login to the VM and install haproxy.

.. code-block:: console

   $ sudo yum install haproxy

4. Disable security feature (to be able to ssh from node `2222`).


.. code-block:: console

   $ sudo setenforce Permissive

5. Edit ssh config file to listen on port `2222`.

.. code-block:: console

   $ sudo vi /etc/ssh/sshd_config

6. Restart the service

.. code-block:: bash

   $ sudo systemctl restart sshd

After, you can verify by logging in again with port `2222`

.. code-block:: bash

   $ ssh -p 2222 <user@hostname>

7. Edit the haproxy configuration.

.. code-block:: bash

   $ sudo vi /etc/haproxy/haproxy.cfg

Delete the main frontend, static backend and round robin balancing sections.

Add configuration for worker nodes to listen in ssh traffic (port 22), https traffic (port 443) and http traffic (port 80).

Here is a full example of an haproxy.cfg file:

.. code-block:: console

  #### port 22, ssh traffic
  listen ssh 0.0.0.0:22
         balance roundrobin
         mode tcp
         option tcp-check
         server <node1-hostname> <node1-ip>:32022 check port 32022 fall 3 rise 2
         server <node2-hostname> <node2-ip>:32022 check port 32022 fall 3 rise 2
         server <node3-hostname> <node3-ip>:32022 check port 32022 fall 3 rise 2

  #### port 443, https traffic
  listen https 0.0.0.0:443
         balance roundrobin
         mode tcp
         option tcp-check
         server <node1-hostname> <node1-ip>:32443 check port 32443 fall 3 rise 2
         server <node2-hostname> <node2-ip>:32443 check port 32443 fall 3 rise 2
         server <node3-hostname> <node3-ip>:32443 check port 32443 fall 3 rise 2

  ## Port 80 is open to all, to allow ACME challenge http01 to work
  #### port 80, http traffic
  listen http 0.0.0.0:80
         balance roundrobin
         mode http
         option httpchk HEAD /healthz  HTTP/1.0
         server <node1-hostname> <node1-ip>:32080 check port 32080 fall 3 rise 2
         server <node2-hostname> <node2-ip>:32080 check port 32080 fall 3 rise 2
         server <node3-hostname> <node3-ip>:32080 check port 32080 fall 3 rise 2

  #### port 6443, k8s API https traffic
  listen https 0.0.0.0:6443
         balance roundrobin
         mode tcp
         option tcp-check
         server <master-hostname> <master-ip>:6443 check port 6443 fall 3 rise 2


B. Using an Openstack LBaaS loadbalancer
----------------------------------------

You can refer to the `Openstack documentation <https://docs.openstack.org/mitaka/networking-guide/config-lbaas.html>`_ for instructions on how to use Openstack LBaaS
