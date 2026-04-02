helm repo add argo https://argoproj.github.io/argo-helm
helm repo update
helm install -f argo-cd-values.yaml argo-cd argo/argo-cd

# To access the UI
# Switch to the namespace where argo cd was installed
# kubectl port-forward service/argo-cd-argocd-server 9090:443
# kubectl get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
