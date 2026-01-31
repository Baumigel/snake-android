# Snake Game Android

A classic Snake game for Android with both touch/swipe and tilt controls.

## Features

- Classic snake gameplay
- **Touch/Swipe Controls** - Swipe in direction you want to move
- **Tilt Controls** - Tilt your phone to control the snake
- Score tracking
- Game over screen with tap-to-restart
- 30x30 pixel grid blocks
- Smooth gameplay with adjustable speed

## Controls

### Tilt Controls (Recommended)
- **Tilt phone right** - Move snake right
- **Tilt phone left** - Move snake left
- **Tilt phone up** - Move snake up
- **Tilt phone down** - Move snake down

### Touch/Swipe Controls
- **Swipe right** - Move snake right
- **Swipe left** - Move snake left
- **Swipe up** - Move snake up
- **Swipe down** - Move snake down

### Game Controls
- **Tap** - Restart after game over

## Game Rules

- Eat green food to grow and score +10 points
- Avoid hitting walls
- Don't run into yourself
- The snake gets longer as you eat food

## Requirements

- Android device with accelerometer (for tilt controls)
- Android Studio 2024.2.1+
- Android SDK 34+
- Java 21+
- Minimum SDK: 21 (Android 5.0 Lollipop)
- Target SDK: 34 (Android 14)

## Installation

### From Source

1. Clone the repository:
```bash
git clone https://github.com/Baumigel/snake-android.git
cd snake-android
```

2. Build the project:
```bash
./gradlew assembleDebug
```

3. Install on device:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

4. Launch the app:
```bash
adb shell am start -n com.example.snake/com.example.snake.MainActivity
```

## Project Structure

```
SnakeGame/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/snake/
│   │       │   ├── MainActivity.java  # Main activity
│   │       │   └── SnakeView.java    # Game view with logic
│   │       ├── res/
│   │       │   ├── layout/            # Layout resources
│   │       │   └── values/            # Value resources
│   │       └── AndroidManifest.xml    # App manifest
│   └── build.gradle                  # App-level Gradle config
├── build.gradle                      # Project-level Gradle config
└── settings.gradle                   # Gradle settings
```

## Building

### Debug Build

```bash
./gradlew assembleDebug
```

### Release Build

```bash
./gradlew assembleRelease
```

## APK Location

After building, the APK can be found at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Technical Details

### Game Components

- **SnakeView**: Custom SurfaceView handling game rendering and logic
- **SensorEventListener**: Implements accelerometer for tilt controls
- **SurfaceHolder**: Manages drawing surface for smooth graphics
- **Game Loop**: Runs on separate thread for consistent frame rate

### Configuration

- Block Size: 30x30 pixels
- Game Speed: 150ms per frame
- Tilt Sensitivity: 3.0 degrees
- Grid Size: Calculated based on screen resolution

## Screenshots

The game features:
- Black background
- Red snake body
- Green food squares
- White score display
- Game over screen with restart prompt

## License

This project is open source and available for educational purposes.

## Credits

Built with:
- Android SDK
- Java
- Gradle 8.2
- AndroidX AppCompat library
