# JDBC Connection Configuration

## Overview

The `DatabaseConnectionManager` class reads database connection parameters from a `.env` file in the project root. This allows for flexible configuration without hardcoding credentials.

## Configuration Variables

The following variables are read from the `.env` file:

| Variable | Description | Default Value |
|----------|-------------|---------------|
| `DB_HOST` | MySQL server hostname | `localhost` |
| `DB_PORT` | MySQL server port | `3306` |
| `DB_NAME` | Database name | `emp_mgmt` |
| `DB_USER` | Database username | `root` |
| `DB_PASS` | Database password | (empty string) |

## Setting Up .env File

1. Copy the example file:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` and update with your database credentials:
   ```env
   # Database Configuration
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=employee_management
   DB_USER=root
   DB_PASS=your_password
   ```

The `.env` file is automatically loaded from the project root directory when `DatabaseConnectionManager` is initialized. If the file is not found or a variable is missing, default values will be used.

## Testing the Connection

Run the `DatabaseConnectionManager` main method to test the connection:

```bash
mvn compile exec:java -Dexec.mainClass="com.emp_mgmt.db.DatabaseConnectionManager"
```

Or compile and run directly:

```bash
mvn compile
java -cp target/classes com.emp_mgmt.db.DatabaseConnectionManager
```

The test will:
1. Attempt to connect to the database
2. Query `information_schema.tables` to verify connectivity
3. Display the number of tables in the current database
4. Close the connection

## Connection Features

- **Singleton Pattern**: Only one instance of `DatabaseConnectionManager` exists
- **Retry Logic**: Automatically retries failed connections up to 3 times with exponential backoff
- **Connection Validation**: Validates connections before returning them
- **Safe Cleanup**: Provides methods to safely close connections

## Security Notes

- Never commit `.env` files or hardcode credentials in source code
- Use strong passwords for production databases
- Consider using connection pooling for production applications
- The current implementation uses `useSSL=false` for local development; enable SSL for production

