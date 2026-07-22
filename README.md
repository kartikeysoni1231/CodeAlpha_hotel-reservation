# Grand Vista - Hotel Reservation System 🏨

A modern, desktop-based Hotel Reservation System built using **Java Swing** and designed with an elegant **Agoda-inspired Light Blue & White theme**. 

This system represents a clean separation of concerns using Object-Oriented Programming (OOP) design patterns. It handles room search filters, custom statistics visualizations, automated flat-file persistence, and real-time transaction processing.

---

## 🌟 Key Features

1. **Overview Dashboard**:
   - Live KPI metric cards tracking *Total Rooms*, *Available Rooms*, *Occupied Rooms*, and *Total Revenue*.
   - Custom-painted vector graphics for **Occupancy Analysis** (Donut Chart) and **Revenue by Category** (Bar Chart).
2. **Interactive Room Grid**:
   - Visual card representation for all rooms showing categories (Standard, Deluxe, Suite) with color-coded badges.
   - Live availability status tags.
   - Quick Action buttons: Quick-jump to booking page or view active guest booking details in a modal.
3. **Smart Booking Form**:
   - Real-time stay duration and total cost estimation as dates and rooms are adjusted.
   - Dynamic date checking (check-out must be after check-in).
4. **Reservations Log Table**:
   - A padded, modern flat list table displaying all active hotel bookings.
   - Custom cell status badge rendering (emerald green for **Paid**, warning amber for **Pending**).
   - Instant booking cancellation with automatic room availability restoration.
5. **Simulated Payment Gateway**:
   - Simulates checkouts via **Cash**, **Card**, or **UPI** and marks bookings as paid.
6. **Flat-File Database Persistence**:
   - Saves room status and bookings logs in simple, lightweight text files (`rooms.txt`, `reservations.txt`) so your data is preserved after closing the window.

---

## 🛠️ Technology Stack

* **Language**: Java 8+
* **Framework**: Java Swing & AWT (Standard Library)
* **Storage**: Local Flat Files (CSV-like storage)
* **Architecture**: Object-Oriented MVC (Models, Services, Views)

---

## 🚀 Getting Started

### Prerequisites
Make sure you have Java JDK (version 8 or newer) installed on your system. You can verify this in your terminal:
```bash
java -version
javac -version
```

### Installation
1. Clone this repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/hotel-reservation.git
   cd hotel-reservation
   ```

### Running on Windows (One-Click)
Double-click the **`run.bat`** file in the root of the project directory. This script will automatically:
1. Compile your Java files into the `bin/` output directory.
2. Launch the GUI dashboard on your desktop.

### Running in VS Code
1. Open the project folder in VS Code.
2. Install the **Extension Pack for Java** extension.
3. Open `src/Main.java` and click **Run** hovering above the `main` method, or press `F5`.

---

## 📁 File Structure
```
hotel-reservation/
├── .vscode/
│   └── launch.json            # VS Code debug launcher configs
├── src/
│   ├── Main.java              # Launcher class (configures AA fonts)
│   ├── MainFrame.java         # Swing GUI Dashboard Window
│   ├── Hotel.java             # Persistence & Business Logic controller
│   ├── Room.java              # Room model details
│   ├── Reservation.java       # Booking model details
│   └── TestBackend.java       # CLI testing suite
├── .gitignore                 # Excludes compiled files from git
├── README.md                  # Project documentation
├── rooms.txt                  # Local rooms database text template
└── run.bat                    # One-click Windows build script
```
