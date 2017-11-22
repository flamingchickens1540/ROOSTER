# Team 1540 Reference Libraries

A collection of useful classes intended to be used for all Team 1540 robots.

## Getting started

If you're using Gradle, you can add the library by adding the following lines in your `build.gradle` file.

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

Alternatively, you can manually attach reference-libraries.jar in build/libs to your project.

## Manifest

### PowerManager
- A flexible, dynamic power management system using ChickenCommands and ChickenSubsystems

### Adjustables
- A manager for fields that need to be tuned on the fly.
