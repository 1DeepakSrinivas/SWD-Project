# Database Initialization Changes

## Summary

Updated the application to automatically initialize the database on first run, ensuring that Division and Job Title dropdowns are always populated with data.

## Changes Made

### 1. Updated `App.java`

**File**: `src/main/java/com/employeemgmt/ui/App.java`

**Changes**:
- Added automatic database initialization on application startup
- Calls `DatabaseInit.initializeIfNeeded()` before building DAOs and services
- Ensures database is ready before the UI loads

**Code Added**:
```java
// Initialize database (creates tables and loads sample data if needed)
System.out.println("Checking database initialization...");
DatabaseInit dbInit = new DatabaseInit();
if (dbInit.initializeIfNeeded()) {
    System.out.println("Database ready.");
} else {
    System.err.println("Warning: Database initialization had issues, but continuing...");
}
```

### 2. Enhanced `DatabaseInit.java`

**File**: `src/main/java/com/employeemgmt/db/DatabaseInit.java`

**Changes**:
- Added new method `initializeIfNeeded()` that checks if initialization is required
- Only initializes if the `division` table is empty or doesn't exist
- Safe to call on every application startup
- Existing `initialize()` method remains for manual/forced initialization

**New Method**:
```java
public boolean initializeIfNeeded() {
    // Checks if division table exists and has data
    // Only initializes if needed
    // Returns true if successful or not needed
}
```

### 3. Enhanced `start-mysql.sh`

**File**: `src/db/start-mysql.sh`

**Changes**:
- Added `--init` flag to initialize database after starting MySQL
- New `initialize_database()` function that:
  - Creates database if it doesn't exist
  - Executes `schema.sql` to create tables
  - Executes `sample-data.sql` to load sample data
- Can now be used as: `./src/db/start-mysql.sh --init`

**New Usage**:
```bash
# Just start MySQL
./src/db/start-mysql.sh

# Start MySQL and initialize database
./src/db/start-mysql.sh --init
```

### 4. Fixed `EmployeeFormController.java`

**File**: `src/main/java/com/employeemgmt/ui/fx/controller/EmployeeFormController.java`

**Changes**:
- Added custom cell factories for Division and Job Title ComboBoxes
- ComboBoxes now display clean names instead of `toString()` output
- Shows "Engineering" instead of "Division[ID: 1, Name: Engineering]"
- Added validation to prevent saving without selecting Division/Job Title

**UI Improvements**:
- Division dropdown shows: Engineering, Sales, Marketing, etc.
- Job Title dropdown shows: Software Engineer, Sales Manager, etc.
- User-friendly error messages if selections are missing

### 5. Fixed `Employee.java` Model

**File**: `src/main/java/com/employeemgmt/model/Employee.java`

**Changes**:
- Fixed `setEmployeeId()` method that was incorrectly setting `email` field
- Now properly sets the `employeeId` field

**Bug Fix**:
```java
// Before (WRONG):
public void setEmployeeId(Integer employeeId) {
    this.email = (email == null) ? null : email.trim();
}

// After (CORRECT):
public void setEmployeeId(Integer employeeId) {
    this.employeeId = employeeId;
}
```

### 6. Fixed `ReportsController.java`

**File**: `src/main/java/com/employeemgmt/ui/fx/controller/ReportsController.java`

**Changes**:
- Renamed `colJobName` to `jobNameColumn` to fix FXML injection issue
- Updated corresponding FXML file to match

### 7. Created Database Documentation

**File**: `src/db/README.md`

**Contents**:
- Comprehensive guide to database setup and initialization
- Three initialization options (automatic, script-based, manual)
- Troubleshooting guide for common issues
- Schema overview and sample data description

## How It Works Now

### Automatic Initialization Flow

1. User starts the application: `mvn clean javafx:run`
2. `App.start()` calls `DatabaseInit.initializeIfNeeded()`
3. `DatabaseInit` checks if `division` table exists and has data
4. If empty or missing:
   - Executes `src/db/schema.sql` to create tables
   - Executes `src/db/sample-data.sql` to load data
5. Application continues with populated database
6. Division and Job Title dropdowns work correctly

### User Experience

**Before**:
- Empty dropdowns
- "Please select a division" error with no options
- Manual database setup required

**After**:
- Dropdowns automatically populated on first run
- Clean display names (e.g., "Engineering" not "Division[ID: 1, Name: Engineering]")
- No manual setup needed
- Sample data includes 5 divisions, 15 job titles, 15 employees

## Testing

To test the changes:

1. **Fresh Database Test**:
   ```bash
   # Drop existing database
   mysql -u root -p -e "DROP DATABASE IF EXISTS emp_mgmt;"
   
   # Start application (will auto-initialize)
   mvn clean javafx:run
   
   # Check Division dropdown - should show 5 divisions
   ```

2. **Existing Database Test**:
   ```bash
   # Start application (should skip initialization)
   mvn clean javafx:run
   
   # Console should show: "Database already initialized with data. Skipping initialization."
   ```

3. **Manual Script Test**:
   ```bash
   # Initialize via script
   ./src/db/start-mysql.sh --init
   
   # Should see success messages for schema and sample data
   ```

## Benefits

1. **Zero-Configuration Setup**: New developers can run the app immediately
2. **Idempotent**: Safe to run multiple times, won't duplicate data
3. **Fail-Safe**: Checks before initializing, handles existing data gracefully
4. **User-Friendly**: Clean dropdown displays, helpful error messages
5. **Flexible**: Three ways to initialize (automatic, script, manual)

## Files Modified

- ✅ `src/main/java/com/employeemgmt/ui/App.java`
- ✅ `src/main/java/com/employeemgmt/db/DatabaseInit.java`
- ✅ `src/main/java/com/employeemgmt/ui/fx/controller/EmployeeFormController.java`
- ✅ `src/main/java/com/employeemgmt/model/Employee.java`
- ✅ `src/main/java/com/employeemgmt/ui/fx/controller/ReportsController.java`
- ✅ `src/main/resources/com/employeemgmt/ui/fx/reports.fxml`
- ✅ `src/db/start-mysql.sh`

## Files Created

- ✅ `src/db/README.md` - Database setup documentation
- ✅ `DATABASE_CHANGES.md` - This file

## No Changes Needed

- ✅ `src/db/schema.sql` - Already uses `CREATE TABLE IF NOT EXISTS`
- ✅ `src/db/sample-data.sql` - Already uses `INSERT IGNORE` and `ON DUPLICATE KEY UPDATE`
