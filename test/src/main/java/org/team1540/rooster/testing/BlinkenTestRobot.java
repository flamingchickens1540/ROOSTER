package org.team1540.rooster.testing;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.rooster.wrappers.RevBlinken;
import org.team1540.rooster.wrappers.RevBlinken.ColorPattern;

public class BlinkenTestRobot extends IterativeRobot {

  private SendableChooser<ColorPattern> patternChooser = new SendableChooser<>();
  private int currentID = -1;
  private RevBlinken blinken;

  @Override
  public void robotInit() {
    boolean defaultSet = false;
    for (ColorPattern pattern : ColorPattern.values()) {
      if (!defaultSet) {
        patternChooser.addDefault(pattern.name(), pattern);
        defaultSet = true;
      } else {
        patternChooser.addObject(pattern.name(), pattern);
      }
    }

    SmartDashboard.putData("Pattern", patternChooser);
    SmartDashboard.putNumber("Spark ID", 0);
    SmartDashboard.putNumber("Current PWM Output", 0);
  }

  @Override
  public void robotPeriodic() {
    int newID = (int) SmartDashboard.getNumber("Spark ID", currentID);
    if (newID != currentID || blinken == null) {
      if (blinken != null) {
        blinken.free();
      }

      blinken = new RevBlinken(newID);
      currentID = newID;
    }
  }

  @Override
  public void teleopPeriodic() {
    blinken.set(patternChooser.getSelected());
    SmartDashboard.putNumber("Current PWM Output", blinken.get());
  }
}
