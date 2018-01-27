# ROOSTER 
[![Build Status](https://travis-ci.org/flamingchickens1540/ROOSTER.svg?branch=master)](https://travis-ci.org/flamingchickens1540/ROOSTER)
[![JitPack](https://jitpack.io/v/org.team1540/rooster.svg)](https://jitpack.io/#org.team1540/rooster)

Reusable Object-Oriented Systems, Templates, and Executables for Robots 🐓

A common library of useful classes and systems intended to be used for all Team 1540 robots.

## Using ROOSTER

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

Using `master-SNAPSHOT` as a version number is good for projects you're actively developing, but after you've finished it's better to anchor it to a specific version (simply change "`master-SNAPSHOT`" to the version number) to avoid possible backwards-compatibility issues.

If needed, you can build off of specific commits or branches. See the [JitPack page](https://jitpack.io/#org.team1540/rooster) for details.

_Note: If you need to use changes that have just been pushed to master, you may need to force Gradle to check for a new version instead of using an already-cached older version.  Open a terminal in your project and run `./gradlew build --refresh-dependencies`._

### Manually

Download the latest version from the [releases page](https://github.com/flamingchickens1540/ROOSTER/releases) and attach it to your project.

## Manifest

### Power Management
- A flexible, dynamic power management system. Uses a centralized `PowerManager` that takes `PowerManageable`s, including the default implementation `ChickenSubsystem`.

### Adjustables 
`org.team1540.adjustables`
- A system for fields that need to be tuned on the fly.
- Simply annotate a field as `Tunable`, pass the enclosing class to the `AdjustableManager`, and call `AdjustableManager.update()` in your main robot loop to have your field show up and be editable on the SmartDashboard and Shuffleboard.

### Triggers
`org.team1540.triggers`
- Simple triggers that extend WPILib's joystick binding functionality. 
- `AxisButton` allows using a joystick axis (triggers or joysticks) as a button–the button will trigger when the axis passes a user-defined threshold.
- `DPadButton` allows using any axis of a controller D-Pad as a button.

## Building

We recommend using IntelliJ IDEA to develop ROOSTER. To import the project, on IntelliJ IDEA's main menu screen or from the `File > New` menu, select `Project from Version Control > GitHub`. Enter `https://github.com/flamingchickens1540/ROOSTER.git` as the Git Repository URL, and set the Parent Directory and Directory name functions according to your preference. The project should configure itself automatically.
