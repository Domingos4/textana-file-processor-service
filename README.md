# Textana: AI-Powered Text Analysis Platform

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen?style=flat-square&logo=spring)
![Maven](https://img.shields.io/badge/Maven-3.9+-blue?style=flat-square&logo=apache-maven)
![AWS S3](https://img.shields.io/badge/AWS-S3-FF9900?style=flat-square&logo=amazon-aws)
![AWS SQS](https://img.shields.io/badge/AWS-SQS-FF9900?style=flat-square&logo=amazon-aws)
![AWS DynamoDB](https://img.shields.io/badge/AWS-DynamoDB-FF9900?style=flat-square&logo=amazon-aws)
![OpenAI](https://img.shields.io/badge/OpenAI-GPT-412991?style=flat-square&logo=openai)
![Lombok](https://img.shields.io/badge/Lombok-Enabled-red?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)

A distributed, cloud-native text analysis platform built with microservices architecture that leverages AI to extract insights, generate summaries, and classify sentiment from text documents. Powered by Spring Boot, AWS services, and OpenAI's GPT models.

---

## Microservices

### Textana Upload Service

**Port:** `8090` | **Context:** `/textana-upload-service`

A RESTful file upload microservice that validates and accepts .TXT files (up to 10MB), stores them securely in **AWS S3** with unique UUID identifiers, and publishes tracking messages to **AWS SQS** to trigger downstream processing—providing a clean entry point for asynchronous text analysis pipelines.

**Key Features:**
- File validation (type, size, format)
- Secure S3 storage with UUID-based keys
- Asynchronous processing via SQS messaging
- RESTful API with structured error handling

**Tech Stack:**
- Spring Boot 3.5.8
- AWS S3 SDK
- AWS SQS SDK v2
- Lombok

**Endpoints:**
- `POST /api/v1/upload` - Upload a text file for analysis

---

### Textana File Processor Service

**Port:** `8091` | **Context:** `/textana-fp-service`

A cloud-native text analysis microservice that consumes file identifiers from **AWS SQS**, retrieves .TXT files from **S3**, and processes them through **OpenAI's GPT models** to extract summaries and classify sentiment (positive, neutral, negative)—storing results in **DynamoDB** for fast, scalable access in distributed workflows.

**Key Features:**
- Scheduled SQS polling with batch processing
- AI-powered text summarization via OpenAI GPT
- Sentiment analysis (POSITIVE, NEUTRAL, NEGATIVE)
- Persistent state management in DynamoDB
- Automatic retry and error handling

**Tech Stack:**
- Spring Boot 3.5.8
- Spring AI (OpenAI Integration)
- AWS S3 SDK v2
- AWS SQS SDK v2
- AWS DynamoDB SDK v2
- Lombok
- Spring Scheduler

**Processing Pipeline:**
1. Consume message from SQS queue
2. Retrieve file content from S3
3. Generate summary using GPT
4. Detect sentiment using GPT
5. Store results in DynamoDB
6. Delete message from queue

---

### Textana API

**Port:** `8090` | **Context:** `/textana-api`

A read-focused REST API that queries **AWS DynamoDB** to retrieve file analysis status and results by UUID—returning structured, clean responses with processing state (in-progress, completed, failed), AI-generated summaries, and sentiment classifications for real-time tracking of text analytics workflows.

**Key Features:**
- UUID-based result retrieval
- Real-time processing status tracking
- Structured JSON responses
- Fast DynamoDB queries
- Comprehensive error handling

**Tech Stack:**
- Spring Boot 3.5.8
- AWS DynamoDB SDK v2
- Lombok

---

### Contributing

If you have any suggestions or feedback, feel free to open an issue or submit a pull request.

