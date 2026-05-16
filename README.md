<div align="center">

# 📚 Namma-Pustaka 🌱
### *Smart Library Assistant for Rural Schools*

<img src="https://img.shields.io/badge/Platform-Android-green?style=for-the-badge">
<img src="https://img.shields.io/badge/Language-Kotlin-purple?style=for-the-badge">
<img src="https://img.shields.io/badge/Database-RoomDB-blue?style=for-the-badge">
<img src="https://img.shields.io/badge/UI-Jetpack%20Compose-red?style=for-the-badge">
<img src="https://img.shields.io/badge/Architecture-MVVM-yellow?style=for-the-badge">
<img src="https://img.shields.io/badge/Scanning-ML%20Kit-success?style=for-the-badge">

</div>

---

# 📖 About Namma-Pustaka

**Namma-Pustaka** is a digital solution designed to transform simple school bookshelves into organized digital catalogs. Tailored for village schools, it empowers both students and teachers by providing a transparent and easy-to-use library management system.

The application acts as a **Digital Librarian**, helping to:
- Catalog books efficiently.
- Track borrows and returns via QR codes.
- Encourage a healthy reading culture through leaderboards and reviews.

---

# ❗ Problem Statement

In many rural schools, libraries are often just a cupboard of books with no formal tracking system. This leads to:
- **Lack of Awareness:** Students don't know which books are available.
- **Management Struggles:** Teachers find it difficult to track who has borrowed which book.
- **Resource Loss:** Books frequently go missing and are never returned.
- **Low Engagement:** A lack of organized access hinders the development of a reading culture among students.

Namma-Pustaka solves these issues by digitizing the entire process, making the library accessible and manageable via a simple Android device.

---

# 🎯 Vision

Namma-Pustaka aims to be the **"Smart Library Assistant"** that brings digital literacy and organization to the heart of rural education.

Our goals are:
- **Digital Cataloging:** Turning every shelf into a searchable digital gallery.
- **Seamless Tracking:** Using QR technology to replace manual registers.
- **Community Engagement:** Letting students review books and compete on a "Reading Leaderboard."

---

# ✨ Features

## 🔍 Smart Book Catalog
Browse the library collection categorized by subjects like:
- Story 📖
- Science 🧪
- History 🏛️
- Literature ✍️
- *Search books instantly by Title or Author.*

---

## 📲 QR Borrow & Return
Efficient tracking using **Google ML Kit**:
- Scan a QR code on a book to "Issue" it to a student profile.
- Return books with a quick scan, automatically updating the system.

---

## ⭐ Review Corner
Students can engage with the content by:
- Giving a "Star Rating."
- Writing a one-sentence review to share their thoughts with peers.

---

## 🏆 Reading Leaderboard
A gamified experience that:
- Tracks the number of pages read by each student.
- Displays a monthly ranking to encourage healthy competition and literacy.

---

## 👤 Student Management
Teachers can register and manage student profiles, including:
- Name
- Student ID
- Grade/Class

---

## ⏰ Overdue Reminders
The system automatically highlights overdue books:
- "Overdue" status turns **Red** to alert teachers for follow-up.

---

## 🌙 Modern UI & Dark Mode
Built with **Jetpack Compose** following Material 3 guidelines for:
- High readability.
- Intuitive navigation.
- Eye-friendly Dark Mode support.

---

# 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin | Primary Programming Language |
| Jetpack Compose | Modern UI Framework |
| Room Database | Local Data Persistence |
| Google ML Kit | QR/Barcode Scanning |
| MVVM Architecture | Scalable App Structure |
| Navigation Compose | In-app Routing |
| Coil | Asynchronous Image Loading |

---

# 📂 Folder Structure

```plaintext
app/src/main/java/com/example/vinnu/
├── data/
│   ├── Entities.kt         # Room Data Entities (Book, Student, Transaction, Review)
│   ├── LibraryDao.kt      # Data Access Object for Database queries
│   └── LibraryDatabase.kt  # Room Database Configuration
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt        # Digital Shelf & Catalog
│   │   ├── AddBookScreen.kt     # Camera-based book entry
│   │   ├── ScannerScreen.kt     # QR Scanning logic
│   │   ├── BookDetailScreen.kt  # Reviews & Metadata
│   │   ├── StudentScreen.kt     # Student Registration
│   │   ├── HistoryScreen.kt     # Lending logs & Overdue tracking
│   │   └── LeaderboardScreen.kt # Reading rankings
│   └── theme/
│       ├── Color.kt             # Brand colors (Primary, Accent, etc.)
│       ├── Type.kt              # Typography System
│       └── Theme.kt             # VinnuTheme implementation
├── viewmodel/
│   └── LibraryViewModel.kt # App logic and State Management
└── MainActivity.kt         # App Entry Point & Navigation Host
```

---

# 🚀 Installation & Setup

1. **Clone the Project:**
   ```bash
   git clone https://github.com/vinnu091/Namma-Pustaka.git
   ```
2. **Open in Android Studio:**
   - Select the `Vaishnavi_Vinnu` folder.
3. **Sync Gradle:**
   - Wait for dependencies to download.
4. **Run:**
   - Deploy to an Android device or emulator (API 26+).

---

# 📊 Success Metrics
- **Instant Search:** Find any book in < 100ms.
- **Accurate Scanning:** Reliable QR detection using ML Kit.
- **Automatic Highlighting:** Visual cues for overdue items.

---

# 👨‍💻 Developer
**Vaishnavi Vinnu**
*Android Developer | Innovating Education through GenAI*

---

<div align="center">

### 🌱 “A digital catalog is the first step toward a global reading culture.”

</div>
