# Setup Guide

Complete setup instructions for the Employee Management System from scratch.

## Prerequisites

Before starting, ensure you have the following installed:

- **Java 21** or higher
  - Check version: `java -version`
  - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
  
- **Maven 3.6** or higher
  - Check version: `mvn -version`
  - Download: [Apache Maven](https://maven.apache.org/download.cgi)
  
- **MySQL 8.0** or higher
  - Check version: `mysql --version`
  - Download: [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)

## Step 1: Clone or Download the Project

If using Git:
```bash
git clone <repository-url>
cd SWD-Project
```

Or extract the project to a directory of your choice.

## Step 2: Set Up MySQL Database

### 2.1 Start MySQL Service

**macOS (Homebrew):**
```bash
brew services start mysql
```

**Linux:**
```bash
sudo systemctl start mysql
# or
sudo service mysql start
```

**Windows:**
Start MySQL from Services or use MySQL Workbench.

### 2.2 Create the Database

Connect to MySQL and create the database:

```bash
mysql -u root -p
```

In MySQL prompt:
```sql
CREATE DATABASE IF NOT EXISTS emp_mgmt;
EXIT;
```

### 2.3 Run Schema Script

Create the database tables:

```bash
mysql -u root -p emp_mgmt < src/db/schema.sql
```

### 2.4 Load Sample Data (Optional)

Populate the database with sample data:

```bash
mysql -u root -p emp_mgmt < src/db/sample-data.sql
```

## Step 3: Configure Environment Variables

### 3.1 Create .env File

Copy the example environment file:

```bash
cp .env.example .env
```

### 3.2 Edit .env File

Open `.env` in a text editor and update with your MySQL credentials:

```env
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=emp_mgmt
DB_USER=root <or your mysql username>
DB_PASS= <your_mysql_password>
```

**Note:** The database name should be `emp_mgmt` (not `employee_management`).

Replace `your_mysql_password` with your actual MySQL root password. If MySQL root has no password, leave `DB_PASS` empty:

Also replace `or your mysql username>` with your username if it is not `root`
```env
DB_PASS=
```

## Step 4: Build the Project

### 4.1 Compile the Project

```bash
mvn clean compile
```

This will:
- Download required dependencies (MySQL Connector/J, JUnit Jupiter)
- Compile Java source files
- Place compiled classes in `target/classes/`

### 4.2 Package the Application

```bash
mvn clean package
```

This will:
- Compile the code
- Run unit tests (if any)
- Create a JAR file with dependencies in `target/` directory

The output JAR will be named: `employee-management-1.0.0.jar`

## Step 5: Test Database Connection

Verify the database connection is working:

```bash
mvn exec:java -Dexec.mainClass="com.emp_mgmt.db.DatabaseConnectionManager"
```

Expected output:
```
Attempting to connect to database...
JDBC URL: jdbc:mysql://localhost:3306/emp_mgmt?useSSL=false&allowPublicKeyRetrieval=true
Connection established successfully.
Database connection test successful.
Number of tables in current database: 6
Connection closed.
```

If you see this output, the setup is complete and working correctly.

## Step 6: Verify Database Setup

Verify tables and data were created:

```bash
mysql -u root -p emp_mgmt -e "SHOW TABLES;"
```

You should see:
- division
- employees
- employee_division
- employee_job_titles
- job_titles
- payroll

Check sample data:

```bash
mysql -u root -p emp_mgmt -e "SELECT COUNT(*) AS employee_count FROM employees;"
```

Should show 15 employees if sample data was loaded.

## Troubleshooting

### Connection Errors

**Error: "Access denied for user 'root'@'localhost'"**
- Verify MySQL password in `.env` file
- Test MySQL connection: `mysql -u root -p`
- Ensure `DB_USER` and `DB_PASS` are correct

**Error: "Unknown database 'emp_mgmt'"**
- Create the database: `mysql -u root -p -e "CREATE DATABASE emp_mgmt;"`
- Run schema script: `mysql -u root -p emp_mgmt < src/db/schema.sql`

**Error: "No suitable driver found"**
- Ensure MySQL Connector/J dependency is in `pom.xml`
- Run `mvn clean compile` to download dependencies
- Check Maven dependencies: `mvn dependency:tree`

### Build Errors

**Error: "JAVA_HOME is not set"**
- Set JAVA_HOME environment variable
- macOS/Linux: `export JAVA_HOME=$(/usr/libexec/java_home)`
- Windows: Set in System Environment Variables

**Error: "Maven not found"**
- Add Maven to PATH
- Verify installation: `mvn -version`

### Database Script Errors

**Error: "Table already exists"**
- This is normal if re-running scripts
- Schema script uses `DROP TABLE IF EXISTS`, so it's safe to re-run

**Error: "Duplicate entry" in sample-data.sql**
- Sample data uses `INSERT IGNORE`, so duplicates are skipped
- Safe to re-run the script

## Next Steps

After successful setup:

1. Review the project structure in `src/main/java/com/emp_mgmt/`
2. Check database connection documentation: `docs/JDBC-CONNECTION.md`
3. Explore the database schema: `src/db/schema.sql`
4. Start developing your application components

## Additional Resources

- Project README: `docs/README.md`
- JDBC Connection Guide: `docs/JDBC-CONNECTION.md`
- Maven Documentation: https://maven.apache.org/guides/
- MySQL Documentation: https://dev.mysql.com/doc/

