
package org.team1540.base.testing;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.team1540.base.power.PowerManager;
import org.team1540.base.preferencemanager.Preference;
import org.team1540.base.preferencemanager.PreferenceManager;

public class PreferencesTestRobot extends IterativeRobot {

  @Preference("A boolean")
  public boolean b;
  @Preference("A String")
  public String string = "String";
  @Preference("A double")
  public double d = 2;
  @Preference("An int")
  public int i = 2;

  // non-persistent
  @Preference(value = "An NP boolean", persistent = false)
  public boolean b2;
  @Preference(value = "An NP String", persistent = false)
  public String string2 = "String";
  @Preference(value = "An NP double", persistent = false)
  public double d2 = 2;
  @Preference(value = "An NP int", persistent = false)
  public int i2 = 2;

  @Override
  public void robotInit() {
    PreferenceManager.getInstance().add(this);
    PowerManager.getInstance().interrupt();
    new Notifier(() -> {
      System.out.println("Current boolean value: " + b);
      System.out.println("Current String value: " + string);
      System.out.println("Current double value: " + d);
      System.out.println("Current int value: " + i);
      System.out.println("Current NP boolean value: " + b2);
      System.out.println("Current NP String value: " + string2);
      System.out.println("Current NP double value: " + d2);
      System.out.println("Current NP int value: " + i2);
    }).startPeriodic(1);
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();
  }
}
