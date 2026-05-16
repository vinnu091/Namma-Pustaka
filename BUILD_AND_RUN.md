# 🚀 Namma-Pustaka: Build & Run Instructions

This document provides detailed instructions on how to set up, build, and run the **Namma-Pustaka** (Smart Library Assistant) application.

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
- **Android Studio** (Hedgehog or newer recommended)
- **JDK 17** (required for the latest Android Gradle Plugin)
- **Android SDK** (API Level 26 or higher)
- **Physical Device or Emulator** with Camera support (for testing the QR scanner)

---

## 🛠️ Installation & Setup

### 1. Clone & Open
- Clone the repository to your local machine.
- Open Android Studio and select **Open**, then navigate to the `Vaishnavi_Vinnu` folder.

### 2. Sync Gradle
- Allow Android Studio to download dependencies and sync the project structure.
- Ensure the build completes without errors.

---

## ⚡ Command Line Instructions (Windows PowerShell)

You can perform common tasks directly from the terminal within Android Studio or a standalone PowerShell window.

### Clean Project
```powershell
.\gradlew.bat clean
```

### Build Debug APK
```powershell
.\gradlew.bat assembleDebug
```

### Install and Run on Device
```powershell
# Optional: Uninstall previous version to reset the local database
adb uninstall com.example.vinnu

# Install and launch the debug build
.\gradlew.bat installDebug
```

---

## 🧪 Testing & Verification Flows

Once the app is running, follow these steps to verify core functionalities:

### 1. Catalog & Search
- **Action**: Scroll through the home screen grid. Use the search bar to type a book title or author.
- **Expected**: The list filters in real-time. Category chips (Science, Story, etc.) should correctly update the view.

### 2. Student Registration
- **Action**: Go to the **Students** tab and register a test student (e.g., "John Doe", ID: "S101", Grade: "10th").
- **Expected**: The student should appear in the "Registered Students" list.

### 3. QR Borrowing (Core Feature)
- **Action**: 
  1. Go to the **Scan** tab.
  2. Grant camera permissions.
  3. Scan a QR code (use any barcode or QR for testing).
  4. Once identified, search for the student you registered.
  5. Tap **Confirm & Issue**.
- **Expected**: The transaction should appear in the **History** tab, and the book's status on the Home screen should change to "Issued".

### 4. Overdue Indicators
- **Action**: Check the **History** tab for borrowed books.
- **Expected**: Any transaction older than 7 days that hasn't been returned must display the status text in **Red**.

### 5. Leaderboard
- **Action**: Return a book via the scanner.
- **Expected**: The student's "Pages Read" count should increase in the **Ranking** tab.

---

## 📊 Viewing Logs
To monitor real-time app activity or troubleshoot issues:
```powershell
adb logcat | select-string "com.example.vinnu"
```
