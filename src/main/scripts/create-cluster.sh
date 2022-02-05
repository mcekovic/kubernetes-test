#!/bin/bash
k3d cluster create cluster -p "80:8080@loadbalancer" -a 2