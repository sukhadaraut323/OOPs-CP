# 🗳️ Election Simulator

A full-featured **Election Management System** built in Java, demonstrating all core Object-Oriented Programming (OOP) concepts. This project simulates a real-world electronic voting system with both a Console interface and a professional Swing GUI.

---

## 📌 Project Overview

| Detail            | Info               |
|-------------------|--------------------|
| **Language**      | Java               |
| **GUI Framework** | Java Swing         |
| **Type**          | OOP Course Project |
| **College**       | VIT Pune           |

---

## 🎯 OOP Concepts Covered

| Concept                | Where Used                                                              |
|------------------------|-------------------------------------------------------------------------|
| **Abstraction**        | `Person`, `Election`, `VotingMethod` are abstract classes               |
| **Encapsulation**      | All fields are private with getters/setters                             |
| **Inheritance**        | `Voter`, `Candidate`, `ElectionOfficer` extend `Person`                 |
| **Polymorphism**       | `conductElection()`, `calculateWinner()`, `verifyIdentity()` overridden |
| **Interface**          | `Votable`, `Verifiable` implemented by `Voter` and `Candidate`          |
| **Constructor**        | Parameterized constructors in every class                               |
| **Method Overriding**  | `calculateWinner()`, `dualVerify()`, `getDetails()`                     |
| **Object Interaction** | `Voter` ↔ `Ballot` ↔ `Election` ↔ `Result`                              |

---

## ✨ Features

### Admin Panel
- Secure admin login (Officer ID + Password)
- Setup election with name and dates
- Add constituencies, parties, and candidates
- Open and close election
- View all registered voters
- View all cast ballots
- Announce election results

### Voter Panel
- Voter registration with full validation
- Secure login via Voter ID
- View candidates in their constituency
- Cast vote via EVM (Electronic Voting Machine)
- Get ballot receipt after voting
- View personal profile

### Security & Validation
- **VoterID Validation** — exactly 10 alphanumeric characters
- **Aadhaar Validation** — exactly 12 digits, starts with 2-9
- **PAN Validation** — format ABCDE1234F
- **Phone Validation** — 10 digits starting with 6-9
- **Age Validation** — must be 18 or older
- **Duplicate Name Detection** — dual document verification for candidates with same name
- **One vote per voter** — enforced strictly
- **Election must be open** — voting blocked otherwise

### Results
- Constituency-wise winner announcement
- Party-wise seat count with progress bars
- Voter turnout statistics
- Margin of victory display
- Printable results report

---

## 🏗️ Project Structure

```
OOPs-CP/
├── src/
│   ├── interfaces/
│   │   ├── Votable.java
│   │   └── Verifiable.java
│   │
│   ├── persons/
│   │   ├── Person.java           (Abstract)
│   │   ├── Voter.java
│   │   ├── Candidate.java
│   │   └── ElectionOfficer.java
│   │
│   ├── core/
│   │   ├── Party.java
│   │   ├── Constituency.java
│   │   ├── Ballot.java
│   │   ├── DocumentVerifier.java
│   │   └── Result.java
│   │
│   ├── election/
│   │   ├── Election.java         (Abstract)
│   │   └── GeneralElection.java
│   │
│   ├── voting/
│   │   └── EVMVoting.java
│   │
│   ├── utils/
│   │   └── Validator.java
│   │
│   ├── console/
│   │   └── ConsoleApp.java
│   │
│   └── gui/
│       ├── MainFrame.java
│       ├── LoginScreen.java
│       ├── RegisterScreen.java
│       ├── AdminDashboard.java
│       ├── VoterDashboard.java
│       ├── BallotScreen.java
│       └── ResultScreen.java
│
└── out/                          (compiled .class files)
```

---

## 🚀 How to Run

### Prerequisites
- Java JDK 17 or higher
- Any IDE (VS Code, IntelliJ, Eclipse) or terminal

### Compile

```bash
# Navigate to src folder
cd OOPs-CP/src

# Create output folder
mkdir ../out

# Compile all files
javac -encoding UTF-8 -d ../out console/*.java core/*.java election/*.java gui/*.java interfaces/*.java persons/*.java utils/*.java voting/*.java
```

### Run GUI App
```bash
java -cp ../out gui.MainFrame
```

### Run Console App
```bash
java -cp ../out console.ConsoleApp
```

---

## 🔐 Default Admin Credentials

```
Officer ID : ADMIN001
Password   : admin@123
```

---

## 🧪 Demo Data

### Election Setup
```
Election ID   : ELEC2024
Election Name : Indian General Election 2024
Start Date    : 01-04-2024
End Date      : 07-04-2024
```

### Constituencies
| ID   | Name         |
|------|--------------|
| C001 | Mumbai North |
| C002 | Pune Central |
| C003 | Nagpur East  |

### Parties
| ID  | Name                     | Symbol|
|-----|--------------------------|-------|
| BJP | Bharatiya Janata Party   | Lotus |
| INC | Indian National Congress | Hand  |
| AAP | Aam Aadmi Party          | Broom |

### Sample Voter
```
Full Name       : Amit Kumar
Age             : 28
Phone           : 9876512345
Voter ID        : VTR1234567
Aadhaar / PAN   : 987654321012
Constituency ID : C001
```

---

## 📋 Validation Rules

| Field    | Rule                                                        |
|----------|-------------------------------------------------------------|
| Voter ID | Exactly 10 alphanumeric characters (e.g. `ABC1234567`)      |
| Aadhaar  | Exactly 12 digits, starts with 2-9 (e.g. `234567891234`)    |
| PAN      | Format: 5 letters + 4 digits + 1 letter (e.g. `ABCDE1234F`) |
| Phone    | 10 digits, starts with 6-9                                  |
| Age      | Between 18 and 120                                          |
| Name     | Letters and spaces only, minimum 3 characters               |

---

## 🔄 Application Flow

```
Admin Login
    └── Setup Election
    └── Add Constituency
    └── Add Party
    └── Add Candidate (with duplicate name dual-verification)
    └── Open Election

Voter Registration
    └── Validate all fields
    └── Check constituency exists

Voter Login
    └── Verify Voter ID format
    └── Check election is scheduled
    └── Find voter in system

Voting (EVM)
    └── Check election is open
    └── Check voter hasn't voted
    └── Display ballot
    └── Confirm vote
    └── Generate receipt

Results
    └── Count votes per constituency
    └── Declare winner
    └── Show party seats
    └── Display statistics
```

---

## 👥 Class Diagram

```
Person (Abstract)
├── Voter          implements Votable, Verifiable
├── Candidate      implements Verifiable
└── ElectionOfficer

Election (Abstract)
└── GeneralElection

Interfaces
├── Votable        → castVote(), hasVoted()
└── Verifiable     → verifyIdentity(), dualVerify()

Core
├── Party
├── Constituency
├── Ballot
├── DocumentVerifier
└── Result

Voting
└── EVMVoting

Utils
└── Validator
```

---

## 📸 GUI Screens

| Screen          | Description                             |
|-----------------|-----------------------------------------|
| Login Screen    | Admin/Voter tab login with info panel   |
| Register Screen | Full voter registration with validation |
| Admin Dashboard | Sidebar navigation with stats overview  |
| Voter Dashboard | Personal voting portal                  |
| Ballot Screen   | EVM-style candidate selection           |
| Result Screen   | Party seats, winner, statistics         |

---

## 🛠️ Built With

- **Java 17+**
- **Java Swing** — GUI framework
- **Java AWT** — Layout and graphics
- **OOP Design Patterns** — Template Method, Strategy

---

## 📄 License

This project is built for educational purposes as part of an OOP course project at VIT Pune.

---