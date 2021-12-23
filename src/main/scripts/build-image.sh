#!/bin/bash
nerdctl image build --namespace k8s.io -t k8s-test/test-app:latest .
