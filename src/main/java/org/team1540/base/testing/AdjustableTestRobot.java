package org.team1540.base.testing;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Solenoid;
import org.team1540.base.adjustables.AdjustableManager;
import org.team1540.base.adjustables.Telemetry;
import org.team1540.base.adjustables.Tunable;
import org.team1540.base.power.PowerManager;

public class AdjustableTestRobot extends IterativeRobot {
  @Tunable("A boolean")
  public boolean b;
  @Tunable("A String")
  public String string = "String";

  @Tunable("A double")
  public double d = 2;
  @Telemetry("Double times 2 is")
  public double dTimes2;

  @Tunable("An int")
  public int i = 2;
  @Telemetry("Int times 2 is")
  public int iTimes2;

  @Telemetry("String plus \"Chickens\" is ")
  public String stringPlusChickens = string + " Chickens";
  @Telemetry("NOT(boolean) is")
  public boolean notB;

  @Telemetry("Solenoid")
  public Sendable sendable = new Solenoid(1);

  @Override
  public void robotInit() {
    AdjustableManager.getInstance().add(this);
    PowerManager.getInstance().interrupt();
  }

  @Override
  public void robotPeriodic() {
    dTimes2 = d * 2;
    iTimes2 = i * 2;
    stringPlusChickens = string + "Chickens";
    notB = !b;

    AdjustableManager.getInstance().update();
  }
}
