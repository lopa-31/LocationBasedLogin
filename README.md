# Location-Based Login

This is a native Android application that automatically manages user login status based on their geographical proximity to a predefined office location. The app utilizes background location tracking to determine if a user is within an 80-meter radius of the office, granting access and automatically logging them out when they leave the area.

## Tech Stack & Architecture

- **Language**: Kotlin
- **Architecture**: Model-View-ViewModel (MVVM)
- **Asynchronous Programming**: Kotlin Coroutines
- **UI**: Android SDK, Material Components, View Binding
- **Android Jetpack**:
    - **ViewModel**: Manages UI-related data in a lifecycle-conscious way.
    - **LiveData**: Notifies views of data changes.
    - **Navigation Component**: Handles in-app navigation between screens.
- **Location**: Google Play Services (`FusedLocationProviderClient`) for efficient location tracking.

## Project Setup

### Prerequisites
- Android Studio Iguana | 2023.2.1 or newer.
- An Android device or an emulator with Google Play Services.

### Instructions

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/LocationBasedLogin.git
    ```
2.  **Open in Android Studio:**
    - Open Android Studio.
    - Click on `File` > `Open`.
    - Navigate to the cloned repository folder and select it.
3.  **Gradle Sync:**
    - Android Studio will automatically start building the project and downloading the required dependencies using Gradle. This might take a few minutes.
4.  **Run the application:**
    - Connect your Android device or start an emulator.
    - Click the 'Run' button (▶️) in Android Studio.
    - The app will request the following permissions at runtime. Please grant them to use the app:
        - **Precise Location**: Essential for determining your position.
        - **Notifications**: Required for the foreground service that tracks location in the background.
        - **Background Location Access**: For the core feature to work, please select **"Allow all the time"** when prompted.