## Test Java Apps with Kubernetes

### Prerequisites
- JDK 17+
- Maven 3.6+
- WSL 2
- Rancher Desktop 0.7+ (containerd runtime)

### Build Java Artifact
- `mvn clean package`

### Build Docker Image
- `nerdctl image build --namespace k8s.io -t k8s-test/test-app:latest .`

### Deploy to Kubernetes
- `kubectl apply -f src/main/k8s/postgresql`
- `kubectl apply -f src/main/k8s/test-app`

### Upgrade App
- Build Java artifact and Docker image
- Restart app: `kubectl rollout restart deployment/test-app`

### Monitor via JMX and VisualVM
- Port-forward particular pod's JMX port: `kubectl port-forward <pod-name> --address 0.0.0.0 9999`
- Open VisualVM and create JMX connection `localhost:9999`

### Install Kubernetes Dashboard
[Official Instructions](https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/)

Helper objects are in `src/main/k8s/dashboard`

### Important Points

#### Docker Image
- Use fixed base image version
- Use two-stage image build to reduce image size and attack surface area
- In the build stage fetch the Spring Boot fat jar Java artifact (in this test plain copy is used for simplicity)
- Use Spring Boot layer tools to produce a layered image
- Do not run the container as root, create group and user for the app
- Externalize JVM options into Kubernetes deployment object
- Build image directly into `k8s.io` namespace to avoid using image registry for local development

#### Kubernetes Objects
- Use `imagePullPolicy: Never` for local development to avoid using image registry
- Limit CPU and memory resources
- Use Spring Boot liveness and readiness health check probes (set `initialDelaySeconds` as default is `0`)
- Use pod lifecycle `preStop` pause to avoid that Kubernetes routes traffic to pods that are shutting down
- Externalize JVM options into pod's environment variables (`env` section)
  + Use relative heap limits: `-XX:MaxRAMPercentage=50.0`
  + Expose JMX port (see `test-app.yaml / env / JDK_JAVA_OPTIONS`)
- Externalize app configuration into pod's environment variables that reference ConfigMap
- Use volumes for logs?