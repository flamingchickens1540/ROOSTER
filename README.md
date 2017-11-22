# Team 1540 Reference Libraries

A common library of useful classes and systems intended to be used for all Team 1540 robots.

## Installation

### Using Gradle
Add the library by adding these lines in your `build.gradle` file:

```Gradle
repositories {
     (other repos)
     maven { url 'https://jitpack.io' }
}

dependencies {
     (other dependencies)
     compile 'com.github.flamingchickens1540:reference-libraries:-SNAPSHOT'
}
```

We use [JitPack](https://jitpack.io) as a Gradle/Maven repository. This means that if you add the project using Gradle it will be automatically updated with the latest changes to the `master` branch, as well as source code and documentation .jar files. 

### Manually
Download [reference-libraries.jar](build/libs/reference-libraries.jar) and attach it to your project.

## Manifest

### PowerManager
- A flexible, dynamic power management system using `ChickenCommands` and `ChickenSubsystems`

### Adjustables 
`org.team1540.adjustables`
- A system for fields that need to be tuned on the fly.
- Simply annotate a field as `Tunable`, pass the enclosing class to the `AdjustableManager`, and call `AdjustableManager.update()` in your main robot loop to have your field show up and be editable on the SmartDashboard.

### Triggers
`org.team1540.triggers`
- Simple triggers that extend WPILib's joystick binding functionality. 
- `AxisButton` allows using a joystick axis (triggers or joysticks) as a button–the button will trigger when the axis passes a user-defined threshold.
- `DPadButton` allows using any axis of a controller D-Pad as a button.
