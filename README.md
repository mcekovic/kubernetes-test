## Test Java Apps with Kubernetes

### Prerequisites

- JDK 17+
- Maven 3.6+
- Rancher Desktop 0.7+ (containerd runtime)

### Build Java Artifact

- `mvn clean install`

### Build Docker Image

- `nerdctl image build --namespace k8s.io -t k8s-test/test-app:latest .`

### Deploy to Kubernetes

- `kubectl apply -f src/main/k8s`

### Upgrade App

- Build Java artifact and Docker image
- Restart app: `kubectl rollout restart deployment/test-app`

### Monitor via JMX and VisualVM

- Port-forward particular pod's JMX port: `kubectl port-forward <pod-name> --address 0.0.0.0 9999`
- Open VisualVM and create JMX connection `localhost:9999`

### Install Kubenetes Dashboard

[Official Instructions](https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/)


### Important Points

#### Docker Image

- Use fixed base image version
- Use two-stage image build to reduce image size
- In build stage fetch the Spring Boot Java artifact (in this test plain copy is used)
- Use Spring Boot layer tools to produce layered image
- Do not run container as root, create group and user for the app
- Externalize JVM options into Kubernetes deployment
- Build image directly into `k8s.io` namespace to avoid using image registry for local development

#### Kubernetes Objects

- Use `imagePullPolicy: Never` for local development to avoid using image registry
- Limit CPU and memory resources
- Use Spring Boot liveness and readiness health check probes (set `initialDelaySeconds` as default is `0`)
- Use lifecycle `preStop` pause to avoid that Kubernetes route traffic to pods that are shutting down
- Externalize JVM options in pod's environment variables (`env` section)
  + Use relative heap limits: `-XX:MaxRAMPercentage=50.0`
  + Expose JMX port (see `test-app.yaml / evn / JDK_JAVA_OPTIONS`)
- Externalize app configuration in pod's environment variables (or ConfigMap?)
- Use volumes for logs (which volume type?)