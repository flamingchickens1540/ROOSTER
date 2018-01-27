package org.team1540.base.commandutils.actuator;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.templates.Actuator;
import org.team1540.base.triggers.VibrationManager;

public class ActuatorJoystickControlCommand extends Command {

  private final Actuator actuator;
  private final int axis;
  private final double currentLimit;
  private final double lowerLim;
  private final int minConsecutiveSpikeTicks;
  private final double multiplier;
  private final Joystick stick;
  private final double upperLim;
  private final Command vibrationCommand;
  private int consecutiveSpikeTicks;
  private double position;

  ActuatorJoystickControlCommand(Actuator actuator, Joystick joystick,
      int joystickAxis, double multiplier, double upperLim, double lowerLim,
      int minConsecutiveSpikeTicks, double currentLimit) {
    super("Control " + actuator.getAttachedSubsystem().getName() + " with joysticks");
    requires(actuator.getAttachedSubsystem());
    this.actuator = actuator;
    this.stick = joystick;
    this.axis = joystickAxis;
    this.multiplier = multiplier;
    this.lowerLim = lowerLim;
    this.upperLim = upperLim;
    this.minConsecutiveSpikeTicks = minConsecutiveSpikeTicks;
    this.consecutiveSpikeTicks = 0;
    this.currentLimit = currentLimit;
    vibrationCommand = VibrationManager.getVibrationCommand(joystick, 0.25, 1);
  }

  @Override
  protected void initialize() {
    position = actuator.getPosition();
  }

  @Override
  protected void execute() {
    // where the operator is telling the mechanism to go
    double requestedPos = position + (stick.getRawAxis(axis) * multiplier);
    double commandedPos;

    // do current limit processing
    if (actuator.getCurrent() > currentLimit) {
      // current is spiking
      consecutiveSpikeTicks++;
    } else {
      consecutiveSpikeTicks = 0;
    }

    if (consecutiveSpikeTicks > minConsecutiveSpikeTicks) {
      commandedPos = actuator.getPosition();
    } else {
      commandedPos = requestedPos;

      // where it's actually going to go (skipped if limits are disabled)
      commandedPos = commandedPos < upperLim ? commandedPos : upperLim - multiplier;
      commandedPos = commandedPos >= lowerLim ? commandedPos : lowerLim + multiplier;
    }
    position = commandedPos;
    actuator.setPosition(position);
    // if where it's actually going is different than where it was told to go, complain
    if (requestedPos != commandedPos) {
      vibrationCommand.start();
    }
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  public Actuator getActuator() {
    return actuator;
  }

  public int getAxis() {
    return axis;
  }

  public double getCurrentLimit() {
    return currentLimit;
  }

  public double getLowerLim() {
    return lowerLim;
  }

  public int getMinConsecutiveSpikeTicks() {
    return minConsecutiveSpikeTicks;
  }

  public double getMultiplier() {
    return multiplier;
  }

  public Joystick getStick() {
    return stick;
  }

  public double getUpperLim() {
    return upperLim;
  }

  public Command getVibrationCommand() {
    return vibrationCommand;
  }

  public int getConsecutiveSpikeTicks() {
    return consecutiveSpikeTicks;
  }

  public void setConsecutiveSpikeTicks(int consecutiveSpikeTicks) {
    this.consecutiveSpikeTicks = consecutiveSpikeTicks;
  }

  public double getPosition() {
    return position;
  }

  public void setPosition(double position) {
    this.position = position;
  }
}
