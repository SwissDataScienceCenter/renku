# Generate SSH host keys

This simple script helps generating the Kubernetes secret that renku-notebooks expect
when the SSH-into-sessions feature is enabled and the Renku administrator wants to
set the SSH host keys.

## Running the script

Install the python dependencies:
```bash
pip install -r requirements.txt
```

Run the scripts, optionally passing the name of the filename where to save the output, the name of the secret
and the namespace where it will be deployed:
```bash
./generate-ssh-host-keys.py --filename ssh-host-keys-secret.yaml --name renku-ssh-host-keys --namespace renku
```

## Deploying the secret

Use kubectl to deploy the generated secret:
```bash
kubectl apply -f ssh-host-keys-secret.yaml
```

Change the values file to point to the deployed secret:
```yaml
notebooks:
  ssh:
    enabled: true
    hostKeySecret: renku-ssh-host-keys
```
