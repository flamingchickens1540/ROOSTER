package org.team1540.base;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.command.Subsystem;
import java.util.HashSet;
import java.util.Set;
import org.team1540.base.power.PowerManageable;


/**
 * Simple implementation of core {@link Subsystem} related interfaces. Makes it quick and easy to
 * build a basic robot.
 */
public class ChickenSubsystem extends Subsystem implements PowerManageable {

  private double priority = 0.0;
  private final Set<CANTalon> motors = new HashSet<CANTalon>();

  public ChickenSubsystem(String name) {
    super(name);
  }

  public ChickenSubsystem() {
  }

  protected void initDefaultCommand() {

  }

  public double getPriority() {
    return priority;
  }

  public void setPriority(double priority) {
    this.priority = priority;
  }

  public double getCurrent() {
    double sum = 0;
    for (CANTalon currentMotor : motors) {
      sum += currentMotor.getOutputCurrent();
    }
    return sum;
  }

  public synchronized void limitPower(double limit) {
    for (CANTalon currentMotor : motors) {
      currentMotor.EnableCurrentLimit(true);
      currentMotor.setCurrentLimit(Math.toIntExact(Math.round(limit / motors.size())));
    }
  }

  public synchronized void stopLimitingPower() {
    for (CANTalon currentMotor : motors) {
      currentMotor.EnableCurrentLimit(false);
    }
  }
}
