# 📷 Instagram Clone – Scalable Microservices Architecture

> A feature-rich Instagram backend clone built with a focus on **microservices**, **asynchronous communication**, **scalability**, and **resilience**. This system includes essential functionalities like posts, comments, likes, profile management, and secure authentication using modern cloud-native technologies.

---

## 🧭 Overview

This project is a scalable clone of Instagram’s backend, implemented as a distributed system composed of multiple microservices. Each service is independently deployable, stateless, and communicates with others via **Kafka**, **REST**, or **Feign Clients**.

The backend supports:
- Uploading and displaying media posts with metadata
- User profiles and authentication
- Comments and likes on posts
- Subscription/follow functionality
- Real-time profile updates via event streaming
- Asynchronous processing and fault tolerance

---

## ✅ Functional Features

### 📄 Post Service
- Create, update, and delete posts
- Upload multiple media files (images/videos) to **AWS S3**
- Indexing in **Elasticsearch** for efficient search
- Event-based communication with Profile and Feed services via **Kafka**

### 👤 Profile Service
- Stores user profile data: username, avatar, bio, status
- Reacts to post creation events via Kafka
- Cached profile lookups using **Redis**

### 💬 Comment Service
- Post and retrieve comments per post
- Asynchronous persistence to optimize performance
- Designed to handle high-volume interactions

### ❤️ Like Service
- Add and remove likes on posts
- Separate microservice to isolate write pressure
- Publishes like events to analytics or feed system

### 🔐 Authentication & User Management
- Registration, login, and secure JWT-based token authentication
- Password encryption and validation
- Role-based authorization

### 📦 Media Storage
- Uses **AWS S3** for storing images and videos
- Pre-signed URLs for secure, client-side uploads
- Links are stored in the Post Service

---

## ⚙️ Architecture Highlights

- **Microservice Architecture**: Each domain (posts, users, likes, comments, calls) runs as an independent Spring Boot service
- **Service Discovery**: Integrated with **Spring Cloud Eureka** for dynamic routing
- **API Gateway**: Routes all external traffic and aggregates documentation (Swagger UI)
- **Async Communication**: **Kafka** is used to propagate events (e.g., PostCreated, UserUpdated) across services
- **Caching Layer**: **Redis** is used for hot-profile caching and token verification
- **Object Storage**: **AWS S3** is used to persist and serve large media content
- **Internal REST**: Lightweight **Feign Clients** connect internal services
- **Secure Auth**: JWT tokens protect endpoints and enforce authorization

---

## 📈 Non-Functional Goals

### ⚡ Asynchronous & Non-Blocking Design
- Kafka-based messaging decouples services
- Each service is reactive-ready and supports high-concurrency scenarios

### 💥 Fault Tolerance & Resilience
- Kafka ensures message delivery even if target services are temporarily down
- Redis caches user/profile data to reduce DB load
- Retry mechanisms are implemented for media uploads and inter-service calls

### 🚀 Scalability
- Horizontal scaling possible per service
- Stateless architecture allows container-based deployment (e.g., Docker Compose)

---

## 🧰 Tech Stack

- **Java 21**, **Spring Boot 3.x**
- **Spring Cloud Gateway**, **Eureka**, **Feign**
- **PostgreSQL**, **MongoDB**
- **Apache Kafka** (KRaft mode)
- **Redis**
- **AWS S3** for media
- **Elasticsearch** for search
- **Docker / Docker Compose**

---

# Architecture diagram
![Instagram](https://github.com/user-attachments/assets/b3d7ea96-c29b-4732-9b98-48e348b3d6a1)

## 📚 API Documentation

Explore the available REST API endpoints using the centralized Swagger UI via the API Gateway:

> 🔗 [http://localhost:8013/swagger-ui.html](http://localhost:8013/swagger-ui.html)

Each microservice is documented individually and accessible through the gateway:

- 🛡️ **Authentication Service**  
  [http://localhost:8013/authentication-service/swagger-ui/index.html](http://localhost:8013/authentication-service/swagger-ui/index.html)

- 👤 **User Data Management Service**  
  [http://localhost:8013/user-data-management-service/swagger-ui/index.html](http://localhost:8013/user-data-management-service/swagger-ui/index.html)

- 🧑‍💼 **Profile Service**  
  [http://localhost:8013/profile-service/swagger-ui/index.html](http://localhost:8013/profile-service/swagger-ui/index.html)

- 📝 **Post Service**  
  [http://localhost:8013/post-service/swagger-ui/index.html](http://localhost:8013/post-service/swagger-ui/index.html)

- 💬 **Comment Service**  
  [http://localhost:8013/comments-service/swagger-ui/index.html](http://localhost:8013/comments-service/swagger-ui/index.html)

- ❤️ **Like Service**  
  [http://localhost:8013/like-service/swagger-ui/index.html](http://localhost:8013/like-service/swagger-ui/index.html)

- 📞 **Calls Service**  
  [http://localhost:8013/calls-service/swagger-ui/index.html](http://localhost:8013/calls-service/swagger-ui/index.html)

- 👥 **Follow (Subscription) Service**  
  [http://localhost:8013/follow-service/swagger-ui/index.html](http://localhost:8013/follow-service/swagger-ui/index.html)

> All documentation is automatically aggregated and exposed via **Springdoc + Spring Cloud Gateway**.

---

## 🗺️ Future Roadmap

- [ ] WebSocket notifications for real-time updates
- [ ] Feed aggregation service
- [ ] Story support with auto-expiry
- [ ] Admin moderation panel
- [ ] Rate limiting and analytics

---

## 🧪 Development & Deployment

All services are containerized and can be launched via:

```bash
docker-compose up --build
