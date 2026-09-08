
# Program Details

- Console-based MotorPH payroll system with two roles: employee and payroll_staff.
- Reads employee and attendance data from CSV files.
- The program starts with a login prompt and supports two user roles: `employee and `payroll_staff` with the password `12345`
- Employee can view basic employee info using employee number start **10001 - 10034** and password 12345.
- payroll_staff can process payroll for one employee or all employees.
- payroll_staff user can add/update/delete record from csv file through the system. 
- Payroll runs for June-December with two cutoffs per month (1-15, 16-31).
- Work hours count only between 8:00 AM and 5:00 PM, minus 1-hour lunch break.
- Computes gross pay, then deductions (SSS, PhilHealth, Pag-IBIG, tax) to get net pay.

## validation input 
`PayrollServiceChecker.java`

| Field                       | Required | Validation                                               | Valid Example                              |
| --------------------------- | :------: | -------------------------------------------------------- | ------------------------------------------ |
| **Last Name**               |   ✅ Yes  | Cannot be empty                                          | `Garcia`                                   |
| **First Name**              |   ✅ Yes  | Cannot be empty                                          | `Juan`                                     |
| **Birthday**                |   ✅ Yes  | Must be a valid date in **MM/DD/YYYY** format            | `08/15/1998`                               |
| **Phone Number**            |   ❌ No   | Must follow **XXX-XXX-XXX**                              | `966-860-270`                              |
| **SSS Number**              |   ✅ Yes  | Must follow **XX-XXXXXXX-X**                             | `12-1234567-8`                             |
| **PhilHealth Number**       |   ✅ Yes  | Exactly **12 digits**                                    | `123456789012`                             |
| **TIN**                     |   ✅ Yes  | Must follow **XXX-XXX-XXX-XXX**                          | `123-456-789-000`                          |
| **Pag-IBIG Number**         |   ✅ Yes  | Exactly **12 digits**                                    | `123456789012`                             |
| **Basic Salary**            |   ✅ Yes  | Numeric value ≥ 0. Commas and one decimal point allowed. | `25000`, `25,000`, `25000.50`, `25,000.50` |
| **Rice Subsidy**            |   ❌ No   | Numeric value ≥ 0                                        | `1500`, `1,500`, `1500.00`                 |
| **Phone Allowance**         |   ❌ No   | Numeric value ≥ 0                                        | `1000`, `1,000.50`                         |
| **Clothing Allowance**      |   ❌ No   | Numeric value ≥ 0                                        | `500`, `500.00`                            |
| **Gross Semi-Monthly Rate** |   ❌ No   | Numeric value ≥ 0                                        | `25000`, `25,000.50`                       |
| **Hourly Rate**             |   ✅ Yes  | Numeric value ≥ 0                                        | `535.71`, `535`, `1,000.50`                |

`AppChecker.java`
| Field        | Required | Validation                                             | Valid Example                        |
| ------------ | :------: | ------------------------------------------------------ | ------------------------------------ |
| **Username** |   ✅ Yes  | Cannot be empty and must be **50 characters or fewer** | `employee`, `payroll_staff`, `10001` |
| **Password** |   ✅ Yes  | Cannot be empty and must be **50 characters or fewer** | `12345`                              |

---

## GitHub Convention

This project also includes a [convention file](github_convention.md) to standardize development practices.

---

## Tech Stack

- Java 25
- Gradle
- Java Swing
- Spotless (formatter)

---

### `scripts/`
Contains development helper scripts.

`dev-reload.sh` watches project files and restarts the app when something changes on Linux, macOS, or WSL.

`dev-reload.bat` does the same for Windows users.

### `.github/workflows/`
Contains GitHub Actions workflows.

`build.yml` runs the Gradle build and tests on Linux, Windows, and macOS.

`formatter.yml` runs the Spotless formatter check.

## Run

```bash
./gradlew run
```

## Live Reload During Development

Plain Swing cannot hot-swap every Java code change inside an already running window. For all-file live reload during development, use the dev reload script:

Linux, macOS, or WSL:

```bash
./scripts/dev-reload.sh
```

Windows:

```bat
scripts\dev-reload.bat
```

This watches the project files and restarts the app when something changes, including Java source files, resources, Gradle files, and README changes. It ignores generated folders like `.git`, `.gradle`, `build`, `app/build`, and `app/bin`.

## Test

```bash
./gradlew test
```

## Build

```bash
./gradlew clean build

java -jar app/build/libs/app.jar
```

create installer artifacts:
```bash
# Linux
jpackage \
  --name MyApp \
  --input app/build/libs \
  --main-jar app.jar \
  --main-class org.example.App \
  --type deb \
  --linux-shortcut \
  --linux-menu-group "Utility" \
  --dest dist
  --verbose

# Windows
jpackage `
  --name MyApp `
  --input app/build/libs `
  --main-jar app.jar `
  --main-class org.example.App `
  --type exe `
  --dest dist
  --verbose

# MacOs
jpackage \
  --name MyApp \
  --input app/build/libs \
  --main-jar app.jar \
  --main-class org.example.App \
  --type dmg \
  --dest dist
  --verbose

# the MyApp.<extention> wil store inside of dist directory
```


## Format

This project uses Spotless with Google Java Format.

Check formatting:

```bash
./gradlew spotlessCheck
```

Apply formatting:

```bash
./gradlew spotlessApply
```

GitHub Actions runs both the build and formatter checks automatically on pushes and pull requests.
