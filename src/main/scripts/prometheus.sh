#!/bin/bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

# Prometheus + Alert Manager
helm install --create-namespace --namespace monitoring --values src/main/k8s/prometheus/values.yaml prometheus prometheus-community/prometheus
# Grafana
helm install --namespace monitoring grafana grafana/grafana
# Prometheus + Alert Manager + Grafana
helm install --create-namespace --namespace monitoring --values src/main/k8s/prometheus/values.yaml prometheus prometheus-community/kube-prometheus-stack