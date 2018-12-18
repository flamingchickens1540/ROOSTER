# Motion Profiling with ROOSTER

This is a start-to-finish guide to implementing the robot side of motion profiling using ROOSTER. Generation is a separate matter (and not covered by ROOSTER at the moment)—this covers the steps after you've created motion profile CSV files and deployed them to a folder on the RoboRIO.

## Loading Profiles

Profiles can be pre-loaded from a designated folder on the RoboRIO using a `ProfileContainer`. A `ProfileContainer` automatically loads motion profiles from Pathfinder-style CSV files and keeps them in RAM until they are needed.

For an example, let's say that your folder structure is like so:

```
/home/lvuser/
    profiles/
        foo_left.csv
        foo_right.csv
        bar_left.csv
        bar_right.csv
```

To load these into two motion profiles named "foo" and "bar" (each containing left and right sides), use a `ProfileContainer` as follows:

```java
import edu.wpi.first.wpilibj.IterativeRobot;
import org.team1540.rooster.motionprofiling.ProfileContainer;

public class Robot extends IterativeRobot {
    public static final ProfileContainer profiles;
    
    @Override
    public void robotInit() {
        profiles = new ProfileContainer(new File("/home/lvuser/profiles"));
    }
}
```

You can then access the left and right profiles in a `Command` or anywhere else you might need them:

```java
import edu.wpi.first.wpilibj.command.Command;
import org.team1540.rooster.motionprofiling.ProfileContainer;
import org.team1540.rooster.motionprofiling.MotionProfile;

public class ProfileCommand extends Command {
    @Override
    protected void initialize() {
        ProfileContainer.DriveProfile profiles = Robot.profiles.get("foo");
        MotionProfile left = profiles.getLeft();
        MotionProfile right = profiles.getRight();
        
        // do something with the profiles
    }
    
    // isFinished, etc.
}
```

### Changing the Suffix

Let's say your profiles use a different naming scheme. For instance:

```
/home/lvuser/
    profiles/
        foo.left.csv
        foo.right.csv
        bar.left.csv
        bar.right.csv
```

The `ProfileContainer` supports differing file formats by specifying the suffixes in the constructor:

```java
import edu.wpi.first.wpilibj.IterativeRobot;
import org.team1540.rooster.motionprofiling.ProfileContainer;

public class Robot extends IterativeRobot {
    public static final ProfileContainer profiles;
    
    @Override
    public void robotInit() {
        profiles = new ProfileContainer(new File("/home/lvuser/profiles"), ".left.csv", ".right.csv");
    }
}
```

Once you have your left and right side profiles, you need to do something with them. Execution is a fairly complicated topic involving a number of different steps.

## Empirical Testing

The first step in accurate profile execution is an accurate model of your robot's behavior. While all of these quantities can be calculated theoretically, it is much better to do them empirically.

### Ticks Per Unit and Track Width Testing

Ticks per unit (TPU) is a number representing the correspondence between encoder ticks and real-world position of the wheels on the ground. While TPU can be theoretically calculated by simply dividing the wheel circumference by the number of ticks per rotation of the wheel, it is better measured empirically to account for tread wear and other factors.

Track width (also erroneously called "wheelbase" in certain libraries) is the distance between your robot's left and right wheels, and its theoretical calculation is even simpler—just measure the distance. However, wheelbase is very susceptible to effects from wheel-scrub and other factors (especially if you are using multiple sets of traction wheels). Note that this measurement is often fed into your generation solution, so you may want to go back and remake your paths.

Both track width and TPU can be tested with the `WheelbaseTestRobot`, in the `org.team1540.rooster.util.robots` package. `WheelbaseTestRobot` is a fully self-contained robot class that should be deployed onto your robot. To deploy the `WheelbaseTestRobot`, set your robot class to `org.team1540.rooster.util.robots.WheelbaseTestRobot` and deploy.

Once you deploy and connect a driver station, it will throw several values onto SmartDashboard or Shuffleboard. Set your left and right motor IDs and inversions as necessary, then run the Reset and Zero commands. 

#### Ticks Per Unit Testing

To measure TPU, push your robot some defined distance (for example, 10 feet) and note the LPOS and RPOS readouts on the SmartDashboard. (They should be near identical.) Then divide them by the distance your robot traveled to get your ticks per unit. For example, if you push your robot ten feet and measure LPOS and RPOS of about 4500, then your ticks per unit (in this case ticks per foot) would be 450.

#### Track Width Testing

Run the Zero command again, then enter your TPU value from the last step into the "encoderTPU" readout. Additionally, it is reccomended to set the "brake" readout to `true` and run the Reset command. Then, enable the robot (in tele-op mode) and hold down the A button on controller 0 until the robot completes ten full revolutions. Your empirically calculated wheelbase width (in whatever units you used to calculate your TPU) will then be placed in the "Calculated Width" readout.