#!/bin/bash
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

# Loki + Promtail
helm install --create-namespace --namespace monitoring loki grafana/loki-stack
# Loki
helm install --create-namespace --namespace monitoring loki grafana/loki
# Promtail
helm install --namespace monitoring --values src/main/k8s/loki/values.yaml promtail grafana/promtail



