# Quick Start Guide - Employee Management System

## Prerequisites

1. **MySQL Server** installed and running
2. **Java 17+** installed
3. **Maven** installed

## First Time Setup

### Step 1: Configure Database

Copy the example environment file and set your MySQL password:

```bash
cp .env.example .env
# Edit .env and set your MySQL password
```

Your `.env` file should look like:
```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=emp_mgmt
DB_USER=root
DB_PASS=your_mysql_password
```

### Step 2: Start MySQL (if not running)

```bash
./src/db/start-mysql.sh
```

Or with automatic database initialization:

```bash
./src/db/start-mysql.sh --init
```

### Step 3: Run the Application

```bash
mvn clean javafx:run
```

**That's it!** The application will automatically:
- Create the database if it doesn't exist
- Create all tables
- Load sample data (5 divisions, 15 job titles, 15 employees)
- Start the UI

## What You'll See

### Main Menu
- **Search Employees** - Find employees by name, SSN, or ID
- **Add/Edit Employee** - Create new employees or modify existing ones
- **Reports** - View payroll reports by division and job title
- **Salary Adjustment** - Bulk salary updates by range

### Sample Data Included

**Divisions:**
- Engineering
- Sales
- Marketing
- Human Resources
- Finance

**Job Titles:**
- Software Engineer
- Senior Software Engineer
- Lead Software Engineer
- Sales Representative
- Sales Manager
- Marketing Specialist
- Marketing Manager
- HR Coordinator
- HR Manager
- Financial Analyst
- Finance Manager
- Product Manager
- Quality Assurance Engineer
- DevOps Engineer
- Business Analyst

**Employees:**
- 15 sample employees with complete records
- Each assigned to a division and job title
- Payroll records spanning 2024-2026

## Common Tasks

### Adding a New Employee

1. Click **"Add/Edit Employee"** from main menu
2. Fill in employee details:
   - First Name
   - Last Name
   - SSN (9 digits, no dashes)
   - Email
3. Select **Division** from dropdown
4. Select **Job Title** from dropdown
5. Click **Save**

### Searching for Employees

1. Click **"Search Employees"** from main menu
2. Select search mode:
   - **Name** - Search by first or last name
   - **SSN** - Search by Social Security Number
   - **Employee ID** - Search by ID number
3. Enter search term
4. Click **Search**

### Running Reports

1. Click **"Reports"** from main menu
2. Select **Year** and **Month**
3. Click **"Run Reports"**
4. View results:
   - Total Pay by Job Title
   - Total Pay by Division

### Adjusting Salaries

1. Click **"Salary Adjustment"** from main menu
2. Enter salary range:
   - Minimum Salary
   - Maximum Salary
3. Enter percentage increase (e.g., 5 for 5%)
4. Click **"Apply Adjustment"**

## Troubleshooting

### Empty Dropdowns

If Division or Job Title dropdowns are empty:

1. Check database has data:
   ```bash
   mysql -u root -p emp_mgmt -e "SELECT * FROM division;"
   ```

2. If empty, reload sample data:
   ```bash
   mysql -u root -p emp_mgmt < src/db/sample-data.sql
   ```

3. Restart the application

### Connection Errors

If you see "Connection refused" or similar errors:

1. Verify MySQL is running:
   ```bash
   ./src/db/start-mysql.sh
   ```

2. Check your `.env` file has correct credentials

3. Test connection manually:
   ```bash
   mysql -h localhost -P 3306 -u root -p
   ```

### JavaFX Runtime Missing

If you see "JavaFX runtime components are missing":

**Always use Maven to run:**
```bash
mvn clean javafx:run
```

**Don't use:**
```bash
java com.employeemgmt.ui.App  # This won't work!
```

## Database Management

### Reset Database

To start fresh with clean sample data:

```bash
# Drop and recreate database
mysql -u root -p -e "DROP DATABASE IF EXISTS emp_mgmt; CREATE DATABASE emp_mgmt;"

# Reload schema and data
./src/db/start-mysql.sh --init

# Or manually:
mysql -u root -p emp_mgmt < src/db/schema.sql
mysql -u root -p emp_mgmt < src/db/sample-data.sql
```

### Backup Database

```bash
mysqldump -u root -p emp_mgmt > backup.sql
```

### Restore Database

```bash
mysql -u root -p emp_mgmt < backup.sql
```

## Development

### Project Structure

```
src/
├── main/
│   ├── java/com/employeemgmt/
│   │   ├── dao/           # Data Access Objects
│   │   ├── db/            # Database utilities
│   │   ├── model/         # Data models
│   │   ├── service/       # Business logic
│   │   └── ui/            # User interface
│   └── resources/
│       └── com/employeemgmt/ui/fx/  # FXML files
└── db/
    ├── schema.sql         # Database schema
    ├── sample-data.sql    # Sample data
    └── start-mysql.sh     # MySQL startup script
```

### Running Tests

```bash
mvn test
```

### Building JAR

```bash
mvn clean package
```

## Support

For detailed database setup information, see:
- `src/db/README.md` - Database documentation
- `DATABASE_CHANGES.md` - Recent changes and improvements

## Next Steps

1. Explore the sample data
2. Try searching for employees
3. Add a new employee
4. Run some reports
5. Customize for your needs!

---

**Note**: The application automatically initializes the database on first run. You don't need to manually create tables or load data unless you want to reset everything.
