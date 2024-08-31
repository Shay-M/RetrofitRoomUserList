# Android User List App

This repository contains an Android application built using the MVVM architecture, Retrofit for API calls, and Room for local data storage. The app displays a list of users fetched from a server, allows adding, updating, and deleting users, and handles data persistence and offline scenarios.
You can find the APK file [here](https://github.com/Shay-M/RetrofitRoomUserList/blob/master/app/release/app-release.apk).

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
- [Assumptions and Challenges](#assumptions-and-challenges)
- [Code Quality](#code-quality)
- [License](#license)

## Features
- Fetch users from a REST API and display them in a list.
- Add, update, and delete users with changes persisted locally using Room.
- Offline support with data fetched from the local database when there's no network connection.
- Automatically generate a user avatar based on the user's name if no image is provided.
- Smooth UI transitions and user-friendly design following Material Design guidelines.
- Pagination in RecyclerView to load users in batches.
- Image loading with Glide.

## Prerequisites
- **Android Studio**: Arctic Fox or later.
- **JDK**: Version 11 or later.
- **Gradle**: Version 7.0 or later.
- **Internet Connection**: Required for fetching user data.

## Installation
1. **Clone the Repository**:
    ```bash
    git clone https://github.com/Shay-M/RetrofitRoomUserList.git
    ```
2. **Open the Project in Android Studio**:
    - Launch Android Studio.
    - Open the cloned project from the directory where it was cloned.

3. **Sync Gradle**:
    - Let Android Studio sync the project and download all necessary dependencies.

## Building the Project
1. **Clean the Project**:
    - Go to `Build > Clean Project`.

2. **Build the Project**:
    - Go to `Build > Rebuild Project`.

3. **Check for Errors**:
    - Ensure that there are no errors after building the project. Warnings related to unused imports can be ignored if intentional.

## Running the Application
1. **Connect an Android Device** or **Start an Emulator**:
    - Make sure your device or emulator is connected and recognized by Android Studio.

2. **Run the Application**:
    - Click on the "Run" button or go to `Run > Run 'app'`.
    - Select the device/emulator to deploy the application.

3. **View Logs**:
    - Use Logcat to view application logs for debugging purposes.

## Assumptions and Challenges
- **Assumptions**:
    - The REST API provides data in a format compatible with the `User` model.
    - Users added locally without a server sync should persist only on the device.
    - If a user is added without an avatar, the app will generate an avatar image based on the user's name.

- **Challenges**:
    - Handling synchronization between local and remote data while ensuring no data loss.
    - Ensuring smooth UI transitions and handling edge cases such as no internet connection.

## Code Quality
- **Clean Architecture**: The app follows the MVVM architecture, separating concerns between UI, data handling, and business logic.
- **Best Practices**: The code adheres to best practices in Android development, including:
    - Proper use of `LiveData` and `ViewModel`.
    - Efficient use of `RecyclerView` with DiffUtil for list updates.
    - Organized package structure separating concerns.
- **Testing**: Unit tests and integration tests are included where applicable. Test coverage is focused on critical components like `ViewModel`, `Repository`, and data sources.


