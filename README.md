## Test Java Apps with Kubernetes

### Prerequisites
- JDK 17+
- Maven 3.8+
- WSL 2
- Rancher Desktop 0.7+ (containerd runtime)

### Build Java Artifact
- `mvn clean package`

### Build Container Image
- `nerdctl image build --namespace k8s.io -t k8s-test/test-app:latest .`

### Deploy to Kubernetes
- `kubectl apply -f src/main/k8s/postgresql`
- `kubectl apply -f src/main/k8s/test-app`

### Upgrade App
- Build Java artifact and container image
- Restart app: `kubectl rollout restart deployment/test-app`

### Monitor via JMX and VisualVM
- Port-forward particular pod's JMX port: `kubectl port-forward <pod-name> --address 0.0.0.0 9999`
- Open VisualVM and create JMX connection `localhost:9999`

### Install Kubernetes Dashboard
[Official Instructions](https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/)

Helper objects are in `src/main/k8s/dashboard`

### Important Points

#### Container Image
- Use fixed base image version
- Use two-stage image build to reduce image size and attack surface area
- In the build stage fetch the Spring Boot fat jar Java artifact (in this test plain copy is used for simplicity)
- Use Spring Boot layer tools to produce a layered image
- Do not run the container as root, create group and user for the app
- Externalize JVM options out of Dockerfile into Kubernetes descriptors
- Build image directly into `k8s.io` namespace to avoid using image registry for local development

#### Kubernetes Objects
- Use `imagePullPolicy: Never` for local development to avoid using image registry
- Limit CPU and memory resources
- Use Spring Boot liveness and readiness health check probes (set `initialDelaySeconds` as default is `0`)
- Use pod lifecycle `preStop` pause to avoid that Kubernetes routes traffic to pods that are shutting down
- Externalize JVM options and app configuration into ConfigMap that is injected into pod's environment variables 
  + Use relative heap limits: `-XX:MaxRAMPercentage=50.0`
  + Expose JMX port (see `test-app.yaml / test-app-config / JDK_JAVA_OPTIONS`)
  + Reference dependent stateful services by StatefulSets hostnames
  + Reference dependent stateless services hosts and ports from environment variables directly in `env` section
  + Materialize application configuration file for expanded properties to be visible in container
- Use volumes for app logs?
- Install databases as StatefulSets with PersistentVolume and VolumeClaimTemplates