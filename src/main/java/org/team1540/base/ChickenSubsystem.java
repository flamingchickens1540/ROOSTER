package org.team1540.base;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.command.Subsystem;
import java.util.HashSet;
import java.util.Set;
import org.team1540.base.power.PowerManageable;


public class ChickenSubsystem extends Subsystem implements PowerManageable {

  private double priority = 0.0;
  private Set<CANTalon> motors = new HashSet<CANTalon>();

  public ChickenSubsystem(String name) {
    super(name);
  }

  public ChickenSubsystem() {
  }

  @Override
  protected void initDefaultCommand() {

  }

  /**
   * Compare two ChickenCommands by priority.
   *
   * @param o ChickenCommand to compare to.
   *
   * @return priority.compareTo(o.priority)
   */
  public int compareTo(ChickenSubsystem o) {
    return Double.compare((priority), o.priority);
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

  public void limitPower(double limit) {
    for (CANTalon currentMotor : motors) {
      currentMotor.EnableCurrentLimit(true);
      currentMotor.setCurrentLimit(Math.toIntExact(Math.round(limit)));
    }
  }

  @Override
  public void stopLimitingPower() {
    for (CANTalon currentMotor : motors) {
      currentMotor.EnableCurrentLimit(false);
    }
  }

  /**
   * Compare two PowerManageables by priority.
   *
   * @param o PowerManageables to compare to.
   * @return priority.compareTo(o.priority)
   */
  public int compareTo(PowerManageable o) {
    return Double.compare((priority), o.getPriority());
  }
}
