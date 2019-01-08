package org.team1540.rooster.testing;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class HelloWorldTestRobot extends TimedRobot {

  @Override
  public void robotInit() {
    String debugString = "debug";
    SmartDashboard.putString("Hello: ", "World!");
    System.out.println("Hello World!");
  }
}
