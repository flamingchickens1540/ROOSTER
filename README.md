[![Build Status](https://travis-ci.org/flamingchickens1540/ROOSTER.svg?branch=master)](https://travis-ci.org/flamingchickens1540/ROOSTER)
[![JitPack](https://jitpack.io/v/org.team1540/rooster.svg)](https://jitpack.io/#org.team1540/rooster)

# ROOSTER
Reusable Object-Oriented Systems, Templates, and Executables for Robots

A common library of useful classes and systems intended to be used for all Team 1540 robots.

## Installation

### Using Gradle
Add the library by adding these lines in your `build.gradle` file:

```Gradle
repositories {
     // other repositories
     maven { url 'https://jitpack.io' }
}

dependencies {
     // other dependencies
     compile 'org.team1540:rooster:master-SNAPSHOT'
}
```

We use [JitPack](https://jitpack.io) as a Gradle/Maven repository. This means that if you add the project using Gradle it will be automatically updated with the latest changes to the `master` branch, as well as source code and documentation .jar files. 

### Manually
Download the latest version from the [releases page](https://github.com/flamingchickens1540/ROOSTER/releases) and attach it to your project.

## Manifest

### PowerManager
- A flexible, dynamic power management system using `ChickenCommands` and `ChickenSubsystems`

### Adjustables 
`org.team1540.adjustables`
- A system for fields that need to be tuned on the fly.
- Simply annotate a field as `Tunable`, pass the enclosing class to the `AdjustableManager`, and call `AdjustableManager.update()` in your main robot loop to have your field show up and be editable on the SmartDashboard and Shuffleboard.

### Triggers
`org.team1540.triggers`
- Simple triggers that extend WPILib's joystick binding functionality. 
- `AxisButton` allows using a joystick axis (triggers or joysticks) as a button–the button will trigger when the axis passes a user-defined threshold.
- `DPadButton` allows using any axis of a controller D-Pad as a button.
