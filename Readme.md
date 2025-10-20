# Task 1: Task Manager REST API

## Author Information
**Name:** Sandeep Kumar  
**Date:** October 19-21, 2025  
**GitHub:** [@SandeepKumar3005](https://github.com/SandeepKumar3005)

---

## Project Overview

A RESTful API for managing and executing shell command tasks. Built with Spring Boot and MongoDB, containerized with Docker, deployed on Kubernetes, with automated CI/CD pipeline using GitHub Actions.

**Key Features:**
- ✅ Create, Read, Update, Delete tasks
- ✅ Execute shell commands via API
- ✅ Search tasks by name
- ✅ MongoDB persistence
- ✅ Docker containerization
- ✅ Kubernetes deployment with 2 replicas
- ✅ Automated CI/CD pipeline
- ✅ Security scanning and code coverage

---

## Technologies Used

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 21 |
| Framework | Spring Boot | 3.5.6 |
| Database | MongoDB | 8.0 |
| Build Tool | Maven | 3.9.11 |
| Container | Docker | Latest |
| Orchestration | Kubernetes | Minikube |
| CI/CD | GitHub Actions | - |
| Security | Trivy | Latest |
| Coverage | JaCoCo | 0.8.11 |

---

## Quick Start

### Prerequisites
- Java 21 JDK
- Maven 3.9+
- MongoDB 8.0
- Docker Desktop
- Kubernetes (Minikube)

### Local Development
```bash
# Clone repository
git clone https://github.com/SandeepKumar3005/Kaiburr-Task1-REST-API.git
cd Kaiburr-Task1-REST-API

# Start MongoDB
mongosh

# Build and run
mvn clean install
mvn spring-boot:run
```

API runs on: **http://localhost:8081**

### Docker Deployment
```bash
# Build image
docker build -t task-manager-api:1.0 .

# Run container
docker run -p 8081:8081 task-manager-api:1.0
```

### Kubernetes Deployment
```bash
# Start Minikube
minikube start --driver=docker

# Load image
minikube image load task-manager-api:1.0

# Deploy
kubectl apply -f kubernetes/mongodb-deployment.yaml
kubectl apply -f kubernetes/task-manager-deployment.yaml

# Get service URL
minikube service task-manager-service --url
```

---

## API Endpoints

### Base URL
```
http://localhost:8081
```

### Endpoints

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/tasks` | Get all tasks | - |
| GET | `/tasks?id={id}` | Get task by ID | - |
| GET | `/tasks/search?name={name}` | Search by name | - |
| PUT | `/tasks` | Create/Update task | Task JSON |
| PUT | `/tasks/{id}/execute` | Execute task | - |
| DELETE | `/tasks/{id}` | Delete task | - |

### Request/Response Examples

#### Create Task (PUT /tasks)

**Request:**
```json
{
  "id": "task-1",
  "name": "Hello World Task",
  "owner": "Sandeep Kumar",
  "command": "echo Hello World"
}
```

**Response:**
```json
{
  "id": "task-1",
  "name": "Hello World Task",
  "owner": "Sandeep Kumar",
  "command": "echo Hello World",
  "taskExecutions": []
}
```

#### Execute Task (PUT /tasks/{id}/execute)

**Request:**
```
PUT http://localhost:8081/tasks/task-1/execute
```

**Response:**
```json
{
  "id": "task-1",
  "name": "Hello World Task",
  "owner": "Sandeep Kumar",
  "command": "echo Hello World",
  "taskExecutions": [
    {
      "startTime": "2025-10-19T14:30:00.000+00:00",
      "endTime": "2025-10-19T14:30:00.045+00:00",
      "output": "Hello World\n"
    }
  ]
}
```

#### Search Tasks (GET /tasks/search?name=Hello)

**Response:**
```json
[
  {
    "id": "task-1",
    "name": "Hello World Task",
    "owner": "Sandeep Kumar",
    "command": "echo Hello World",
    "taskExecutions": [...]
  }
]
```

---

## Testing with Postman

### Collection Setup

Import or create these requests:

1. **GET All Tasks**
   - URL: `http://localhost:8081/tasks`
   - Method: GET

2. **Create Task**
   - URL: `http://localhost:8081/tasks`
   - Method: PUT
   - Body: raw JSON

3. **Execute Task**
   - URL: `http://localhost:8081/tasks/task-1/execute`
   - Method: PUT

4. **Search Tasks**
   - URL: `http://localhost:8081/tasks/search?name=Hello`
   - Method: GET

5. **Delete Task**
   - URL: `http://localhost:8081/tasks/task-1`
   - Method: DELETE

---

## Project Structure
```
Task-Manager/
├── .github/
│   └── workflows/              # CI/CD pipelines
│       ├── ci-cd-pipeline.yml
│       ├── docker-publish.yml
│       └── kubernetes-deploy.yml
├── src/
│   ├── main/
│   │   ├── java/com/kaiburr/Task_Manager/
│   │   │   ├── controller/
│   │   │   │   └── TaskController.java
│   │   │   ├── model/
│   │   │   │   ├── Task.java
│   │   │   │   └── TaskExecution.java
│   │   │   ├── repository/
│   │   │   │   └── TaskRepository.java
│   │   │   ├── service/
│   │   │   │   └── TaskService.java
│   │   │   └── TaskManagerApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── kubernetes/
│   ├── mongodb-deployment.yaml
│   ├── task-manager-deployment.yaml
│   └── screenshots/
├── screenshots/                # Task 1 screenshots
├── Dockerfile
├── pom.xml
├── README.md                   # This file
└── CI-CD-README.md            # Detailed CI/CD docs
```

---

## CI/CD Pipeline (Task 4)

![CI/CD Pipeline](https://github.com/SandeepKumar3005/Kaiburr-Task1-REST-API/workflows/CI%2FCD%20Pipeline/badge.svg)

### Pipeline Features

✅ **Automated Build** - Maven builds on every push  
✅ **Automated Testing** - Unit tests with JaCoCo coverage  
✅ **Security Scanning** - Trivy vulnerability detection  
✅ **Docker Build** - Automated image creation  
✅ **Code Quality** - Coverage reports and analysis  
✅ **Kubernetes Deploy** - Automated deployment automation  
✅ **Parallel Execution** - Fast feedback with parallel jobs  
✅ **Artifact Storage** - Build outputs saved for 30 days  

### Pipeline Architecture
```
Git Push → GitHub Actions Trigger
    ↓
┌───────────────┬───────────────┐
↓               ↓               ↓
Build & Test   Docker Build   Security Scan
    ↓               ↓               ↓
Code Quality   Artifact Upload   Notifications
    ↓
Deploy to Kubernetes
```

### Workflows

1. **Main CI/CD Pipeline** (`ci-cd-pipeline.yml`)
   - Build and test with Maven
   - Create Docker image
   - Run security scans with Trivy
   - Generate code coverage reports
   - Upload build artifacts

2. **Docker Publish** (`docker-publish.yml`)
   - Build and tag Docker images
   - Optimize with build cache
   - Triggered on version tags

3. **Kubernetes Deploy** (`kubernetes-deploy.yml`)
   - Deploy to Minikube cluster
   - Run on successful CI/CD
   - Verify deployment health

### Trigger Pipeline

**Automatic:**
```bash
git add .
git commit -m "Update application"
git push origin main
```

**Manual:**
- GitHub → Actions tab → Select workflow → Run workflow

### View Results

1. Go to repository: https://github.com/SandeepKumar3005/Kaiburr-Task1-REST-API
2. Click **"Actions"** tab
3. View workflow runs and logs
4. Download artifacts (JAR, Docker image, coverage reports)

### Complete CI/CD Documentation

**📁 Detailed guide:** [CI-CD-README.md](CI-CD-README.md)

Includes:
- Pipeline architecture diagrams
- Stage-by-stage breakdown
- Troubleshooting guide
- Local testing instructions
- Configuration details

---

## Kubernetes Deployment (Task 2)

**Architecture:**
- MongoDB: 1 pod (ClusterIP)
- Task Manager API: 2 replicas (NodePort)
- Automatic load balancing
- High availability

**Quick Deploy:**
```bash
minikube start
docker build -t task-manager-api:1.0 .
minikube image load task-manager-api:1.0
kubectl apply -f kubernetes/
minikube service task-manager-service --url
```

**Separate Repository:** https://github.com/SandeepKumar3005/Kaiburr-Task2-Kubernetes

---

## React Frontend (Task 3)

Modern web UI for the Task Manager API with:
- Create, update, delete tasks
- Execute commands and view output
- Search functionality
- Beautiful gradient design
- Real-time updates

**Repository:** https://github.com/SandeepKumar3005/Kaiburr-Task3-React-Frontend

**Access:** http://localhost:3000 (when running)

---

## Database Schema

### Task Collection
```javascript
{
  "_id": ObjectId,
  "id": String,           // User-defined task ID
  "name": String,         // Task name
  "owner": String,        // Task owner
  "command": String,      // Shell command to execute
  "taskExecutions": [     // Execution history
    {
      "startTime": ISODate,
      "endTime": ISODate,
      "output": String
    }
  ]
}
```

**Indexes:**
- Unique index on `id` field
- Text index on `name` field (for search)

---

## Configuration

### application.properties
```properties
# Server
server.port=8081

# MongoDB
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=taskdb

# Logging
logging.level.com.kaiburr=DEBUG
```

### Environment Variables (Kubernetes)
```yaml
- name: MONGODB_HOST
  value: mongodb-service
- name: SPRING_DATA_MONGODB_HOST
  value: mongodb-service
- name: SPRING_DATA_MONGODB_PORT
  value: "27017"
```

---

## Development

### Build Commands
```bash
# Clean build
mvn clean install

# Skip tests
mvn clean install -DskipTests

# Run tests only
mvn test

# Code coverage
mvn clean test jacoco:report

# Run application
mvn spring-boot:run
```

### Docker Commands
```bash
# Build image
docker build -t task-manager-api:1.0 .

# Run container
docker run -p 8081:8081 task-manager-api:1.0

# View logs
docker logs <container-id>

# Stop container
docker stop <container-id>
```

### Kubernetes Commands
```bash
# Deploy
kubectl apply -f kubernetes/

# View pods
kubectl get pods

# View services
kubectl get services

# View logs
kubectl logs <pod-name>

# Scale replicas
kubectl scale deployment task-manager --replicas=3

# Delete deployment
kubectl delete -f kubernetes/
```

---

## Troubleshooting

### Application won't start

**Check:**
- MongoDB is running: `mongosh`
- Port 8081 is available
- Java 21 is installed: `java -version`

### Connection refused errors

**Check:**
- MongoDB connection string in application.properties
- MongoDB is accessible: `mongosh mongodb://localhost:27017`

### Tests failing

**Check:**
- MongoDB not required for unit tests
- Test dependencies in pom.xml
- Run: `mvn clean test`

### Docker build fails

**Check:**
- Dockerfile exists in project root
- All referenced files exist
- Docker daemon is running

### Kubernetes pods not starting

**Check:**
- Minikube is running: `minikube status`
- Image loaded: `minikube image ls`
- View pod logs: `kubectl logs <pod-name>`

---

## Screenshots

### Task 1: REST API
- Postman collections
- API responses
- MongoDB data
- Local execution

📁 Location: `screenshots/`

### Task 2: Kubernetes
- Pods and services
- Deployment status
- Kubernetes dashboard
- Load balancing

📁 Location: `kubernetes/screenshots/`

### Task 4: CI/CD Pipeline
- GitHub Actions workflows
- Pipeline execution
- Build artifacts
- Security scans

📁 Location: `ci-cd-screenshots/` (to be created after first run)

---

## Related Repositories

This project is part of the Kaiburr Assessment series:

| Task | Description | Repository |
|------|-------------|------------|
| Task 1 | REST API with Spring Boot & MongoDB | [Link](https://github.com/SandeepKumar3005/Kaiburr-Task1-REST-API) |
| Task 2 | Kubernetes Deployment | [Link](https://github.com/SandeepKumar3005/Kaiburr-Task2-Kubernetes) |
| Task 3 | React Web UI | [Link](https://github.com/SandeepKumar3005/Kaiburr-Task3-React-Frontend) |
| Task 4 | CI/CD Pipeline | This repo (Actions tab) |

---

## Performance

### Metrics
- API Response Time: < 100ms (avg)
- Docker Image Size: ~300MB
- Build Time: ~2-3 minutes
- Test Execution: ~10 seconds
- Deployment Time: ~5 minutes

### Scalability
- Horizontal scaling with Kubernetes replicas
- Stateless API design
- MongoDB connection pooling
- Load balancing across pods

---

## Security

### Implemented
✅ Input validation with Spring Validation  
✅ Dependency scanning with Trivy  
✅ Secure Docker image (non-root user)  
✅ Kubernetes security contexts  
✅ No hardcoded secrets  

### Best Practices
- Regular security scans in CI/CD
- Minimal Docker base image
- Environment variable configuration
- GitHub Security alerts enabled

---

## Contributing

This is an assessment project. For issues or suggestions:

1. Open an issue on GitHub
2. Describe the problem or enhancement
3. Include steps to reproduce (if bug)

---

## License

Created for Kaiburr Assessment - Educational purposes

---

## Contact

**Sandeep Kumar**
- GitHub: [@SandeepKumar3005](https://github.com/SandeepKumar3005)
- Email: sandeep.kumar@example.com

---

## Acknowledgments

- Kaiburr Assessment Team
- Spring Boot Community
- Kubernetes Community
- MongoDB Community
- GitHub Actions Documentation

---

**Kaiburr Assessment - Complete Full-Stack Application**

**Main Repository:** https://github.com/SandeepKumar3005/Kaiburr-Task1-REST-API

**Status:** ✅ All Tasks Complete
- ✅ Task 1: REST API
- ✅ Task 2: Kubernetes
- ✅ Task 3: React Frontend
- ✅ Task 4: CI/CD Pipeline

---

**Last Updated:** October 21, 2025