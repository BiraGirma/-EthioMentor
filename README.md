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

- **User Registration & Login 🔐** – Secure authentication for students and admins.
- **Dashboard 📊** – Overview of activities, groups, and chats.
- **Mentor Matching 🤝** – Connect mentees with available mentors.
- **Study Groups 📚** – Create, join, and manage study groups.
- **Chat System 💬** – Real-time messaging between users.

The backend is built using **Java Servlets ☕** and **JSP**, with a **PostgreSQL 🐘** database, and the frontend uses HTML, CSS, and JavaScript.

---

## Project Structure 🗂️

```
Ethiomentor/
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
            │   └── studygroups.js
            ├── index.jsp
            ├── login.jsp
            ├── register.jsp
            ├── dashboard.jsp
            ├── chat.jsp
            ├── MentorMatching.jsp
            └── studygroups.jsp
```

---

## Features 🌟

- Role-based authentication (Admin, Mentor, Mentee) 🔐
- Real-time chat using WebSockets 💬
- Mentor-mentee matching system 🤝
- Study group creation and management 📚
- Dashboard with activity overview 📊
- Secure password handling and session management 🔒

---

## Technologies Used 🛠️

- **Backend:** Java Servlets ☕, JSP 📄
- **Frontend:** HTML 🌐, CSS 🎨, JavaScript ⚡
- **Database:** PostgreSQL 🐘
- **Server:** Apache Tomcat 9 🖥️
- **Libraries:** JSON 📦, JSTL 📚

---

## Installation & Setup ⚙️

1. **Clone the repository:**

```bash
git clone https://github.com/BiraGirma/-EthioMentor.git
```

2. **Configure database:**

   - Open `src/main/java/com/ethiomentor/config/DBConfig.java` and set your **port number, username, and password** for PostgreSQL.
   - The database and tables will be **automatically initialized** when you run the app.

3. **Deploy on Tomcat 9:**

   - Copy the project to the `webapps` folder of your Tomcat 9 installation.
   - Start the Tomcat server.

4. **Access the application:**

   - Open a web browser and navigate to:

   ```
   http://localhost:YOUR_PORT/index.jsp
   ```

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

- [Bira Girma](https://github.com/BiraGirma)
- [Biniyam Lema](https://github.com/ben-on-tech)
- [Hanif Esmail](https://github.com/hanifo)
- [Kalkidan Birhane](https://github.com/pro-kal)
- [Tewodros Mesfin](https://github.com/tedacodder)

---

## License 📖

This project is for **educational purposes only**.

---

## GitHub Repository 🔗

[https://github.com/BiraGirma/-EthioMentor](https://github.com/BiraGirma/-EthioMentor)
