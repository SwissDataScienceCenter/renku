#!/bin/env python3

import argparse
import Crypto
from Crypto.PublicKey import RSA
from Crypto.PublicKey import DSA
from Crypto.PublicKey import ECC

from ruamel.yaml import YAML
from ruamel.yaml.scalarstring import PreservedScalarString as pss

argparser = argparse.ArgumentParser(description=__doc__)
argparser.add_argument("--filename", help="Name of the file to output the secret.", default="ssh-host-keys-secret.yaml")
argparser.add_argument("--name", help="Name of the secret.", default="renku-ssh-host-keys")
argparser.add_argument("--namespace", help="Target namespace for the secret.", default="renku")
args = argparser.parse_args()

rsa_key = RSA.generate(3072)
dsa_key = DSA.generate(1024)
ecdsa_key = ECC.generate(curve="P-256")
ed25519_key = ECC.generate(curve="Ed25519")

secret_manifest = {
    "kind": "Secret",
    "apiVersion": "v1",
    "metadata": {"name": args.name, "namespace": args.namespace},
    "stringData": {
        "ssh_host_dsa_key": pss(dsa_key.export_key(format="PEM").decode("UTF-8")),
        "ssh_host_dsa_key.pub": pss(dsa_key.public_key().export_key(format="OpenSSH").decode("UTF-8")),
        "ssh_host_rsa_key": pss(rsa_key.export_key().decode("UTF-8")),
        "ssh_host_rsa_key.pub": pss(rsa_key.public_key().export_key().decode("UTF-8")),
        "ssh_host_ecdsa_key": pss(ecdsa_key.export_key(format="PEM")),
        "ssh_host_ecdsa_key.pub": pss(ecdsa_key.public_key().export_key(format="OpenSSH")),
        "ssh_host_ed25519_key": pss(ed25519_key.export_key(format="PEM")),
        "ssh_host_ed25519_key.pub": pss(ed25519_key.public_key().export_key(format="OpenSSH")),
    },
}

with open("ssh-host-keys-secret.yaml", "w") as f:
    YAML().dump(secret_manifest, f)
