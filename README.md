<div align="center">

  
  # CostCircle - Expense Sharing Made Simple
  
  **Smart group expense tracking, split logic, and premium financial reports.**

  ![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple?style=flat&logo=kotlin)
  ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-M3-4285F4?style=flat&logo=android)
  ![Platform](https://img.shields.io/badge/Platform-Android-green?style=flat&logo=android)
  ![License](https://img.shields.io/badge/License-MIT-orange?style=flat)

  <a href="https://play.google.com/store/apps/details?id=com.samkit.costcircle">
    <img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" height="80" />
  </a>
  
</div>

---

## 📱 About The App

**CostCircle** is a modern Android application built to simplify shared finances. Whether you're on a trip with friends, sharing an apartment, or managing a project budget, CostCircle ensures everyone pays their fair share.

It features a clean **Material Design 3** interface, real-time expense tracking, and powerful backend services to generate downloadable PDF financial reports.

## ✨ Key Features

* **👥 Group Management:** Create groups for trips, home, or events.
* **💸 Smart Expense Splitting:** Support for Equal, Percentage, and Exact amount splits.
* **📊 Premium Reports:** Download detailed **PDF Reports** containing spending charts, category breakdowns, and settlement history (Powered by Node.js & QuickChart).
* **⚖️ Automatic Settlements:** Algorithmically calculated "Who owes Who" to minimize transactions.
* **📈 Visual Analytics:** Interactive graphs showing spending trends.
* **🔔 Notifications:** Real-time updates for new expenses (via FCM).

## 📸 Screenshots

| Dashboard | Group Details | Add Expense | PDF Report |
|:---:|:---:|:---:|:---:|
| <img src="https://via.placeholder.com/300x600?text=Dashboard" width="200"/> | <img src="https://via.placeholder.com/300x600?text=Group+View" width="200"/> | <img src="https://via.placeholder.com/300x600?text=Add+Expense" width="200"/> | <img src="https://via.placeholder.com/300x600?text=PDF+Preview" width="200"/> |

## 🛠 Tech Stack

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material 3)
* **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture
* **Networking:** Retrofit2 & OkHttp3
* **Dependency Injection:** Hilt
* **Async:** Coroutines & Flow
* **Image Loading:** Coil
* **Navigation:** Compose Navigation
* **Local Storage:** DataStore Preferences (for Tokens)

## 🚀 Getting Started

### Prerequisites
* Android Studio Hedgehog or newer.
* JDK 17.
* Backend Server running (See [Backend Repo](../backend-repo-link)).

### Installation

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/sammkkit/costcircle-Frontend.git](https://github.com/sammkkit/CostCircle-Frontend.git)
    cd costcircle-android
    ```

2.  **Configure Base URL**
    Create a file named `local.properties` in the root directory (if not exists) and add your backend URL:
    ```properties
    # For Emulator (Standard)
    BASE_URL="[http://10.0.2.2:5000/api/](http://10.0.2.2:5000/api/)"
    
    # For Physical Device (Use your Laptop IP)
    # BASE_URL="[http://192.168.1.](http://192.168.1.)X:5000/api/"
    ```

3.  **Build and Run**
    Open the project in Android Studio and click the **Run** (▶) button.

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

## 📞 Contact
email : samkitjain430@gmail.com
