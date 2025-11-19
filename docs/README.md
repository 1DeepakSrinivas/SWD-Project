# Employee Management System

## Overview

Employee Management System is a Java application for managing employee data and operations. The system provides functionality for employee record management, reporting, and database operations using MySQL.

## Quickstart

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

### Setup

1. Clone the repository or navigate to the project directory.

2. Configure your database connection settings (if applicable).

3. Build the project using Maven.

## Build Commands

To build the project, run:

```bash
mvn clean package
```

This command will:
- Clean previous build artifacts
- Compile the source code
- Run unit tests
- Package the application into a JAR file with dependencies (using Maven Shade Plugin)

The resulting JAR file will be located in the `target/` directory.

### Additional Maven Commands

- Compile only: `mvn compile`
- Run tests: `mvn test`
- Clean build directory: `mvn clean`
- Install to local repository: `mvn install`

