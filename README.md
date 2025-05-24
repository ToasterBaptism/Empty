# Controller Tuner for Android

A comprehensive Android application for configuring and tuning game controllers with advanced customization options.

## Features

- **Sensitivity Control**: Adjust controller sensitivity with precision sliders
- **Dead Zone Configuration**: Customize dead zones for analog sticks and triggers
- **Acceleration Curves**: Configure acceleration profiles for enhanced control
- **Right Stick Tuning**: Specialized acceleration settings for right analog stick
- **Profile Management**: Save, load, and manage multiple controller configurations
- **Real-time Preview**: Test settings in real-time with visual feedback
- **Multi-Controller Support**: Compatible with various Android-supported controllers

## Architecture

### Core Components

1. **User Interface**: Modern Android UI with Material Design components
2. **Controller Input Module**: Handles raw input from various controllers
3. **Mapping Engine**: Processes input with user-defined configurations
4. **Profile Manager**: Manages configuration persistence and retrieval
5. **Android Input System**: Integration with Android's input framework

### Technical Stack

- **Language**: Java
- **Build System**: Gradle
- **Target SDK**: Android API 33+
- **Minimum SDK**: Android API 21 (Android 5.0)
- **Architecture**: MVVM with Repository pattern

## Development Tools & Libraries

### Core Development
- Android SDK
- Android Studio / IntelliJ IDEA
- Gradle Build Tool
- Java Development Kit (JDK 11+)

### Android Libraries
- AndroidX Core
- AndroidX AppCompat
- Material Design Components
- ConstraintLayout
- RecyclerView
- ViewPager2
- Navigation Component

### Input Handling
- Android Input Framework
- GameController API
- MotionEvent Processing
- KeyEvent Handling

### Data Persistence
- SharedPreferences
- Room Database (for complex profiles)
- JSON serialization

### Testing & Development
- JUnit for unit testing
- Espresso for UI testing
- Mockito for mocking
- Android Debug Bridge (ADB)

### Termux Development Support
- Termux API for Android development
- Git for version control
- Gradle wrapper for builds
- Android SDK tools

## Building the App

### Prerequisites
1. Install Android SDK
2. Set up Java Development Kit
3. Configure Gradle

### Build Commands
```bash
# Clean build
./gradlew clean

# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install on device
./gradlew installDebug
```

## Usage

1. **Connect Controller**: Pair your controller with the Android device
2. **Launch App**: Open Controller Tuner from your app drawer
3. **Select Profile**: Choose an existing profile or create a new one
4. **Configure Settings**: Adjust sensitivity, dead zones, and acceleration
5. **Test Settings**: Use the real-time preview to test your configuration
6. **Save Profile**: Save your settings for future use

## Supported Controllers

- Xbox Controllers (wired/wireless)
- PlayStation Controllers (DualShock/DualSense)
- Generic HID controllers
- Bluetooth game controllers
- USB OTG controllers

## License

This project is licensed under the MIT License - see the LICENSE file for details.