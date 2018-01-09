package org.team1540.base.testing;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class HelloWorldTestRobot extends IterativeRobot {
  @Override
  public void robotInit() {
    String debugString = "debug";
    SmartDashboard.putString("Hello: ", "World!");
    System.out.println("Hello World!");
  }
}