.. _loadbalancer:

Configuring a Load Balancer
===========================

A. Using an HAProxy loadbalancer
--------------------------------

Kubernetes ports for HAProxy:
```
#### port 443, https traffic
listen myhttps 0.0.0.0:443
       server sv-renku-k8s01 128.178.244.6:32443 check port 32443 fall 3 rise 2
       server sv-renku-k8s02 128.178.244.7:32443 check port 32443 fall 3 rise 2

#### port 80, http traffic
listen myhttp 0.0.0.0:80

       server sv-renku-k8s01 128.178.244.6:32080 check port 32080 fall 3 rise 2
       server sv-renku-k8s02 128.178.244.7:32443 check port 32443 fall 3 rise 2
```

B. Using an LBASS loadbalancer
------------------------------

[TODO]
