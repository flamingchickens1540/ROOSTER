# Preference System

The Preference system, located in the `org.team1540.base.preferencemanager` package, allows persistent and non-persistent robot preferences (PID coefficients, setpoints, and other such values) to be accessed with ease.

## Example

```java
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.team1540.base.preferencemanager.Preference;
import org.team1540.base.preferencemanager.PreferenceManager;

public class Robot extends IterativeRobot {
    @Preference("A boolean")
    public boolean b;
    @Preference("A String")
    public String string = "String";
    @Preference("A double")
    public double d = 2;
    @Preference("An int")
    public int i = 2;
    
    @Preference(value = "Non-Persistent int", persistent = false)
    public int velocity = 1;
    
    @Override
    public void robotInit() {
        PreferenceManager.getInstance().add(this);
    }
    
    @Override
    public void robotPeriodic() {
        Scheduler.getInstance().run();
    }
}
```

This outlines how to use the basic functionality of the manager. Variables annotated with [`@Preference`](https://flamingchickens1540.github.io/ROOSTER/index.html?org/team1540/base/triggers/SimpleButton.html) will be constantly (every time `Scheduler.getInstance().run()` is called) set with the latest values from the driver station. The label on the driver station will be the label provided as an argument to the annotation. The value of these variables will be saved to disk and restored on robot reboot. However, if you don't want that to happen, specify `persistent = false` in the annotation (due to Java rules you'll also need to add the `value = ` section as seen above). 

Preference fields must be `public`, can be static or non-static, and must be of type `boolean`, `String`, `double`, or `int`.

For fields marked with `@Preference` in a class to be recognized, an instance of that class must be passed to the [`PreferenceManager`](https://flamingchickens1540.github.io/ROOSTER/index.html?org/team1540/base/triggers/SimpleButton.html) using `PreferenceManager.getInstance().add()`. For static fields, any instance will do; for non-static fields, the specific instance of the class must be passed to the manager. If multiple instances of the same class are added to the `PreferenceManager`, due to labeling limitations, all tunable fields will contain the same values.

Just because variable values are updated doesn't mean that they're updated at the places they're used. For example, when Talon SRX/Victor SPX PID coefficients are updated, they must be re-set. 
