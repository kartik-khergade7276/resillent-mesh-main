# ResilientMesh – Self-Healing Microservice Mesh

This project is a small, a self-healing microservice mesh concept.

Features demonstrated
- Auto-detect failing nodes via health checks and circuit breakers.
- Auto-reroute requests to healthy instances.
- Chaos testing: downstream service injects random latency and failures.
- Resilience patterns: circuit breaker, retries, exponential backoff.
- Kubernetes manifests including a HorizontalPodAutoscaler (HPA) for auto-scaling based on CPU.

Tech stack
- Java 17
- Spring Boot 3 (Web, Actuator)
- Resilience4j (circuit breaker + retry)
- Docker & Docker Compose
- Kubernetes manifests (for optional deployment)

Quick start with Docker Compose
1. Install Docker and Docker Compose.
2. From the project root, build and run:
   docker compose up --build
3. Once everything is up, call the gateway:
   curl http://localhost:8080/api/orders/42

You’ll see successful responses and, occasionally, error responses when both instances are in chaos.
Most of the time, the gateway will transparently retry or reroute to a healthy instance.

Services
- gateway-service
  - Entry point for clients.
  - Talks to multiple instances of orders-service.
  - Uses Resilience4j for circuit breaker + retry + backoff.
  - Implements simple failover: tries each configured downstream URL until one works.

- orders-service
  - Simulated unstable service.
  - Random latency and random 500 errors (chaos mode).
  - Exposes /actuator/health for readiness / liveness checks.

Kubernetes (optional)
- See k8s/ directory for:
  - Deployments and Services for gateway and orders-service.
  - HorizontalPodAutoscaler for orders-service (CPU-based auto-scaling).
- You can apply them onto a cluster (e.g., minikube or kind):
  kubectl apply -f k8s/

This is a demo, not a production mesh. It’s meant to:
- Show how self-healing patterns look in code.
- Give you a realistic talking point for SRE/DevOps interviews.
- Be a starting point you can extend with service mesh tools like Istio/Linkerd, Prometheus, Grafana, etc.
