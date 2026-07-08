# ✈️ FinPilot AI - Smart Expense Tracker & Budget Manager

> A cutting-edge, production-ready Android application built entirely with **Jetpack Compose** and **Kotlin**. FinPilot AI leverages on-device **Machine Learning (OCR)** and **Gemini AI** to empower users to take absolute control of their personal finances through automated daily expense tracking, intelligent spending insights, smart notifications, and secure digital receipt parsing.

---

## 🏗️ **Architectural Design Patterns**

FinPilot AI is engineered using modern Android development best practices, ensuring a highly scalable, testable, and robust codebase:

┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                   │
│       (Jetpack Compose UI & Type-Safe Navigation)       │
└───────────────────────────┬───────────────────────────────┘
│  MVVM (StateFlow & Coroutines)
┌───────────────────────────▼───────────────────────────────┐
│                     ViewModel Layer                       │
│        (Handles UI State, CameraX, and AI Orchestration)  │
└───────────────────────────┬───────────────────────────────┘
│  Clean Architecture
┌───────────────────────────▼───────────────────────────────┐
│                Data & Cloud Infrastructure                │
│   (Room DB ──► Gemini AI ──► ML Kit OCR ──► Firebase)     │
└───────────────────────────────────────────────────────────┘


### 🛠️ **The Complete Tech Stack**
*   **Language:** Kotlin 🚀
*   **UI Framework:** Jetpack Compose (100% Declarative UI with Jetpack SplashScreen API)
*   **Core Architecture:** MVVM (Model-View-ViewModel) with reactive StateFlow and Clean Architecture principles
*   **Generative AI Integration:** Google Gemini AI SDK (`generativeai`) for smart, contextual financial insights and automation
*   **On-Device Machine Learning:** Google ML Kit Text Recognition (OCR) for lightning-fast receipt scanning
*   **Hardware Integration:** CameraX API for fluid, lifecycle-aware device camera access
*   **Cloud Backend & Infrastructure:** Firebase Ecosystem (Authentication, Firestore Cloud Database, Cloud Messaging Notifications, Analytics, and Crashlytics tracking)
*   **Dependency Injection:** Dagger Hilt (Compile-time safe DI)
*   **Local Caching & State Persistence:** Room Database (Offline transaction architecture) & Jetpack DataStore Preferences (User settings/Session management)
*   **Networking & Serialization:** Retrofit2 with OkHttp Logging Interceptor & KotlinX Serialization (Type-safe JSON parsing)
*   **Asynchronous Processing:** Kotlin Coroutines & Play Services Coroutines integration
*   **Image Loading:** Coil Compose (Asynchronous image pipelines)

---

## ✨ **Key Features**

*   **🔒 Secure Cloud Authentication:** Enterprise-grade User Sign-In and Cloud Registration flows powered by Firebase.
*   **🤖 On-Device OCR Receipt Scanning:** Scan physical bills using the integrated **CameraX** pipeline and instantly extract text/numbers using **Google ML Kit**.
*   **💡 Gemini AI Financial Insights:** Get personalized, deep financial advice and saving recommendations tailored entirely to your spending behaviors.
*   **📊 Dynamic Fin-Dashboard:** Real-time financial monitoring featuring live tracking metrics for Monthly Budgets, Total Spend, and Active Savings.
*   **🔔 Intelligent Push Notifications:** Real-time transaction alerts and system prompts driven by Firebase Cloud Messaging (FCM).
*   **🔍 Detailed Expense Logs:** Fully optimized local indexing engine using **Room DB** paired with custom category filters for browsing transaction histories.

---

## 📸 **App Walkthrough & User Interface**

### 1. Welcome & Onboarding Flow
| Splash Screen | 1. AI-Powered Finance | 2. Scan & Attach Receipts | 3. Track Your Budget |
| --- | --- | --- | --- |
| <img src="https://raw.githubusercontent.com/Samina2004/FinPilot-AI/main/WhatsApp%20Image%202026-07-05%20at%209.29.51%20AM.jpeg" width="220"> | <img src="https://raw.githubusercontent.com/Samina2004/FinPilot-AI/main/WhatsApp%20Image%202026-07-05%20at%209.29.51%20AM%20(1).jpeg" width="220"> | <img src="https://raw.githubusercontent.com/Samina2004/FinPilot-AI/main/WhatsApp%20Image%202026-07-05%20at%209.29.52%20AM.jpeg" width="220"> | <img src="https://raw.githubusercontent.com/Samina2004/FinPilot-AI/main/WhatsApp%20Image%202026-07-05%20at%209.29.52%20AM%20(1).jpeg" width="220"> |

### 2. User Authentication & Input Validation
| Sign In (Welcome Back) | Blank Registration | Dummy Data Validation |
| --- | --- | --- |
| <img src="https://raw.githubusercontent.com/Samina2004/FinPilot-AI/main/WhatsApp%20Image%202026-07-05%20at%209.29.54%20AM.jpeg" width="250"> | <img src="https://raw.githubusercontent.com/Samina2004/FinPilot-AI/main/WhatsApp%20Image%202026-07-05%20at%209.29.52%20AM%20(2).jpeg" width="250"> | <img src="https://raw.githubusercontent.com/Samina2004/FinPilot-AI/main/WhatsApp%20Image%202026-07-05%20at%209.29.54%20AM%20(1).jpeg" width="250"> |

### 3. Core Dashboard Progression & Smart Features
| Initial Setup (Empty State) | Active Dashboard & AI Insights | Expense Logging Setup |
| --- | --- | --- |
| <img src="https://raw.githubusercontent.com/Samina2004/FinPilot-AI/main/WhatsApp%20Image%202026-07-05%20at%209.29.55%20AM.jpeg" width="250"> | <img src="https://raw.githubusercontent.com/Samina2004/FinPilot-AI/main/WhatsApp%20Image%202026-07-05%20at%209.46.13%20AM.jpeg" width="250"> | <img src="https://raw.githubusercontent.com/Samina2004/FinPilot-AI/main/WhatsApp%20Image%202026-07-05%20at%209.46.12%20AM%20(1).jpeg" width="250"> |

### 4. Receipt Scanner Flow & History Filters
| Attach Receipt Init | Receipt Captured | Detailed Transaction Log | All Filtered Expenses |
| --- | --- | --- | --- |
| <img src="https://raw.githubusercontent.com/Samina2004/FinPilot-AI/main/WhatsApp%20Image%202026-07-05%20at%209.29.56%20AM%20(1).jpeg" width="220"> | <img src="https://raw.githubusercontent.com/Samina2004/FinPilot-AI/main/WhatsApp%20Image%202026-07-05%20at%209.46.14%20AM.jpeg" width="220"> | <img src="https://raw.githubusercontent.com/Samina2004/FinPilot-AI/main/WhatsApp%20Image%202026-07-05%20at%209.46.11%20AM.jpeg" width="220"> | <img src="https://raw.githubusercontent.com/Samina2004/FinPilot-AI/main/WhatsApp%20Image%202026-07-05%20at%209.46.12%20AM.jpeg" width="220"> |

---

## ⚙️ **Getting Started**

### Prerequisites
*   Android Studio (Ladybug or newer stable variant)
*   Android SDK 26+ (Targeting Android 15, SDK 35)
*   A Google Gemini API Key — [Get your API Key here](https://aistudio.google.com/)
*   A Firebase Project configuration file (`google-services.json`)

### Local Installation & Setup Steps
1. Clone this project repository via your terminal:
   ```bash
   git clone [https://github.com/Samina2004/FinPilot-AI.git](https://github.com/Samina2004/FinPilot-AI.git)
Setup your Firebase credentials:

Download your google-services.json from the Firebase Console.

Paste the file directly inside the app/ directory.

Secure your Gemini API Key:

Create a file named local.properties in your root project directory (if it doesn't already exist).

Append your credentials as follows:

**Properties**
GEMINI_API_KEY=your_actual_gemini_api_key_here
Open the source project structure inside Android Studio, let Gradle sync complete, and execute on your target device (Shift + F10).

👩‍💻** Author**
Samina Nawaz

Native Android Developer | BS Information Technology, University of Sargodha

📄** License**
This repository is developed as a premium personal portfolio project showcasing advanced Jetpack Compose and Google AI implementations. Feel free to explore, branch, or utilize code segments for learning objectives.
