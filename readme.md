
# 🏘️ Apartment Rentals Mobile App

A secure, scalable, and cleanly architected Android app for renting apartments, built using **MVVM** and **CLEAN Architecture** principles.



## 🔧 Features Implemented

- ✅ User Authentication via **Firebase Auth**
  - Email/Password sign up & sign in
  - Role-based access (Realtor / Regular User)
- ✅ Secure Token Management using **EncryptedSharedPreferences**
- ✅ Role-Based Navigation:
  - **Realtor:** CRUD their own apartment listings
  - **Regular User:** Browse & Filter apartments
- ✅ Offline support using **Room**
  - Apartments cached locally for offline browsing
- ✅ Apartment Details:
  - Image, Area Size, Rooms, Price, Description
- ✅ Internet connectivity detection with automatic updates
- ✅ Input validation and graceful error handling
- ✅ Dynamic theming (Light/Dark mode)
- ✅ Loggin session management using shared preferences to avoid loggin every time

---

## 🗂 Architecture

Built with:
- **MVVM (Model-View-ViewModel)**
- **CLEAN Architecture**
- **Repository Pattern**
- **Loose Coupling for Better Scalability & Maintainability**

---

## 🔐 Security

- ✅ **EncryptedSharedPreferences** used to store sensitive user data securely.
- ✅ Authentication token is securely saved
- ✅ Role-based restrictions enforced (Realtors cannot edit others’ listings).

---

## 🚫 Firebase Storage Note

Apartment preview images are loaded from local storage paths (on-device), as Firebase Storage began charging fees during development. You can easily swap this with Firebase Storage or another service if needed in production.
---

## 📦 Tech Stack

- Kotlin
- Firebase Auth
- Room DB (for caching)
- Material 3 (Jetpack Compose compatible UI components)
- EncryptedSharedPreferences
- Firebase (for optional storage, replaced due to pricing)

---

## 📱 App Flow

- On first launch: Users can register or login.
- After login:
  - **Realtor →** Navigates to admin page
  - **User →** Navigates to Browse screen
- Apartments are fetched from the backend and cached locally.
- Offline users can still view previously loaded listings.

---

## 📎 Future Improvements

- Add location-based filtering
- Add Push Notifications

---

## 🧪 Edge Case Handling

- Invalid input fields
- No internet connection (fallback to local cache)
- Unauthorized actions (realtor deleting others' listings, etc.)
