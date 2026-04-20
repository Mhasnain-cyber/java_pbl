# String Processing Application (StringBuffer & StringTokenizer)

A premium interactive Java application built with **Spring Boot** to demonstrate Unit II PBL concepts: **StringBuffer** and **StringTokenizer**.

## 🚀 Quick Run Guide

Since your system uses specific paths for Java and Maven, use the following commands in your PowerShell terminal to run the project.

### Step 1: Navigate to the project
```powershell
cd "c:\Users\MOHD HASNAIN\OneDrive\Desktop\pslp\hasnainjavapbl"
```

### Step 2: Build the project
Copy and paste this command to build the JAR file:
```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot"; $env:Path += ";$env:JAVA_HOME\bin;C:\maven\apache-maven-3.9.6\bin"; & "C:\maven\apache-maven-3.9.6\bin\mvn.cmd" clean package -DskipTests
```

### Step 3: Start the Application
Run this command to start the server:
```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot"; $env:Path += ";$env:JAVA_HOME\bin"; & "$env:JAVA_HOME\bin\java.exe" -jar "target\string-processing-1.0.0.jar"
```

### Step 4: Open in Browser
Go to: **[http://localhost:8081](http://localhost:8081)**

---

## 💡 Key Features
- **Interactive StringBuffer Ops**: Append, Insert, Delete, Reverse, Replace, and more.
- **StringTokenizer Demo**: Split strings using custom delimiters and count tokens.
- **Java Code Generator**: See the exact Java code for every operation you perform in real-time.
- **Premium UI**: Modern dark-mode dashboard with glassmorphism effects.

## 🛠 Tech Stack
- **Backend**: Java 21, Spring Boot 3.2.5
- **Frontend**: HTML5, Vanilla CSS, JavaScript
- **Communication**: STOMP over WebSocket for live logs
