
# Technicolor Server

A multi-client file server built in Java to understand networking, sockets, protocols, file transfer, and server-side system design from first principles.

The project started as a learning exercise inspired by a discussion on building projects that improve understanding of how computers communicate. Over time, it evolved into a complete client-server application supporting authentication, file operations, session management, and AI-powered file summarization.

## Features

* Multi-client server architecture
* User registration
* User authentication and session management
* File upload
* File download
* Directory navigation
* Command-based protocol design
* Persistent user credentials
* Server-side logging
* AI-powered file summarization using Gemini API

## Tech Stack

* Java
* Java Sockets
* File I/O
* Java HttpClient
* Gemini API

## Commands

### Authentication

```text
REGISTER <username>
LOGIN <username>
```

### File Operations

```text
LIST
OPEN <file>
UPLOAD <file>
DOWNLOAD <file>
CD <directory>
PWD
WHOAMI
HELP
```

### AI Features

```text
SUMMARIZE <file>
```

Reads a text file and generates a concise summary using Gemini.

## Example Session

```text
LOGIN ANIKA
Please enter password.
scoobydoo

User Authenticated.

LIST

orange.txt
pink.txt

SUMMARIZE pink.txt

The text encourages readers to expand their perspective and look beyond immediate limitations.
```

## What I Learned

Building Technicolor Server provided hands-on experience with:

* Network programming using sockets
* Protocol design and communication boundaries
* Streams and file transfer
* Authentication and session management
* Debugging distributed interactions
* Refactoring and clean code practices
* Single Responsibility Principle (SRP)
* Logging and observability
* Integrating external AI services through APIs

## Future Improvements

* Database-backed user management
* Spring Boot migration
* Improved JSON parsing for AI responses
* Role-based authorization
* Web or mobile client interface

## Project Status

Technicolor Server V1 is complete.

What began as curiosity became a practical exploration of networking, system design, and backend development.
