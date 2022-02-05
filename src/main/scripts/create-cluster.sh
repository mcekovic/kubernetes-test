#!/bin/bash
k3d cluster create cluster -p "8080:8080@loadbalancer" -a 2