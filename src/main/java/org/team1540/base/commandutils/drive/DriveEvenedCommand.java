package org.team1540.base.commandutils.drive;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.templates.Drive;

class DriveEvenedCommand extends Command {
  /**
   * How much confidence will increase or decrease per tick.
   */
  private final double confidenceCoeff;
  /**
   * How much the setpoint will decrease per tick when PID error is high.
   */
  private final double decreaseCoeff;
  private final Drive drive;
  /**
   * How much the requested speed will increase per tick when PID error is low.
   */
  private final double increaseCoeff;
  private final Joystick joystick;
  // TODO: Support triggers to move forward and backward
  private final int leftAxis;
  /**
   * Error above which the drivetrain will decrease the requested speed.
   */
  private final double maxError;
  private final double minConfidence;
  private final int rightAxis;
  /**
   * A measure of how confident the system is that it's got the PID setpoints in the right
   * place. If confidence is below a certain value it will revert to tank drive when not in
   * conditions suitable for setpoint calibration.
   */
  private double confidence;
  private double setpoint;

  public DriveEvenedCommand(Drive drive, Joystick joystick, int leftAxis, int rightAxis,
      double increaseCoeff, double decreaseCoeff, double maxError, double confidenceCoeff,
      double minConfidence) {

    requires(drive.getAttachedSubsystem());

    this.minConfidence = minConfidence;
    this.confidenceCoeff = confidenceCoeff;
    this.drive = drive;
    this.joystick = joystick;
    this.leftAxis = leftAxis;
    this.rightAxis = rightAxis;
    this.increaseCoeff = increaseCoeff;
    this.decreaseCoeff = decreaseCoeff;
    this.maxError = maxError;
  }

  @Override
  protected void initialize() {
    setpoint = 0;
  }

  @Override
  protected void execute() {
    /*
    This is a sort of bang-bang control. It could theoretically be transferred to a PID, I guess,
    but then you're controlling the behavior of a PID using a PID and at a certain point
    it's time to stop.
    */
    // TODO: actually implement the algorithm
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}
