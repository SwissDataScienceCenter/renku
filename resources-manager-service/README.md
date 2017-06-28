# incubator-resources-manager-service

generate some new keys:
```
ssh-keygen
```

Get the public key in the right format
```
ssh-keygen -m PKCS8 -e
```

Get the private key in the right format
```
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in private.key -out pkcs8.key
```