.. _openstack:

TODO Re format and reformulate this this (taken from renku-admin-docs)
=================================================

k8s-switch
============

Ansible playbook for deploying kubernetes on SWITCHengines

The content of this repository is heavily inspired by
the work from:
- https://cloudblog.switch.ch/2017/06/26/deploy-kubernetes-on-the-switchengines-openstack-cloud/
- https://github.com/infraly/k8s-on-openstack

# Requirements

## Python Requirements
- python (3.6)
- `pip install -r requirements.txt`

## OpenStack Requirements

Access to an OpenStack cluster with enough resources to spawn a handful of VMs (minimum 3), 2x 100GBs and at least two available floating IPs.

It is strongly recommended to use a machine user on OpenStack.
To set this up on SWITCHengines, contact their support (see email: https://help.switch.ch/engines/).

Note: LBaaSv2 is supported on SWITCHengines -> https://cloudblog.switch.ch/2017/06/26/deploy-kubernetes-on-the-switchengines-openstack-cloud/

The last part requires the registration of a domain name.
Without it, we cannot properly set up HTTPS entry points.

As minimum resources, suitable only for testing, we propose:
* 3x nodes (4 cores, 16GBs, 40GBs)
* 2x PVs of 100GB (pv-renku-gitlab, pv-renku-postgresql)

# Usage

## A. Prepare OpenStack

Get then credentials file (v3) from the openstack console.
Also, setup an SSH key pair on openstack.

Install openstack client (from `requirements.txt`, or via `pip install python-openstackclient`) and
neutron client `pip install python-openstackclient`.

```bash
$ source ./<project>-openrc.sh ## the downloaded version, as explained above
$ openstack
```

```bash
(openstack) network create --enable --internal --description "Network for Kubernetes" kubenet
(openstack) subnet create --subnet-range 192.168.0.0/24 --gateway 192.168.0.254 --ip-version 4 --network kubenet kubesubnet
(openstack) router create --description "Router for Kubernetes" kuberouter
(openstack) router add subnet kuberouter kubesubnet
(openstack) router set --external-gateway public kuberouter
```

We can now populate env.sh:
```
(openstack) image list # select centos
(openstack) floating ip list
(openstack) network list
(openstack) router list
```

## B. Prepare variables
- `source ./env.sh`
- `source ./<project>-openrc.sh`

## C. Use Rancher to deploy kubernetes, tune machines

Regardless if the resources are raw metal or VMs, the procedure is quite similar;
in the below instructions, k8s is deployed via `rancher`; however any other similar tool would work:
* Bring up the VMs, using centos7. Inspect if they are ready:
   * Ensure `yum upgrade ; yum upgrade kernel` and related commands have been applied and rebooted as needed
   * Ensure kernels across VMs match each other and the output of `rpm -qa` is consistent
   * Ensure /etc/resolv.conf does not have a `search` value, only nameservers listed
   * NTP/DNS should be functional and correct; DO this check NOW.
* Deploy `rancher/2.0` per its installation instructions, on your `master` node
   * single node install is fine for now.
   * familiarize yourself with the rancher interface
* Add k8s cluster via `rancher`, tune `sans` values and use for network `weave`
   * Save in git repo your rancher yaml file, like in: `20181126-k8s-site-XYZ-prod-rancher-config.yaml`
   * Spin up a sufficient number of worker nodes for your cluster, via rancher.
* Make sure you download the k8s config file and save it as the `admin.conf` in your git repo.
   * T.B.D: Edit `admin.conf` to use the master node floating IP in place of something like `server: https://192.168.20.7:6443`.
* `kubectl get nodes` should return the list of nodes ; Do NOT proceed until this is working fine.

## D. Create default k8s storage class

```bash
$ export KUBECONFIG=`pwd`/admin.conf
$ kubectl apply -f manifests/storage-class.yml
$ kubectl get sc
NAME                PROVISIONER            AGE
default             kubernetes.io/cinder   6s
```

Set the default StorageClass annotation (https://kubernetes.io/docs/tasks/administer-cluster/change-default-storage-class/):

```bash
$ kubectl edit sc
$ kubectl get sc
NAME                PROVISIONER            AGE
default (default)   kubernetes.io/cinder   47s
```


