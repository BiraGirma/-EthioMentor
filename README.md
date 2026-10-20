# Ethiomentor 🌟

[![GitHub Repo](https://img.shields.io/badge/GitHub-Ethiomentor-blue?logo=github)](https://github.com/BiraGirma/-EthioMentor)
[![License](https://img.shields.io/badge/License-Educational%20Use-green?logo=book)]

Ethiomentor is a **peer mentoring platform for Ethiopian university students**, offering **chat 💬**, **study groups 📚**, and **mentor matching 🤝** to support students in academic collaboration and mentorship.

---

## Table of Contents 📑

1. [Project Overview](#project-overview-)
2. [Project Structure](#project-structure-)
3. [Features](#features-)
4. [Technologies Used](#technologies-used-)
5. [Installation & Setup](#installation--setup-)
6. [Screenshots](#screenshots-)
7. [Contributors](#contributors-)
8. [License](#license-)

---

## Project Overview ✨

Ethiomentor connects university students with peers and mentors to enhance academic performance and collaboration. The platform supports:

* **User Registration & Login 🔐** – Secure authentication for students and admins.
* **Dashboard 📊** – Overview of activities, groups, and chats.
* **Mentor Matching 🤝** – Connect mentees with available mentors.
* **Study Groups 📚** – Create, join, and manage study groups.
* **Chat System 💬** – Real-time messaging between users.

The backend is built using **Java Servlets ☕** and **JSP**, with a **PostgreSQL 🐘** database, and the frontend uses HTML, CSS, and JavaScript.

---

## Project Structure 🗂️

```
Ethiomentor/
├── build.sh              # build without an IDE (see Linux Mint section)
├── scripts/
│   ├── test.sh           # automated tests (add --db for database tests)
│   └── smoke-test.sh     # end-to-end HTTP test against a running server
└── src/
    └── main/
        ├── java/
        │   └── com/ethiomentor/
        │       ├── config/
        │       │   ├── DBConfig.java
        │       │   └── DBInit.java
        │       ├── controller/
        │       │   ├── AdminServlet.java
        │       │   ├── AuthServlet.java
        │       │   ├── ChatWebSocket.java
        │       │   ├── CreateStudyGroupServlet.java
        │       │   ├── DashboardServlet.java
        │       │   ├── GroupServlet.java
        │       │   ├── JoinStudyGroupServlet.java
        │       │   ├── LogoutServlet.java
        │       │   ├── MentorMatchingServlet.java
        │       │   ├── MessageServlet.java
        │       │   ├── LoadMessagesServlet.java
        │       │   ├── MentorDashboardServlet.java
        │       │   ├── RegisterServlet.java
        │       │   ├── StartupServlet.java
        │       │   ├── StudyGroupServlet.java
        │       │   └── UserServlet.java
        │       ├── dao/
        │       │   ├── ChatDAO.java
        │       │   ├── ConversationDAO.java
        │       │   ├── GroupDAO.java
        │       │   ├── MentorDAO.java
        │       │   ├── MessageDAO.java
        │       │   ├── MessageStatusDAO.java
        │       │   ├── StudyGroupDAO.java
        │       │   └── UserDAO.java
        │       ├── model/
        │       │   ├── ChatMessage.java
        │       │   ├── ChatRoom.java
        │       │   ├── Conversation.java
        │       │   ├── MenteeProfile.java
        │       │   ├── MentorProfile.java
        │       │   ├── MessageStatus.java
        │       │   ├── StudyGroup.java
        │       │   └── User.java
        │       ├── service/
        │       │   ├── AdminService.java
        │       │   ├── AuthService.java
        │       │   ├── ChatService.java
        │       │   ├── MentorService.java
        │       │   └── UserService.java
        │       └── util/
        │           ├── JsonUtil.java
        │           └── PasswordUtil.java
        ├── resources/
        │   └── application.properties (optional)
        └── webapp/
            ├── META-INF/
            │   └── MANIFEST.MF
            ├── WEB-INF/
            │   ├── web.xml
            │   ├── jsp/
            │   │   └── includes/
            │   │       ├── header.jsp
            │   │       ├── footer.jsp
            │   │       └── sidebar.jsp
            │   └── lib/
            │       ├── json-20230618.jar
            │       ├── jstl-1.2.jar
            │       └── postgresql-42.7.8.jar
            ├── css/
            │   ├── chat.css
            │   ├── dashboard.css
            │   ├── global.css
            │   ├── landing.css
            │   ├── login.css
            │   ├── mentormatching.css
            │   ├── register.css
            │   ├── sidebar.css
            │   └── studygroups.css
            ├── js/
            │   ├── chat.js
            │   ├── landing.js
            │   ├── login.js
            │   ├── studygroups.js
            │   └── admin.js
            ├── index.jsp
            ├── login.jsp
            ├── register.jsp
            ├── dashboard.jsp
            ├── chat.jsp
            ├── MentorMatching.jsp
            ├── mentorDashboard.jsp
            ├── admin.jsp
            └── studygroups.jsp
```

---

## Features 🌟

* Role-based authentication (Admin, Mentor, Mentee) 🔐
* Real-time chat using WebSockets 💬
* Mentor-mentee matching system 🤝
* Study group creation and management 📚
* Dashboard with activity overview 📊
* Secure password handling and session management 🔒

---

## Technologies Used 🛠️

* **Backend:** Java Servlets ☕, JSP 📄
* **Frontend:** HTML 🌐, CSS 🎨, JavaScript ⚡
* **Database:** PostgreSQL 🐘
* **Server:** Apache Tomcat 9 🖥️
* **Libraries:** JSON 📦, JSTL 📚

---

## Installation & Setup ⚙️

### Run on Linux Mint (no IDE required)

Target platform: Linux Mint 22.x. Needs a JDK (17 or newer), PostgreSQL, and Apache Tomcat 9. Nothing else is required — no Maven or Gradle.

**1. Install Java and PostgreSQL**

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk postgresql postgresql-client
```

**2. Set a password for the PostgreSQL `postgres` user**

```bash
sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'choose-a-db-password';"
```

**3. Install Tomcat 9**

Download the Tomcat **9.0.x** "core" `tar.gz` from https://tomcat.apache.org/download-90.cgi and extract it, for example to `~/apache-tomcat-9.0.x`. Tomcat 10 and newer will **not** work (the code uses `javax.*` APIs).

**4. Build the WAR**

```bash
cd ~/path/to/EthioMentor
export CATALINA_HOME=$HOME/apache-tomcat-9.0.x
./build.sh            # produces build/EthioMentor.war
```

**5. Deploy and start**

```bash
export DB_PASSWORD='choose-a-db-password'
export ADMIN_INITIAL_PASSWORD='choose-an-admin-password'   # used only when the admin account is first created
cp build/EthioMentor.war "$CATALINA_HOME/webapps/"
"$CATALINA_HOME/bin/catalina.sh" run
```

Keep the terminal open while the server runs. Stop it with `Ctrl+C`.

**6. Open the app**

http://localhost:8080/EthioMentor/

On first start the app creates the `ethiomentors` database and all tables. The admin account is `admin`; its password is the value of `ADMIN_INITIAL_PASSWORD`. If that variable was not set, a one-time password is printed in `$CATALINA_HOME/logs/catalina.out`. Log in and change it.

**Configuration reference (environment variables)**

| Variable | Default | Purpose |
|---|---|---|
| `DB_PASSWORD` | none (required) | PostgreSQL password |
| `DB_URL` | built from the variables below | Full JDBC URL, overrides the rest |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `ethiomentors` | Database name |
| `DB_USER` | `postgres` | Database user |
| `ADMIN_INITIAL_PASSWORD` | random, printed once | Password for the first `admin` account |

Never put real passwords in files that are committed to Git. Export them in your shell, or in a `bin/setenv.sh` file inside your Tomcat folder (not in this repository).

**Tests**

```bash
CATALINA_HOME=$HOME/apache-tomcat-9.0.x scripts/test.sh               # unit tests
CATALINA_HOME=$HOME/apache-tomcat-9.0.x DB_PASSWORD='...' scripts/test.sh --db   # + database tests
```

The database tests use scratch databases named `ethiomentor_test` and `ethiomentor_migration_test`, which are dropped and recreated. Your real database is not touched.

With the server running:

```bash
ADMIN_PASSWORD='your-admin-password' scripts/smoke-test.sh
```

Only run the smoke test against a development database; it creates throw-away users and groups.

**Troubleshooting**

- `Cannot find Tomcat's servlet-api.jar` — `CATALINA_HOME` is not set or does not point to Tomcat 9.
- `Database initialization failed` — PostgreSQL is not running (`sudo systemctl status postgresql`) or `DB_PASSWORD` is wrong. Check `logs/catalina.out`.
- Port 8080 already in use — stop the other program, or change the `Connector` port in `$CATALINA_HOME/conf/server.xml`.
- Old `admin/admin123` account still exists — the server logs a security warning. Change that password.

**Known limitations**

- Mentor-to-mentee assignment is not implemented; the mentor dashboard's mentee count is derived from existing conversations.
- The Java 17 build was not run on Tomcat in the environment where this README was written; treat the commands above as the expected path and report any failure.
- Git history contains a database password from an earlier version of `DBConfig.java`. That password must be rotated; removing it from the current files does not remove it from history.

---

### Project Setup Guide (Eclipse + JDK 18 + Apache Tomcat)

This project is a **Java Web Application** developed using **Eclipse IDE**, **JDK 18**, and **Apache Tomcat**. Follow the steps below **exactly** to ensure the project runs correctly.

---

### 1. Prerequisites

Ensure the following are installed:

* Java Development Kit (JDK) 18
* Eclipse IDE for Enterprise Java and Web Developers
* Apache Tomcat 9.0.x
* Git

---

### 2. Install JDK 18

Verify installation:

```bash
java --version
```

---

### 3. Install Eclipse

Download **Eclipse IDE for Enterprise Java and Web Developers** (not plain Java).

---

### 4. Configure Eclipse to Use JDK 18

`Window → Preferences → Java → Installed JREs` → Add JDK 18 → Set as default.

---

### 5. Install and Configure Tomcat 10.1

Add Tomcat 9.0.*  to Eclipse via the **Servers** view.

---

### 6. Clone the Project

```bash
git clone https://github.com/BiraGirma/-EthioMentor.git
```

Import as an **Existing Eclipse Project**.

---

### 7. Fix Project Configuration

* Enable **Project Facets** (Java 18, Dynamic Web Module 4.0)
* Set **Target Runtime** to Tomcat 9.0.*
* Ensure **Java Build Path** uses JDK 18

---

### 8. Run the Project

Run the project on **Tomcat 9.0.* ** using **Run on Server**.

---

### 9. Common Mistakes

* Using JRE instead of JDK
* Using Tomcat 10/11
* Missing Project Facets

---

### 10. Instructor Execution Steps

1. Install JDK 18
2. Install Eclipse Enterprise
3. Install Tomcat 9.0
4. Import project
5. Fix facets if prompted
6. Run on server

---

## 🔐 Admin Panel Screenshots

| Admin Dashboard                                      | 
| ---------------------------------------------------- | 
| ![admin-dashboard](assets/admin-dashboard.png) | 

---

## Screenshots 📸

| Landing Page                   | Login Page                 | Sign Up Page                 |
| ------------------------------ | -------------------------- | ---------------------------- |
| ![landing](assets/landing.png) | ![login](assets/login.png) | ![signup](assets/signup.png) |

| Dashboard                          | Mentor Matching                              | Study Groups                         | Chat / Messages          |
| ---------------------------------- | -------------------------------------------- | ------------------------------------ | ------------------------ |
| ![dashboard](assets/dashboard.png) | ![mentormatching](assets/mentormatching.png) | ![studygroup](assets/studygroup.png) | ![chat](assets/chat.png) |


---

## Contributors 👥

This project was developed as a **group project for educational purposes** by:

* [Bira Girma](https://github.com/BiraGirma)  UGR/34107/16
* [Biniyam Lema](https://github.com/ben-on-tech) UGR/34018/16
* [Hanif Esmail](https://github.com/hanifo) UGR/30629/15
* [Kalkidan Birhane](https://github.com/pro-kal) UGR/34711/16
* [Tewodros Mesfin](https://github.com/tedacodder) UGR/35497/16

---

## License 📖

This project is for **educational purposes only**.

---

## GitHub Repository 🔗

[https://github.com/BiraGirma/-EthioMentor](https://github.com/BiraGirma/-EthioMentor)
