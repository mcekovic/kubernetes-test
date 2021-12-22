#!/bin/bash
nerdctl image build --namespace k8s.io -t mcekovic/test-app:latest .
