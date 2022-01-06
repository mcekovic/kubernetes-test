#!/bin/bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

helm install --create-namespace --namespace prometheus --values src/main/k8s/prometheus/values.yaml prometheus prometheus-community/prometheus