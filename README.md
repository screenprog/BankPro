# BankPro Documentation

## Project Overview
The Bank Account Management System is a web-based application designed to streamline banking operations for admins, staff, and users. This system includes a secure backend built with Java Spring Boot and a frontend developed for user interaction. The project is currently under development, with several features already functional and others planned for future updates.
- Frontend is live on [Netlify](https://resonant-caramel-052c2b.netlify.app)
- Backend is live on [Render](https://bankpro.onrender.com)
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

#### Prerequisites
- Download the `.rar` file from [releas v1.0.3](https://github.com/screenprog/BankPro/releases) section, which contains:
  - Backend Rest API
  - Data-Volume
  - Front-end folder
  
### Database Setup

#### Prerequisites
- [PostgreSQL](https://www.postgresql.org/docs/)
#### Steps
1. **Create a Database**:

   Login into your postgres account and run the following command:

    ```CREATE DATABASE <database_name>;```

2. **Import Data**:
   Navigate to Data-Volume folder which contains the `backup.dump` file and run the following command to import the data:
   ```
   pg_restore -U <username> -d <database_name> -h <hostname> -p <port> -W ./db_backup.dump
   ```

3. **Verify the Database**:
   ```
   psql -U <username> -d <database_name>;
   ```

### Backend REST API Setup

#### Prerequisites
- [JDK version 21](https://www.oracle.com/in/java/technologies/downloads/#java21) or any latest one


### Enviroment variables:
- DATASOURCE_URL = `jdbc:<database_url>`
- DATASOURCE_USERNAME = `database_username`
- DATASOURCE_PASSWORD = `databse_password`
- MY_EMAIL = `your email id`
- MY_EMAIL_APP_PASS = `email app password or email password`
- MAIL_HOST = `smtp.gmail.com` is a gmail host
- MAIL_PORT = `587` is a gmail port
- FRONTEND_URL = `http://127.0.0.1:5500`

#### Steps
1. Navigate to Backend Rest API folder which contains the jar file.
2. Run the jar file:
   ```
   java -jar BankPro-v1.0.3.jar
   ```
### Frontend Setup

#### Prerequisites
- [VS Code](https://code.visualstudio.com/) with [Live Server](https://marketplace.visualstudio.com/items?itemName=ritwickdey.LiveServer) extension

#### Steps
1. Open the `index.html` file in VS Code.
2. Run the file using the Live Server extension.

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
- User login functionality using email ID and customer ID both.
- Development of the admin interface for the frontend.

---

## Contributions
Contributions to the project are welcome! Future updates and new features will be pushed to the repository. For any issues or suggestions, feel free to raise an issue in the repository or contact me.

---

For more details, refer to the project files and codebase.
