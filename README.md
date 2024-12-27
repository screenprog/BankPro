# BankPro Documentation

## Project Overview
The Bank Account Management System is a web-based application designed to streamline banking operations for admins, staff, and users. This system includes a secure backend built with Java Spring Boot and a frontend developed for user interaction. The project is currently under development, with several features already functional and others planned for future updates.

---

## Features

### Admin
- Access to all API endpoints.

### Staff
- Restricted to staff and user-related endpoints.
- Responsible for verifying or rejecting user applications.
- Manages deposits, withdrawals, and account creation.
- Can view all account details, customer details, and transactions.
- Handles operations such as adding customers, removing customers, removing accounts, and closing or suspending accounts (pending frontend implementation).

### User
- Restricted to user-related endpoints.
- Can register, log in, and create accounts.
- Needs staff assistance for depositing money to activate accounts.
- Can transfer money to another account.
- Can view account details and transaction history.

### Public Endpoints
- Login
- Email verification
- OTP validation
- Registration form
- Forgot password

### Registration Workflow
1. User provides a valid email address.
2. An OTP is sent to the provided email.
3. User verifies OTP and fills out the registration form, including setting a password.
4. After submission, the user receives an email confirming the application is under review.
5. Staff reviews and either approves or rejects the application.
6. Upon approval, the user receives an email with their customer ID.
7. User logs in and creates an account, with initial deposits facilitated by staff.

---

## How to Run the Project

### Database Setup

#### Prerequisites
- Podman
- Download the zip file from the release section, which contains:
  - `BankPro-v1.0.0.jar`
  - `ourbankdata.tar.gz`
  - Frontend folder

#### Steps
1. **Download Podman** and navigate to the folder containing the `ourbankdata.tar.gz` file.
2. **Start Podman Machine**:
   ```
   podman machine start
   ```
3. **Create a Volume**:
   ```
   podman volume create bankdatavolume
   ```
4. **Mount Data to Volume**:
   ```
   podman run --rm -v bankdatavolume:/data -v /d/path/to/volume:/host_mnt/n busybox tar xzf /host_mnt/d/ourbankdata.tar.gz -C /data
   ```
5. **Create Postgres Container**:
   ```
   podman run -d --name ourbankpro -e POSTGRES_PASSWORD=password -v bankdatavolume:/var/lib/postgresql/data -p 5437:5432 postgres:16-alpine
   ```

   **Note**: Ensure to use port `5437` as the jar file is configured to connect to this port.

### Backend Setup

#### Prerequisites
- JDK version 21 or any latest one

#### Steps
1. Navigate to the folder containing the jar file.
2. Run the jar file:
   ```
   java -jar BankPro-v1.0.0.jar
   ```

### Frontend Setup

#### Prerequisites
- VS Code with Live Server extension

#### Steps
1. Open the `login.html` file in VS Code.
2. Run the file using the Live Server extension.

   **Note**: Backend accepts requests from `http://127.0.0.1:5500/` only.

---

## Project Access

### Staff Credentials
- Email: `harsh.deep@example.com`
- Password: `harsh@123`
- Email: `deepak.joshi@example.com`
- Password: `deepak@123`

### User Credentials
- Customer ID: `<customer_id>`
- Password: `<name>@123` (where <name> is the customer's name)
---

## Future Updates
- User login functionality using email ID instead of customer ID.
- Implementation of forgot password feature.
- Development of the admin interface for the frontend.

---

## Contributions
Contributions to the project are welcome! Future updates and new features will be pushed to the repository. For any issues or suggestions, feel free to raise an issue in the repository or contact the project team.

---

For more details, refer to the project files and codebase.

