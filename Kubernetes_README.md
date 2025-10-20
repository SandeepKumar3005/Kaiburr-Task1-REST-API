# Task 2: Kubernetes Deployment

## Author Information
**Name:** Sandeep Kumar  
**Date:** October 21, 2025  
**GitHub:** [@SandeepKumar3005](https://github.com/SandeepKumar3005)

---

## Overview

This directory contains Kubernetes configuration files for deploying the Task Manager REST API with MongoDB on a local Kubernetes cluster using Minikube. The deployment demonstrates container orchestration, service discovery, load balancing, and high availability.

---

## Architecture

The deployment consists of:

- **MongoDB Database** (1 replica)
  - Stores task data persistently
  - Exposed via ClusterIP service (internal only)
  - Port: 27017

- **Task Manager API** (2 replicas)
  - Spring Boot REST API application
  - Connects to MongoDB service
  - Exposed via NodePort service (external access)
  - Port: 8081 → NodePort: 30081
```
┌─────────────────────────────────┐
│   External User (Postman)       │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│    Minikube Service Tunnel      │
│  http://127.0.0.1:xxxxx/tasks   │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│  task-manager-service (NodePort)│
│     Port: 8081 → 30081          │
└────────────┬────────────────────┘
             │
        ┌────┴────┐
        ▼         ▼
   ┌────────┐ ┌────────┐
   │  API   │ │  API   │  (2 Replicas - Load Balanced)
   │  Pod 1 │ │  Pod 2 │
   └───┬────┘ └───┬────┘
       │          │
       └────┬─────┘
            ▼
     ┌─────────────┐
     │  mongodb-   │
     │  service    │
     │ (ClusterIP) │
     └──────┬──────┘
            ▼
     ┌─────────────┐
     │  MongoDB    │
     │    Pod      │
     └─────────────┘
```

---

## Prerequisites

Before deploying, ensure you have:

- **Docker Desktop** installed and running
- **kubectl** (Kubernetes command-line tool)
- **Minikube** (local Kubernetes cluster)
- **Task Manager Docker Image** (`task-manager-api:1.0`)

---

## Files Description

### `mongodb-deployment.yaml`
Contains:
- **Deployment**: Defines MongoDB pod with mongo:8.0 image
- **Service**: ClusterIP service for internal database access

### `task-manager-deployment.yaml`
Contains:
- **Deployment**: Defines Task Manager API pods (2 replicas) with custom image
- **Service**: NodePort service for external API access
- **Environment Variables**: 
  - `MONGODB_HOST=mongodb-service`
  - `SPRING_DATA_MONGODB_HOST=mongodb-service`

---

## Installation & Deployment Steps

### Step 1: Start Minikube
```bash
minikube start --driver=docker
```

**Expected output:**
```
😄  minikube v1.37.0 on Windows 11
✨  Using the docker driver
👍  Starting control plane node minikube in cluster minikube
🚜  Pulling base image ...
🔥  Creating docker container ...
🐳  Preparing Kubernetes v1.34.0 on Docker 27.2.0 ...
🔎  Verifying Kubernetes components...
🏄  Done! kubectl is now configured to use "minikube" cluster
```

**Verify:**
```bash
minikube status
```

---

### Step 2: Build Docker Image (if not already built)
```bash
cd D:\kaiburr\Task-Manager
docker build -t task-manager-api:1.0 .
```

---

### Step 3: Load Docker Image to Minikube

Minikube runs in its own Docker environment, so we need to load our local image:
```bash
minikube image load task-manager-api:1.0
```

**Expected output:**
```
✅  Successfully loaded image task-manager-api:1.0
```

---

### Step 4: Deploy MongoDB
```bash
kubectl apply -f kubernetes/mongodb-deployment.yaml
```

**Expected output:**
```
deployment.apps/mongodb created
service/mongodb-service created
```

**Wait for MongoDB to be ready:**
```bash
kubectl get pods -w
```

Wait until you see:
```
NAME                       READY   STATUS    RESTARTS   AGE
mongodb-xxxxxxxxx-xxxxx    1/1     Running   0          30s
```

Press `Ctrl+C` to stop watching.

---

### Step 5: Deploy Task Manager API
```bash
kubectl apply -f kubernetes/task-manager-deployment.yaml
```

**Expected output:**
```
deployment.apps/task-manager created
service/task-manager-service created
```

---

### Step 6: Verify All Pods are Running
```bash
kubectl get pods
```

**Expected output:**
```
NAME                            READY   STATUS    RESTARTS   AGE
mongodb-xxxxxxxxx-xxxxx         1/1     Running   0          2m
task-manager-xxxxxxxxx-xxxxx    1/1     Running   0          45s
task-manager-yyyyyyyyy-yyyyy    1/1     Running   0          45s
```

✅ **You should see 3 pods total:** 1 MongoDB + 2 Task Manager replicas

---

### Step 7: Get Service URL
```bash
minikube service task-manager-service --url
```

**Example output:**
```
http://127.0.0.1:53690
```

**Copy this URL** - this is your API endpoint!

---

## Testing the Deployment

Use the URL from Step 7 in all API calls below. Replace `http://127.0.0.1:53690` with your actual URL.

### Test 1: GET All Tasks
```bash
curl http://127.0.0.1:53690/tasks
```

**Or in Postman:**
- Method: GET
- URL: `http://127.0.0.1:53690/tasks`

---

### Test 2: Create a Task
```bash
curl -X PUT http://127.0.0.1:53690/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "id": "k8s-test",
    "name": "Kubernetes Test",
    "owner": "Sandeep",
    "command": "echo Hello from K8s!"
  }'
```

**Or in Postman:**
- Method: PUT
- URL: `http://127.0.0.1:53690/tasks`
- Body (raw, JSON):
```json
{
  "id": "k8s-test",
  "name": "Kubernetes Test",
  "owner": "Sandeep",
  "command": "echo Hello from K8s!"
}
```

---

### Test 3: Execute Task
```bash
curl -X PUT http://127.0.0.1:53690/tasks/k8s-test/execute
```

**Expected Response:**
```json
{
  "id": "k8s-test",
  "name": "Kubernetes Test",
  "owner": "Sandeep",
  "command": "echo Hello from K8s!",
  "taskExecutions": [
    {
      "startTime": "2025-10-21T12:47:14.000+00:00",
      "endTime": "2025-10-21T12:47:14.044+00:00",
      "output": "Hello from K8s!\n"
    }
  ]
}
```

---

### Test 4: Search Tasks
```bash
curl http://127.0.0.1:53690/tasks/search?name=Kubernetes
```

---

### Test 5: Delete Task
```bash
curl -X DELETE http://127.0.0.1:53690/tasks/k8s-test
```

**Expected Response:**
```
Task deleted successfully
```

---

## Verification Commands

### Check Pod Status
```bash
kubectl get pods
```

All pods should show `READY 1/1` and `STATUS Running`

---

### Check Services
```bash
kubectl get services
```

**Expected output:**
```
NAME                   TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
kubernetes             ClusterIP   10.96.0.1       <none>        443/TCP          15m
mongodb-service        ClusterIP   10.100.200.50   <none>        27017/TCP        5m
task-manager-service   NodePort    10.100.200.100  <none>        8081:30081/TCP   3m
```

---

### View Pod Logs

**Get pod names:**
```bash
kubectl get pods
```

**View Task Manager logs:**
```bash
kubectl logs <task-manager-pod-name>
```

**View MongoDB logs:**
```bash
kubectl logs <mongodb-pod-name>
```

---

### Describe Pod (Detailed Info)
```bash
kubectl describe pod <pod-name>
```

Shows: Events, container status, environment variables, volumes, etc.

---

## Key Features Demonstrated

✅ **Container Orchestration** - Kubernetes manages Docker containers  
✅ **High Availability** - 2 replicas of API for redundancy  
✅ **Load Balancing** - Kubernetes distributes traffic across pods  
✅ **Service Discovery** - Pods find MongoDB via service name  
✅ **Auto-restart** - Pods automatically restart if they crash  
✅ **Horizontal Scaling** - Can easily scale to more replicas  
✅ **Environment Configuration** - Environment variables for MongoDB host  

---

## Scaling the Deployment

### Scale API Replicas

**Scale up to 3 replicas:**
```bash
kubectl scale deployment task-manager --replicas=3
```

**Scale down to 1 replica:**
```bash
kubectl scale deployment task-manager --replicas=1
```

**Verify:**
```bash
kubectl get pods
```

---

## Troubleshooting

### Pods Not Starting?

**Check pod status:**
```bash
kubectl get pods
```

**Check pod events:**
```bash
kubectl describe pod <pod-name>
```

**View logs:**
```bash
kubectl logs <pod-name>
```

**Common issues:**
- Image not loaded to Minikube → Run `minikube image load task-manager-api:1.0`
- MongoDB not ready → Wait for MongoDB pod to be Running first
- Port conflicts → Restart Minikube: `minikube stop && minikube start`

---

### Can't Connect to API?

**1. Get fresh service URL:**
```bash
minikube service task-manager-service --url
```

**2. Check if pods are ready:**
```bash
kubectl get pods
```

All should show `1/1` in READY column.

**3. Check service exists:**
```bash
kubectl get services
```

---

### MongoDB Connection Issues?

**Check MongoDB service:**
```bash
kubectl get service mongodb-service
```

**Check MongoDB pod:**
```bash
kubectl logs <mongodb-pod-name>
```

**Verify environment variables in API pod:**
```bash
kubectl describe pod <task-manager-pod-name>
```

Look for `MONGODB_HOST=mongodb-service`

---

## Clean Up Resources

### Delete All Deployments
```bash
kubectl delete -f kubernetes/task-manager-deployment.yaml
kubectl delete -f kubernetes/mongodb-deployment.yaml
```

### Stop Minikube
```bash
minikube stop
```

### Delete Minikube Cluster
```bash
minikube delete
```

This removes all resources and frees up disk space.

---

## Screenshots

Visual documentation of the deployment:

1. **Pods Running** - All 3 pods (1 MongoDB + 2 API) in Running status
2. **Services** - mongodb-service and task-manager-service configured
3. **Initial GET** - Empty tasks array before creating tasks
4. **Create Task** - PUT request creating new task
5. **Execute Task** - PUT request executing task with output
6. **Search Task** - GET request searching by name
7. **Delete Task** - DELETE request removing task
8. **GET After Delete** - Confirming task was deleted (empty array)

All screenshots available in `screenshots/` folder.

---

## Environment Variables

The Task Manager API uses these environment variables to connect to MongoDB:

| Variable | Value | Description |
|----------|-------|-------------|
| `MONGODB_HOST` | `mongodb-service` | Kubernetes service name for MongoDB |
| `SPRING_DATA_MONGODB_HOST` | `mongodb-service` | Spring Data MongoDB configuration |

These are set in `task-manager-deployment.yaml`.

---

## Port Configuration

| Component | Internal Port | External Port | Access |
|-----------|---------------|---------------|---------|
| MongoDB | 27017 | N/A | Internal only (ClusterIP) |
| Task Manager API | 8081 | 30081 (NodePort) | External via Minikube tunnel |
| Minikube Service | N/A | Dynamic (e.g., 53690) | `minikube service` command |

---

## Technology Stack

- **Kubernetes** - Container orchestration platform
- **Minikube** - Local Kubernetes cluster
- **Docker** - Container runtime
- **Spring Boot** - Java application framework
- **MongoDB** - NoSQL database
- **kubectl** - Kubernetes CLI tool

---

## What We Learned

Through this deployment, we demonstrated:

1. **Containerization** - Packaging Spring Boot app in Docker
2. **Kubernetes Concepts** - Pods, Deployments, Services, Replicas
3. **Service Types** - ClusterIP (internal) vs NodePort (external)
4. **Load Balancing** - Traffic distribution across multiple pods
5. **Service Discovery** - Using service names for inter-pod communication
6. **Configuration Management** - Environment variables for configuration
7. **Health Checks** - Kubernetes monitors pod health automatically
8. **Scaling** - Horizontal scaling with replica sets

---

## Next Steps (Optional)

- **Task 3:** Build React frontend to interact with this API
- **Task 4:** Set up CI/CD pipeline for automated deployment
- Deploy to cloud Kubernetes service (GKE, EKS, AKS)
- Add persistent volumes for MongoDB data
- Implement health check endpoints
- Add resource limits and requests
- Configure Ingress for better routing

---

## References

- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Minikube Documentation](https://minikube.sigs.k8s.io/docs/)
- [Spring Boot with Kubernetes](https://spring.io/guides/gs/spring-boot-kubernetes/)
- [MongoDB on Kubernetes](https://www.mongodb.com/kubernetes)

---

**✅ Task 2 Successfully Completed!**

**Repository:** https://github.com/SandeepKumar3005/Kaiburr-Task2-Kubernetes

**Deployment:** Fully functional Kubernetes cluster with load-balanced API and MongoDB database.