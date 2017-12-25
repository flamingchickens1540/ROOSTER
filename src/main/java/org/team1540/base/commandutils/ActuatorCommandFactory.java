package org.team1540.base.commandutils;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import org.team1540.base.templates.Actuator;
import org.team1540.base.triggers.VibrationManager;

/**
 * Factory class to create {@link Actuator}-related commands.
 * <p>
 * The {@code ActuatorCommandFactory} is used to produce actuator-related commands. It currently
 * has the capability to create two types of command: movement and joystick control.
 * <p>
 * To create either of these commands, you first need a subsystem that implements the {@link
 * Actuator} interface. Then, create an ActuatorCommandFactory, and set up its core options (such
 * as movement speed, limits, and its behavior when jammed) using setters. Finally, create one or
 * more
 * {@link Command Commands} using the command creation methods.
 * <p>
 * All setters in this class use a builder pattern; that is, they return an instance of the class
 * they are called on. This can be used to chain method calls easily by calling another setter
 * method on the return value of previous setter methods.
 * <p>
 * Commands prouced by this class are entirely independent; any subsequent changes to factory
 * parameters using the setter interfaces will not affect commands that have already been built.
 *
 * @see Actuator
 * @see Command
 */
public class ActuatorCommandFactory {
  private Actuator actuator;
  private double currentLimit;
  private double endTolerance = -1;
  private double lowerLim;
  private int minConsecutiveSpikeTicks;
  private double speed;
  private double upperLim;

  /**
   * Creates a {@link Command} that controls the provided {@link Actuator} with a joystick.
   *
   * @param joystick The joystick to use when controlling the actuator.
   * @param axis The axis ID to use for control.
   */
  public Command createJoystickControlCommand(Joystick joystick, int axis) {
    return new JoystickControlCommand(actuator, joystick, axis, speed, upperLim, lowerLim,
        minConsecutiveSpikeTicks, currentLimit);
  }

  /**
   * Creates a {@link Command} that moves the actuator to the provided position using the values set
   * with setters beforehand.
   * <p>
   * The returned command functions by a fairly simple method. Every tick (which occur every 20ms or
   * so) it adds the speed to the position that it's commanding the PID to to go to. This results in
   * a smooth movement as long as speed is kept relatively low (a low fraction of the motor's
   * maximum speed) so that the change in commanded position does not outstrip the change in actual
   * position.
   *
   * @param position The position to move to.
   *
   * @return A {@link Command} that moves the actuator to the provided position.
   */
  public Command createMoveToPositionCommand(double position) {
    return new MoveToPositionCommand(actuator, position, endTolerance, speed, currentLimit,
        minConsecutiveSpikeTicks);
  }

  /**
   * Gets the actuator used for all commands produced by this {@code ActuatorCommandFactory}.
   *
   * @return The {@link Actuator} used, or {@code null} if none is set.
   */
  public Actuator getActuator() {
    return actuator;
  }

  /**
   * Sets the {@link Actuator} used for all commands produced by this {@code
   * ActuatorCommandFactory}.
   *
   * @param actuator The actuator to use.
   *
   * @return An instance of this {@code ActuatorCommandFactory} in a builder pattern.
   */
  public ActuatorCommandFactory setActuator(Actuator actuator) {
    this.actuator = actuator;
    return this;
  }

  /**
   * Gets the motor current limit above which the arm will stop its movement.
   *
   * @return The motor current limit, in amps.
   */
  public double getCurrentLimit() {
    return currentLimit;
  }

  /**
   * Sets the motor current limit above which the arm will stop its movement.
   *
   * @param currentLimit The current limit to set.
   *
   * @return An instance of this {@code ActuatorCommandFactory} in a builder pattern.
   */
  public ActuatorCommandFactory setCurrentLimit(double currentLimit) {
    this.currentLimit = currentLimit;
    return this;
  }

  /**
   * Gets the end tolerance for commands produced by this {@code ActuatorCommandFactory}. When the
   * position error of a command is lower than the end tolerance, the command will stop. If the
   * end tolerance is a negative number, the command will work continuously.
   *
   * @return The end tolerance.
   */
  public double getEndTolerance() {
    return endTolerance;
  }

  /**
   * Gets the end tolerance for commands produced by this {@code ActuatorCommandFactory}. When the
   * position error of a command is lower than the end tolerance, the command will stop. To disable
   * commands stopping when this occurs, set this to a negative number.
   *
   * @param endTolerance The tolerance to use.
   *
   * @return An instance of this {@code ActuatorCommandFactory} in a builder pattern.
   */
  public ActuatorCommandFactory setEndTolerance(double endTolerance) {
    this.endTolerance = endTolerance;
    return this;
  }

  /**
   * Gets the lower soft limit of this {@code ActuatorCommandFactory}. When, during joystick
   * control, the arm exceeds this limit, it will vibrate the controlling joystick briefly.
   *
   * @return The lower limit.
   */
  public double getLowerLim() {
    return lowerLim;
  }

  /**
   * Sets the lower soft limit of this {@code ActuatorCommandFactory}. When, during joystick
   * control, the arm exceeds this limit, it will vibrate the controlling joystick briefly.
   *
   * @return An instance of this {@code ActuatorCommandFactory} in a builder pattern.
   */
  public ActuatorCommandFactory setLowerLim(double lowerLim) {
    this.lowerLim = lowerLim;
    return this;
  }

  /**
   * Gets the minimum consecutive spike ticks of this {@code ActuatorCommandFactory}. When the
   * current has been spiking (i.e. is above the current limit set with {@link
   * #setCurrentLimit(double) setCurrentLimit()}) for more than this number of ticks consecutively
   * (or about this value * 20ms), it will stop arm movement.
   *
   * @return The minimum consecutive spike ticks.
   */
  public int getMinConsecutiveSpikeTicks() {
    return minConsecutiveSpikeTicks;
  }

  /**
   * Sets the minimum consecutive spike ticks of this {@code ActuatorCommandFactory}. When the
   * current has been spiking (i.e. is above the current limit set with {@link
   * #setCurrentLimit(double) setCurrentLimit()}) for more than this number of ticks consecutively
   * (or about this value * 20ms), it will stop arm movement.
   *
   * @param minConsecutiveSpikeTicks The minimum consecutive spike ticks to set.
   *
   * @return An instance of this {@code ActuatorCommandFactory} in a builder pattern.
   */
  public ActuatorCommandFactory setMinConsecutiveSpikeTicks(int minConsecutiveSpikeTicks) {
    this.minConsecutiveSpikeTicks = minConsecutiveSpikeTicks;
    return this;
  }

  /**
   * Gets the movement speed of this {@code ActuatorCommandFactory}. The speed is how much the arm
   * will move in one tick, about 20 milliseconds, either during a movement command or when the
   * joystick is maxed out in either direction.
   *
   * @return The movement speed for commands produced by this {@code ActuatorCommandFactory}.
   */
  public double getSpeed() {
    return speed;
  }

  /**
   * Gets the movement speed of this {@code ActuatorCommandFactory}. The speed is how much the arm
   * will move in one tick, about 20 milliseconds, either during a movement command or when the
   * joystick is maxed out in either direction. As such, in most cases, it should be a very low
   * number.
   *
   * @param speed The speed to set.
   *
   * @return An instance of this {@code ActuatorCommandFactory}.
   */
  public ActuatorCommandFactory setSpeed(double speed) {
    this.speed = speed;
    return this;
  }

  /**
   * Gets the upper soft limit of this {@code ActuatorCommandFactory}. When, during joystick
   * control, the arm exceeds this limit, it will vibrate the controlling joystick briefly.
   *
   * @return The upper limit.
   */
  public double getUpperLim() {
    return upperLim;
  }

  /**
   * Gets the upper soft limit of this {@code ActuatorCommandFactory}. When, during joystick
   * control, the arm exceeds this limit, it will vibrate the controlling joystick briefly.
   *
   * @return The upper limit.
   */
  public ActuatorCommandFactory setUpperLim(double upperLim) {
    this.upperLim = upperLim;
    return this;
  }

  private static class JoystickControlCommand extends Command {
    final Actuator actuator;
    final int axis;
    int consecutiveSpikeTicks;
    final double currentLimit;
    final double lowerLim;
    final int minConsecutiveSpikeTicks;
    final double multiplier;
    final Joystick stick;
    final double upperLim;
    final Command vibrationCommand;
    double position;

    private JoystickControlCommand(Actuator actuator, Joystick joystick,
        int joystickAxis, double multiplier, double upperLim, double lowerLim,
        int minConsecutiveSpikeTicks, double currentLimit) {
      super("Control " + actuator.getSubsystem().getName() + " with joysticks");
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
  }

  private static class MoveToPositionCommand extends Command {
    final Actuator actuator;
    final double currentLimit;
    final double endPos;
    final int minConsecutiveSpikeTicks;
    final double speed;
    final double tolerance;
    double commandedPos;
    int consecutiveSpikeTicks;

    private MoveToPositionCommand(Actuator actuator, double endPos, double tolerance, double speed,
        double currentLimit, int minConsecutiveSpikeTicks) {
      super("Move " + actuator.getSubsystem().getName() + " to " + endPos + " at " + speed);
      requires(actuator.getSubsystem());

      this.actuator = actuator;
      this.endPos = endPos;
      this.tolerance = tolerance;
      this.speed = speed;
      this.minConsecutiveSpikeTicks = minConsecutiveSpikeTicks;
      this.currentLimit = currentLimit;
    }

    @Override
    protected void initialize() {
      commandedPos = actuator.getPosition();
      consecutiveSpikeTicks = 0;
    }

    @Override
    protected void execute() {
      if (actuator.getCurrent() > currentLimit) {
        // current is spiking
        consecutiveSpikeTicks++;
      } else {
        consecutiveSpikeTicks = 0;
      }

      if (consecutiveSpikeTicks > minConsecutiveSpikeTicks) {
        // current has been spiking long enough to stop moving
        commandedPos = actuator.getPosition();
      } else {
        commandedPos += signum(endPos - commandedPos) * speed;
      }
      actuator.setPosition(commandedPos);
    }

    @Override
    protected boolean isFinished() {
      return abs(actuator.getPosition() - endPos) < tolerance;
    }
  }
}
