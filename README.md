# ROOSTER 
[![Build Status](https://travis-ci.org/flamingchickens1540/ROOSTER.svg?branch=master)](https://travis-ci.org/flamingchickens1540/ROOSTER)
[![JitPack](https://jitpack.io/v/org.team1540/rooster.svg)](https://jitpack.io/#org.team1540/rooster)

Reusable Object-Oriented Systems, Templates, and Executables for Robots 🐓

A common library of useful classes and systems intended to be used for all Team 1540 robots.

## Using ROOSTER

### What's In The Jar

[Javadoc hosted on Github Pages](https://flamingchickens1540.github.io/ROOSTER)

#### Drive Pipeline System

`org.team1540.rooster.drive.pipeline`

A flexible system for controlling a robot drive. More docs [here](docs/Drive%20Pipelines.md), with a specific section on motion profiling [here](docs/Motion%20Profiling.md).

#### Preferences

`org.team1540.rooster.preferencemanager`

A system to easily set tuning fields through WPILib `Preferences`.

#### Triggers

`org.team1540.rooster.triggers`

Simple triggers that extend WPILib's joystick binding functionality. 

- `AxisButton` allows using a joystick axis (triggers or joysticks) as a button–the button will trigger when the axis passes a user-defined threshold.
- `DPadButton` and `StrictDPadButton` allow using any axis of a controller D-Pad as a button.

#### Utilities

`org.team1540.rooster.Utilities`

Functions and classes for common tasks.

- Deadzone processing
- Capping an output
- Inverting an input/output depending on a boolean


### Installation

Add the library by adding these lines in your `build.gradle` file:

```Gradle
repositories {
	// other repositories
     mavenCentral()
     maven { url 'https://jitpack.io' }
}

dependencies {
     // other dependencies
     compile 'org.team1540:rooster:master-SNAPSHOT'
}
```

Additionally, you should be using the latest version of GradleRIO with CTRE Phoenix, Kauai Labs NavX, and Pathfinder v1 vendor libraries installed.

We use [JitPack](https://jitpack.io) as a Gradle/Maven repository. This means that if you add the project using Gradle it will be automatically updated with the latest changes to the `master` branch, as well as source code and documentation .jar files.

Using `master-SNAPSHOT` as a version number is good for projects you're actively developing, but after you've finished it's better to anchor it to a specific version (simply change "`master-SNAPSHOT`" to the version number) to avoid possible backwards-compatibility issues.

If needed, you can build off of specific commits or branches. See the [JitPack page](https://jitpack.io/#org.team1540/rooster) for details.

_Note: If you need to use changes that have just been pushed to master, you may need to force Gradle to check for a new version instead of using an already-cached older version.  Open a terminal in your project and run `./gradlew build --refresh-dependencies`._

## Developing ROOSTER

### Project Structure

ROOSTER's code is divided into two segments: `main` (in `src/main`), containing main library code which is packed into distribution JARs and given to anyone who adds the library as a dependency, and `testbots` (in `src/testbots`), containing robot classes etc. for testing the components in `main`.  

### Building

We recommend using IntelliJ IDEA to develop ROOSTER. To import the project, on IntelliJ IDEA's main menu screen or from the `File > New` menu, select `Project from Version Control > GitHub`. Enter `https://github.com/flamingchickens1540/ROOSTER.git` as the Git Repository URL, and set the Parent Directory and Directory name functions according to your preference. The project should configure itself automatically.

Alternatively, the project can be built from the command line with the Gradle Wrapper:

```bash
./gradlew build
```

### Code Style

Team 1540 (and ROOSTER) uses [Google Java Style](https://google.github.io/styleguide/javaguide.html) for all code. Additionally, all new code should have proper nullability annotations on all public-facing parameters and return types. (`@NotNull` for parameters that must not be `null` or methods that never return `null`, `@Nullable` for the opposite.)
