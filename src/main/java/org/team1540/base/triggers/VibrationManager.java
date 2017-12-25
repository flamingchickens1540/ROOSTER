package org.team1540.base.triggers;

import static edu.wpi.first.wpilibj.GenericHID.RumbleType.kLeftRumble;
import static edu.wpi.first.wpilibj.GenericHID.RumbleType.kRightRumble;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.command.TimedCommand;
import java.util.Map;
import java.util.TreeMap;

/**
 * A class to handle creating vibration commands for driveteam joysticks.
 * <p>
 * The main purpose of this class is to make life easier when you want to vibrate a joystick for
 * a given amount of time to signal that your grabber grabbed or your shifter shifted or your arm
 * is armed. Simply call {@link #getVibrationCommand(Joystick, double, double)
 * getVibrationCommand()}
 * to get a {@link Command} that you can call {@link Command#start() start()} on to vibrate the
 * joystick.
 */
public class VibrationManager {
  // could be more thread-safe but there's only one method that acesses this so we can just make it synchronized
  private static final Map<Integer, JoystickSubsystem> joysticks = new TreeMap<>();

  /**
   * Gets a {@link Command} that can be used to vibrate the joystick.
   *
   * @param joystick The joystick to vibrate.
   * @param time The vibration time, in seconds.
   * @param intensity The intensity of the vibration, from 0 to 1 inclusive.
   *
   * @return A {@link Command} that vibrates the given joystick for the given time at the given
   *     intensity.
   */
  public static synchronized Command getVibrationCommand(Joystick joystick, double time, double intensity) {
    if (!joysticks.containsKey(joystick.getPort())) {
      joysticks.put(joystick.getPort(), new JoystickSubsystem(joystick));
    }

    return new VibrateJoystickCommand(joysticks.get(joystick.getPort()), time, intensity);
  }

  /**
   * "Dummy" subsystem to prevent multiple vibration commands from running at the same time.
   * In the future, this could be made more advanced; Say command A vibrates the joystick at 0.25
   * and you start command B that vibrates the joystick at 0.5. Right now it would just vibrate
   * the joystick at 0.5 because of the command system, but you could theoretically have the
   * vibration actually be cumulative, i.e. the joystick would vibrate at 0.75.
   */
  private static class JoystickSubsystem extends Subsystem {
    Joystick joystick;

    JoystickSubsystem(Joystick joystick) {
      this.joystick = joystick;
    }

    @Override
    protected void initDefaultCommand() {}
  }

  private static class VibrateJoystickCommand extends TimedCommand {
    private Joystick joystick;
    private double intensity;

    public VibrateJoystickCommand(JoystickSubsystem joystick, double vibrationTime, double intensity) {
      super("Vibrate Joystick " + joystick.joystick.getPort(), vibrationTime);
      this.joystick = joystick.joystick;
      this.intensity = intensity;
      requires(joystick);
    }

    @Override
    protected void initialize() {
      joystick.setRumble(kLeftRumble, 1);
      joystick.setRumble(kRightRumble, 1);
    }

    @Override
    protected void end() {
      joystick.setRumble(kLeftRumble, 0);
      joystick.setRumble(kRightRumble, 0);
    }
  }
}