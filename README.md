# 🏨 HotelEase – Hotel Management System

A Java-based desktop application for managing hotel operations like room booking, guest management, and administrative tasks.

---

## 🚀 Features

* View and book available rooms
* Manage guest details
* Admin dashboard with room & guest summary
* Real-time updates using client-server architecture
* Interactive GUI using Java Swing

---

## 🛠️ Tech Stack

* Frontend: Java Swing
* Backend: Java (Sockets, JDBC)
* Database: Oracle Database
* Architecture: Client-Server

---

## ⚙️ Setup Instructions

### 1. Database Setup

1. Open Oracle SQL Developer / SQL*Plus
2. Run the SQL script provided in:
   hotelease_db_setup.txt
3. Make sure tables are created:

   * users
   * rooms
   * guests

---

### 2. Configure Database Connection

In all Java files, update:

DriverManager.getConnection(
"jdbc:oracle:thin:@localhost:1521:xe",
"your_username",
"your_password"
);

---

### 3. Run the Application

Start Server:
HotelEaseServer.java

Start Client:
ClientDashboard.java

Start Admin Panel:
LoginUI.java

---

## 🔐 Note on Security

This is a student-level project, so:

* Passwords are stored in plain text (for simplicity)
* Database credentials are placeholders
* No encryption or advanced authentication is implemented

In real-world applications:

* Passwords should be hashed
* Secure authentication should be used
* Credentials should not be hardcoded

---

## 📸 Screenshots

(Add your screenshots here)

---

## 📌 Disclaimer

This project is created for educational purposes.
All personal and system-specific details have been removed before publishing.

---

## 👨‍💻 Author

Anonymous Developer

---

## ⭐ Future Improvements

* Password hashing
* Payment integration
* Web or mobile version
* Advanced analytics dashboard
